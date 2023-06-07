package com.checkers.network.server;

import com.checkers.board.Field;
import com.checkers.board.Piece;
import com.checkers.network.Packet;
import com.checkers.network.PacketType;
import com.checkers.network.Player;
import com.checkers.network.PlayerID;
import com.checkers.utils.InvalidMoveException;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.checkers.network.moveValidating.*;
import static com.checkers.network.server.Server.*;
import static com.checkers.utils.ConsoleColors.*;

public class ClientHandler extends Thread {

    private Player player;

    public ClientHandler(PlayerID id) {
        this.player = players.get(id);
    }

    @Override
    public void run() {
        try {
            managePackets(player);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(player.id +" is unreachable. " + e.getMessage());
        }
    }

    private void managePackets(Player player) throws IOException, ClassNotFoundException {
        new Packet(PacketType.INIT, player.id, whoseTurn).sendTo(player.outputStream);
        sendStartingMessage();
        while (true) {
            try {
                Packet packet = Packet.receivePacket(player.inputStream);
                switch (packet.type){
                    case MOVE -> makeMove(packet);
                    case DISCONNECT -> playerDisconnect();
                }
            } catch (InvalidMoveException e) {
                System.out.println(RED.set("Illegal move. " + e.getMessage()));
                new Packet(PacketType.EXCEPTION, e.getMessage()).sendTo(player.outputStream);
            }
        }
    }

    private void sendStartingMessage() throws IOException {
        try {
            new Packet(PacketType.MESSAGE, "Connection established").sendTo(player.outputStream);
            new Packet(PacketType.MESSAGE, "Opponent joined the game").sendTo(getEnemy().outputStream);

        } catch (NoSuchElementException e){
            System.out.println(e.getMessage());
        }
    }

    private void playerDisconnect() throws IOException {
        if(players.size() == 2)
            new Packet(PacketType.FINISH_GAME).sendTo(getEnemy().outputStream);
        players.remove(player.id);
        throw new IOException(player.id+" left the game.");
    }

    private void makeMove(Packet packet) throws IOException {
        if (packet.playerID != whoseTurn) {
            System.out.println("It's "+whoseTurn+"'s turn");
            throw new InvalidMoveException("Wait for your turn!");
        }
        if(players.size()!=2)
            throw new InvalidMoveException("Opponent hasn't joined yet");

        switch (validatePacket(board, packet)) {
            case CAPTURE -> {
                validateCapture(board, packet);
                capturePiece(packet);
                movePiece(packet);
            }
            case MOVE -> {
                validateMove(board, packet);
                movePiece(packet);
            }
        }
        if (isInKingsRow(packet)) {
            sendPacketsToPlayers(new Packet(PacketType.PROMOTION, packet.destination));
            board.getField(packet.destination).getPiece().promote();
        }

        sendNextTurnPackets();
        nextTurn();
    }

    private void sendNextTurnPackets() throws IOException {
        if (player.getStartTime() == 0)
            player.initTimer();

        getEnemy().setStartTime();
        player.accumulateTime();

        Packet enemyPacket = new Packet(PacketType.PLAYER_TURN, player.id, getEnemy().getTimeAccumulated(), player.getTimeAccumulated());
        Packet playerPacket = new Packet(PacketType.ENEMY_TURN, player.id, player.getTimeAccumulated(), getEnemy().getTimeAccumulated());

        enemyPacket.sendTo(getEnemy().outputStream);
        playerPacket.sendTo(player.outputStream);
    }

    private boolean isInKingsRow(Packet p) {
        Piece piece = board.getField(p.destination).getPiece();
        if (piece.isKing())
            return false;
        if (p.destination.y == board.getBoardSize()-1 && p.playerID.equals(PlayerID.PLAYER1) || p.destination.y == 0 && p.playerID.equals(PlayerID.PLAYER2))
            return true;
        return false;
    }

    private void movePiece(Packet packet) throws IOException {
        board.movePiece(packet.source, packet.destination,false);
        sendPacketsToPlayers(packet);
    }

    private void capturePiece(Packet p) throws IOException {
        Field captureZone = board.getField(p.source.between(p.destination));
        captureZone.removePiece();
        sendPacketsToPlayers(new Packet(PacketType.CAPTURE, captureZone.position));
        player.checkersLost--;
    }

    private Player getEnemy() {
        if(players.size()==2)
            return players.get(player.id.getEnemy());
        else
            throw new NoSuchElementException("Opponent hasn't joined yet");
    }
}

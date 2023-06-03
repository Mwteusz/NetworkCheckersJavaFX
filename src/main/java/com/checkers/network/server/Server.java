package com.checkers.network.server;

import com.checkers.board.Board;
import com.checkers.network.Packet;
import com.checkers.network.PacketType;
import com.checkers.network.Player;
import com.checkers.network.PlayerID;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.TreeMap;

import static com.checkers.utils.ConsoleColors.YELLOW;


public class Server{

    static TreeMap<PlayerID, Player> players = new TreeMap<>();
    static Board board = new Board(false);
    static PlayerID whoseTurn = PlayerID.PLAYER1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for connections...");

            while(players.size() != 2) {
                Player player = new Player(serverSocket.accept(), PlayerID.fromIndex(players.size()+1));
                players.put(player.id,player);
                System.out.println(YELLOW.set(player.id+" joined"));
                new Thread(new ClientHandler(player.id)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void nextTurn() {
        whoseTurn = whoseTurn.getEnemy();
    }

    protected static Board restartGame() throws IOException {
        sendPacketsToPlayers(new Packet(PacketType.RESTART,whoseTurn));
        new Packet(PacketType.INIT, PlayerID.PLAYER1, whoseTurn).sendPacket(players.get(PlayerID.PLAYER1).outputStream);
        new Packet(PacketType.INIT, PlayerID.PLAYER2, whoseTurn).sendPacket(players.get(PlayerID.PLAYER2).outputStream);
        board = new Board(false);
        players.forEach((playerID, player) -> player.initPlayer());
        return board;
    }

    protected static void sendPacketsToPlayers(Packet packet) throws IOException {
        for (Map.Entry<PlayerID, Player> entry : players.entrySet()) {
            packet.sendPacket(entry.getValue().outputStream);
        }
    }
}

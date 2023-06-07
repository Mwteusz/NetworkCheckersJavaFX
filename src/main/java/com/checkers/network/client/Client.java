package com.checkers.network.client;

import com.checkers.board.Board;
import com.checkers.board.MoveListener;
import com.checkers.network.*;
import com.checkers.utils.ColorPalette;
import com.checkers.utils.ConsoleColors;
import com.checkers.utils.Point;
import com.checkers.utils.Timer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.checkers.utils.ConsoleColors.*;

public class Client extends Application implements MoveListener {
    private static final double TILE_SIZE = 60;
    private static final int UPDATE_FREQUENCY = 11;
    private static String server_ip = "127.0.0.1";
    private static int server_port = 12345;
    static Board board;
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;
    private Socket server;
    static PlayerID playerID;
    private Stage stage;
    Timer playerTimer = new Timer();
    Timer enemyTimer = new Timer();
    Label playerTimerLabel;
    Label enemyTimerLabel;
    Label messageLabel;
    Label scoreLabel;
    Integer[] scores = new Integer[]{0, 0};

    public Client(String server_ip, int server_port) throws IOException {
        Client.server_ip = server_ip;
        Client.server_port = server_port;
        this.server  = new Socket(server_ip, server_port);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        setupUI();
        try {
            System.out.println(GREEN.set("Connection established"));

            outputStream = new ObjectOutputStream(server.getOutputStream());
            inputStream = new ObjectInputStream(server.getInputStream());

            new Thread(this::receivePackets).start();

        } catch (IOException e) {
            System.out.println(RED.set("Server unreachable. ")+e.getMessage());
        }
    }

    private void receivePackets() {
        try {
            while (true) {
                Packet packet = Packet.receivePacket(inputStream);
                switch (packet.type) {
                    case INIT -> initPlayer(packet);
                    case MOVE -> movePiece(packet);
                    case CAPTURE -> capturePiece(packet);
                    case PROMOTION -> newKing(packet);
                    case MESSAGE,EXCEPTION -> viewMessage(packet);
                    case PLAYER_TURN -> playerTurn(packet);
                    case ENEMY_TURN -> enemyTurn(packet);
                    case FINISH_GAME -> finishGame();
                    case RESTART -> restartGame(packet);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(RED.set("Connection lost. ") + e.getMessage());
        }
    }

    private void viewMessage(Packet packet) {
        String message = packet.msg;
        ConsoleColors color = switch (packet.type){
            case EXCEPTION -> RED;
            case MESSAGE -> BLUE;
            default -> throw new IllegalStateException("Unexpected value: " + packet.type);
        };
        System.out.println(color.set(message));

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), messageLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(e->messageLabel.setText(""));

        Platform.runLater(()->{
            messageLabel.setText(message);
            fadeTransition.play();
        });
    }

    private void restartGame(Packet packet) {
        scores[packet.playerID == playerID ? 0 : 1]++;
        enemyTimer.stop(0);
        playerTimer.stop(0);
        Platform.runLater(this::setupUI);
    }

    private void updateTimer(Timer timer, Label label) {
        try {
            do{
                Platform.runLater(()-> label.setText(String.valueOf(timer.getTime())));
                Thread.sleep(UPDATE_FREQUENCY);
            } while (timer.isRunning());
            Platform.runLater(()-> label.setText(String.valueOf(timer.getTime())));
        } catch (InterruptedException e) {
            System.out.println(RED.set("Timer has stopped: ")+e.getMessage());
        }
    }

    private void enemyTurn(Packet packet) {
        playerTimer.stop(packet.playerTime);
        enemyTimer.start(packet.enemyTime);
        new Thread(()->updateTimer(enemyTimer,enemyTimerLabel)).start();
        highlightPieces(false);
    }

    private void playerTurn(Packet packet) {
        enemyTimer.stop(packet.enemyTime);
        playerTimer.start(packet.playerTime);
        new Thread(()->updateTimer(playerTimer,playerTimerLabel)).start();
        highlightPieces(true);
    }

    private void setupUI() {
        stage.setOnCloseRequest((e)-> disconnect());

        board = new Board(true);
        board.addBoardListener(this);


        BorderPane root = new BorderPane();
        root.setTop(new StackPane(createLabelPane()));
        root.setCenter(new StackPane(board,messageLabel));
        root.setBackground(new Background(new BackgroundFill(ColorPalette.dark,null,new Insets(0))));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    private BorderPane createLabelPane() {
        playerTimerLabel = createLabel();
        enemyTimerLabel = createLabel();
        scoreLabel = createLabel();
        messageLabel = createLabel();

        playerTimerLabel.setText(playerTimer.getTime());
        enemyTimerLabel.setText(enemyTimer.getTime());
        updateScore();

        BorderPane labels = new BorderPane();
        labels.setLeft(playerTimerLabel);
        labels.setRight(enemyTimerLabel);
        labels.setCenter(scoreLabel);
        labels.setBackground(new Background(new BackgroundFill(ColorPalette.light,null,new Insets(0))));
        labels.setBorder(new Border(new BorderStroke(ColorPalette.dark, BorderStrokeStyle.SOLID, null, new BorderWidths(TILE_SIZE * 0.1))));
        labels.setPrefHeight(TILE_SIZE * 0.75);
        labels.setMaxWidth(TILE_SIZE * 13);
        return labels;
    }

    private void updateScore() {
        scoreLabel.setText(scores[0] + " : " + scores[1]);
    }

    private void disconnect() {
        try {
            new Packet(PacketType.DISCONNECT).sendTo(outputStream);
        } catch (IOException e) {
            System.out.println(RED.set("Server unreachable. ")+e.getMessage());
        }
        finishGame();
    }

    private void finishGame() {
        System.exit(0);
    }

    private Label createLabel() {
        Label label = new Label();
        label.setMouseTransparent(true);
        label.setFont(Font.font("Arial", FontWeight.BOLD, TILE_SIZE * 0.5));
        label.setEffect(new Reflection());
        return label;
    }

    private void newKing(Packet packet) {
        Platform.runLater(()-> board.getField(packet.destination).getPiece().promote());
    }

    private void initPlayer(Packet packet) {
        playerID = packet.playerID;
        System.out.println("Player id: " + GREEN.set(playerID.toString()));
        Platform.runLater(()->{
            if(playerID.equals(PlayerID.PLAYER1))
                board.setRotate(180); //TODO own setRotate
            if(packet.startingPlayer == playerID)
                highlightPieces(true);
            stage.setTitle(playerID.toString());
        });
    }

    @Override
    public void inputMove(Board board, Point src, Point dst){
        try {
            if (board.getField(src).getPiece().getState() == playerID) {
                new Packet(PacketType.MOVE, playerID, src, dst).sendTo(outputStream);
            }
        } catch (IOException e) {
            System.out.println(RED.set("Server unreachable. ")+e.getMessage());
        }
    }

    private void capturePiece(Packet packet) {
        Platform.runLater(() -> board.getField(packet.destination).removePiece());
    }

    private void movePiece(Packet packet) {
        Platform.runLater(() -> board.movePiece(packet.source, packet.destination,true));
    }


    private static void highlightPieces(Boolean b) {
        board.forEach(playerID, field -> field.getPiece().setSelect(b));
    }

    public static void main(String[] args) {
        Platform.runLater(()-> {
            try {
                new Client(server_ip,server_port).start(new Stage());
            } catch (IOException e) {
                System.out.println("Could not connect to "+server_ip+":"+server_port + ". "+e.getMessage());
            }
        });
    }
}
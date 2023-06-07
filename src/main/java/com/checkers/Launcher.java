package com.checkers;

import com.checkers.network.client.Client;
import com.checkers.network.server.Server;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class Launcher extends Application {
    private Server server;
    private Label exceptionLabel;
    private Stage stage;
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Checkers Launcher");

        Label serverPortLabel = new Label("Server Port:");
        TextField serverPortField = new TextField();
        serverPortField.setText("12345");
        Button serverLaunchButton = new Button("Host Game");
        exceptionLabel = new Label("");
        exceptionLabel.setTextFill(Color.RED);

        Label clientIPLabel = new Label("Server IP:");
        TextField clientIPField = new TextField();
        clientIPField.setText("127.0.0.1");
        Label clientPortLabel = new Label("Port:");
        TextField clientPortField = new TextField();
        clientPortField.setText("12345");
        Button clientJoinButton = new Button("Join Game");

        serverLaunchButton.setOnAction(e -> {
            startServer(serverPortField.getText());
            joinServer("127.0.0.1",serverPortField.getText());
        });

        clientJoinButton.setOnAction(e -> {
            joinServer(clientIPField.getText(),clientPortField.getText());
        });

        VBox container = new VBox();
        VBox clientVBox = new VBox();
        clientVBox.setSpacing(5);
        VBox serverVBox = new VBox();
        serverVBox.setSpacing(5);
        HBox layout = new HBox(serverVBox,clientVBox);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(100);

        serverVBox.getChildren().add(new Label("HOST SERVER"));
        serverVBox.getChildren().add(serverPortLabel);
        serverVBox.getChildren().add(serverPortField);
        serverVBox.getChildren().add(serverLaunchButton);

        clientVBox.getChildren().add(new Label("JOIN SERVER"));
        clientVBox.getChildren().add(clientIPLabel);
        clientVBox.getChildren().add(clientIPField);
        clientVBox.getChildren().add(clientPortLabel);
        clientVBox.getChildren().add(clientPortField);
        clientVBox.getChildren().add(clientJoinButton);

        container.getChildren().addAll(layout,exceptionLabel);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);

        Scene scene = new Scene(container, 500, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void startServer(String portString) {
        try {
            int port = Integer.parseInt(portString);
            server = new Server(port);
            server.start();
            stage.close();
        } catch (IOException e) {
            viewException("Could not start the server. "+e.getMessage());
        } catch (IllegalArgumentException e) {
            viewException("Incorrect input. "+e.getMessage());
        }
    }

    private void viewException(String msg) {
        System.out.println(msg);
        exceptionLabel.setText(msg);
    }

    private void joinServer(String ip,String portString) {
        try {
            int port = Integer.parseInt(portString);
            System.out.println("Joining server at IP: " + ip + " Port: " + port);
            new Client(ip,port).start(new Stage());
            stage.close();
        } catch (IOException e){
            viewException("Could not join the server. "+e.getMessage());
        } catch (IllegalArgumentException e) {
            viewException("Incorrect input. "+e.getMessage());
        }
    }
}

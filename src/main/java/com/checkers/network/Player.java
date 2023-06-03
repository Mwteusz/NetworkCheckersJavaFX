package com.checkers.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player {
    Socket socket;
    public PlayerID id;
    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;
    private long startTime;
    private long timeAccumulated;
    public int checkersLost = 0;

    public void initPlayer() {
        startTime = 0;
        timeAccumulated = 0;
        checkersLost = 0;
    }

    public Player(Socket socket, PlayerID id) throws IOException {
        this.id = id;
        this.socket = socket;
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        initPlayer();
    }

    public long getTimeAccumulated() {
        return timeAccumulated;
    }

    public void accumulateTime() {
        this.timeAccumulated += System.currentTimeMillis() - startTime;
    }

    public void initTimer() {
        this.startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }
}

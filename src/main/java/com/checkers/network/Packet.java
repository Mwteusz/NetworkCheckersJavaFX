package com.checkers.network;

import com.checkers.utils.Point;

import java.io.*;


public class Packet implements Serializable{
    public PacketType type;
    public Point source;
    public Point destination;
    public PlayerID playerID;
    public PlayerID startingPlayer;
    public String msg;
    public long playerTime = 0;
    public long enemyTime = 0;

    public Packet(PacketType type) {
        this.type = type;
    }

    public Packet(PacketType type, PlayerID playerID, Point source, Point destination) { // move
        this(type,playerID);
        this.source = source;
        this.destination = destination;
    }
    public Packet(PacketType type, PlayerID playerID) {
        this(type);
        this.playerID = playerID;
    }

    public Packet(PacketType type,Point destination) { //update, king
        this(type);
        this.destination = destination;
    }
    public Packet(PacketType type, String msg) { //exception
        this(type);
        this.msg = msg;
    }

    public Packet(PacketType type, PlayerID playerID, long playerTime,  long getEnemyTime) {//timer
        this(type,playerID);
        this.playerTime = playerTime;
        this.enemyTime = getEnemyTime;
    }

    public Packet(PacketType type, PlayerID playerID, PlayerID startingPlayer) {
        this(type,playerID);
        this.startingPlayer = startingPlayer;
    }

    public void sendTo(ObjectOutputStream output) throws IOException {
        if(output == null)
            throw new IOException("Strumien nie istnieje");
        output.writeObject(this);
        System.out.println("<-  sent "+type.name());
    }

    public static Packet receivePacket(ObjectInputStream input) throws IOException, ClassNotFoundException {
        if(input == null)
            throw new IOException("Strumien nie istnieje");
        Packet receivedPacket = (Packet) input.readObject();
        System.out.println(" -> received " + receivedPacket.type.name());
        return receivedPacket;
    }

    @Override
    public String toString() {
        return "src=" + source + ", dst=" + destination;
    }

}

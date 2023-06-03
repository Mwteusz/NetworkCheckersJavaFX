package com.checkers.network;

import javafx.scene.paint.Color;

public enum PlayerID {
    NONE(0, Color.GRAY),
    PLAYER1(1, Color.RED),
    PLAYER2(2, Color.BLUE);

    public Color color;
    int index;

    PlayerID(int index, Color color) {
        this.index = index;
        this.color = color;
    }

    public static PlayerID fromIndex(int index) {
        for (PlayerID myEnum : PlayerID.values()) {
            if (myEnum.getIndex() == index) {
                return myEnum;
            }
        }
        throw new IllegalArgumentException("Invalid index: " + index);
    }

    public int getIndex() {
        return index;
    }

    public PlayerID getEnemy() {
        return this.equals(PLAYER1) ? PlayerID.PLAYER2 : PlayerID.PLAYER1;
    }
}

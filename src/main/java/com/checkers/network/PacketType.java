package com.checkers.network;

public enum PacketType {
    INIT,
    MOVE,
    CAPTURE,
    PROMOTION,
    MESSAGE,
    PLAYER_TURN,
    ENEMY_TURN,
    DISCONNECT,
    FINISH_GAME,
    EXCEPTION, RESTART
}

package com.checkers.utils;

public class InvalidMoveException extends RuntimeException{
    public InvalidMoveException(String msg){
        super(msg);
    }
}

package com.checkers.network;

import com.checkers.board.Board;
import com.checkers.board.Field;
import com.checkers.utils.InvalidMoveException;
import com.checkers.utils.Point;

public class moveValidating {

    public static PacketType validatePacket(Board board, Packet p) {
        if (!board.getField(p.source).hasPiece())
            throw new InvalidMoveException("No checker server-side!");
        if (!p.source.isDiagonalTo(p.destination))
            throw new InvalidMoveException("Only diagonal moves are allowed");
        if (board.getField(p.destination).hasPiece())
            throw new InvalidMoveException("Field occupied");
        if (!board.getField(p.source).getPiece().isKing()) {
            if (isMoveBackwards(p)) {
                throw new InvalidMoveException("No moving backwards");
            }
        }
        Field captureZone = board.getField(p.source.between(p.destination));
        if (p.source.diagonalDistance(p.destination) == 2 && captureZone.hasPiece() && !captureZone.getPiece().getState().equals(p.playerID))
            return PacketType.CAPTURE;
        return PacketType.MOVE;
    }

    public static void validateMove(Board board, Packet p) {
        if (p.source.diagonalDistance(p.destination) > 1 && !board.getField(p.source).getPiece().isKing())
            throw new InvalidMoveException("Unreachable field");
        else
            isDiagonalClear(board, p);
    }

    public static void validateCapture(Board board, Packet p) {
        if (board.getField(p.source).hasPiece() && board.getField(p.source).getPiece().isKing())
            return;
        Field captureZone = board.getField(p.source.between(p.destination));
        if (!captureZone.hasPiece() || captureZone.getPiece().getState() == p.playerID)
            throw new InvalidMoveException("No checker to capture");
        if (p.source.diagonalDistance(p.destination) != 2)
            throw new InvalidMoveException("Unreachable field");

    }

    private static boolean isMoveBackwards(Packet p) {
        return switch (p.playerID) {
            case PLAYER1 -> (p.source.yDiff(p.destination) >= 0);
            case PLAYER2 -> (p.source.yDiff(p.destination) <= 0);
            default -> false;
        };
    }

    public static void isDiagonalClear(Board board, Packet p) {
        Point scanner = new Point(p.source);
        for (int i = 1; i < p.source.diagonalDistance(p.destination); i++) {
            scanner.moveTowards(p.destination);
            if (board.getField(scanner).hasPiece()) {
                throw new InvalidMoveException("Move obstructed");
            }
        }
    }
}


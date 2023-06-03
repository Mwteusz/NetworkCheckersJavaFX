package com.checkers.board;

import com.checkers.utils.Point;

public interface MoveListener {

    void inputMove(Board board, Point position, Point position1);
}


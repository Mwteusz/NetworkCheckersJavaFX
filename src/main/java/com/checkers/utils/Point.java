package com.checkers.utils;

import java.io.Serializable;

public class Point implements Serializable {
    public Integer x;
    public Integer y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point1) {
        this.x=point1.x;
        this.y= point1.y;
    }

    boolean isNegative() {
        return x < 0 || y < 0;
    }

    public Point between(Point point) {
       return new Point((this.x + point.x)/2, (this.y + point.y)/2);
    }

    public boolean isDiagonalTo(Point point) {
        return yDiffAbs(point) == xDiffAbs(point);
    }
    boolean isNextTo(Point point) {
        return yDiffAbs(point) <= 1 && xDiffAbs(point) <= 1;
    }

    public int diagonalDistance(Point point) {
        if(isDiagonalTo(point))
        {
            return (xDiffAbs(point) + yDiffAbs(point)) / 2;
        }
        return -1;
    }
    int xDiffAbs(Point point) {
        return Math.abs(this.x - point.x);
    }
    int yDiffAbs(Point point) {
        return Math.abs(this.y - point.y);
    }
    int xDiff(Point point) {
        return this.x - point.x;
    }
    public int yDiff(Point point) {
        return this.y - point.y;
    }

    public Point add(int x, int y) {
        return new Point(this.x+x,this.y+y);
    }

    public void moveTowards(Point point) {
        int moveX, moveY;
        if(xDiff(point)>0)
            moveX=-1;
        else
            moveX=1;
        if(yDiff(point)>0)
            moveY=-1;
        else
            moveY=1;
        this.move(moveX,moveY);
    }

    private void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public String toString() {
        return "[" + this.x.toString() + ", " + this.y.toString() + "]";
    }

    public boolean isOdd() {
        return (x+y)%2 == 1;
    }
}

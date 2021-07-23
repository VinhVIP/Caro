package com.vinh.caro.models;

import com.vinh.caro.Turn;

/**
 * Create by VinhIT
 * On 23/07/2021
 */

public class Point {
    private int x, y;
    private Turn turn;

    public Point(int x, int y, Turn turn) {
        this.x = x;
        this.y = y;
        this.turn = turn;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Turn getXo() {
        return turn;
    }

    public void setXo(Turn turn) {
        this.turn = turn;
    }
}

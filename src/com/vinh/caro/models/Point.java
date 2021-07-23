package com.vinh.caro.models;

import com.vinh.caro.XO;

/**
 * Create by VinhIT
 * On 23/07/2021
 */

public class Point {
    private int x, y;
    private XO xo;

    public Point(int x, int y, XO xo) {
        this.x = x;
        this.y = y;
        this.xo = xo;
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

    public XO getXo() {
        return xo;
    }

    public void setXo(XO xo) {
        this.xo = xo;
    }
}

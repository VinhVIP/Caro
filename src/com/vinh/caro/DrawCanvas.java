package com.vinh.caro;

import com.vinh.caro.models.Point;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Create by VinhIT
 * On 23/07/2021
 */

public class DrawCanvas extends Canvas {

    public static final int CELL_SIZE = 40;
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    public static final int CELL_X = 1;
    public static final int CELL_O = 2;

    public static final int GRID_COLOR = 0xff00ff;

    int[][] board = new int[WIDTH][HEIGHT];
    private XO turn;


    public DrawCanvas() {
        setPreferredSize(new Dimension(810, 810));
        setBackground(Color.WHITE);

        addMouseListener(new MyMouseAdapter());

        turn = XO.X;
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(GRID_COLOR));

        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, HEIGHT * CELL_SIZE);
            g.drawLine(0, i * CELL_SIZE, WIDTH * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawXO(Point p) {
        Graphics g = getGraphics();

        if (p.getXo() == XO.X) {
            g.setColor(Color.RED);
            g.drawLine(p.getX() * CELL_SIZE + 8, p.getY() * CELL_SIZE + 8, (p.getX() + 1) * CELL_SIZE - 8, (p.getY() + 1) * CELL_SIZE - 8);
            g.drawLine((p.getX() + 1) * CELL_SIZE - 8, p.getY() * CELL_SIZE + 8, p.getX() * CELL_SIZE + 8, (p.getY() + 1) * CELL_SIZE - 8);
        } else {
            g.setColor(Color.GREEN);
            g.drawOval(p.getX() * CELL_SIZE + 7, p.getY() * CELL_SIZE + 7, 26, 26);
            g.drawOval(p.getX() * CELL_SIZE + 8, p.getY() * CELL_SIZE + 8, 24, 24);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawGrid(g);
    }

    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            Point p = getPoint(e.getX(), e.getY(), turn);
            if (board[p.getX()][p.getY()] == 0) {
                if (turn == XO.X) {
                    turn = XO.O;
                    board[p.getX()][p.getY()] = CELL_O;
                } else {
                    turn = XO.X;
                    board[p.getX()][p.getY()] = CELL_X;
                }
                p.setXo(turn);

                drawXO(p);
            }


        }
    }

    public Point getPoint(int symX, int symY, XO xo) {
        return new Point(symX / CELL_SIZE, symY / CELL_SIZE, xo);
    }
}

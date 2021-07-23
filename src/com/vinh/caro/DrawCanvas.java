package com.vinh.caro;

import com.vinh.caro.models.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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

    private int countXO = 0;
    int[][] board = new int[WIDTH][HEIGHT];
    private Turn turn;
    private Point lastHover = null;


    public DrawCanvas() {
        setPreferredSize(new Dimension(810, 810));
        setBackground(Color.WHITE);

        addMouseListener(new MyMouseAdapter());
        addMouseMotionListener(new MyMouseMotionAdapter());

        turn = Turn.X;
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(GRID_COLOR));

        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, HEIGHT * CELL_SIZE);
            g.drawLine(0, i * CELL_SIZE, WIDTH * CELL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawCell(Graphics g, Point p, boolean isHover) {
        if (!insideBoard(p)) return;

        if (isHover) {
            g.setColor(Color.CYAN);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(p.getX() * CELL_SIZE + 1, p.getY() * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
        drawXO(p);
    }

    private void drawXO(Point p) {
        Graphics g = getGraphics();

        if (board[p.getX()][p.getY()] == CELL_X) {
            g.setColor(Color.RED);
            g.drawLine(p.getX() * CELL_SIZE + 8, p.getY() * CELL_SIZE + 8, (p.getX() + 1) * CELL_SIZE - 8, (p.getY() + 1) * CELL_SIZE - 8);
            g.drawLine((p.getX() + 1) * CELL_SIZE - 8, p.getY() * CELL_SIZE + 8, p.getX() * CELL_SIZE + 8, (p.getY() + 1) * CELL_SIZE - 8);
        } else if (board[p.getX()][p.getY()] == CELL_O) {
            g.setColor(Color.GREEN);
            g.drawOval(p.getX() * CELL_SIZE + 7, p.getY() * CELL_SIZE + 7, 26, 26);
            g.drawOval(p.getX() * CELL_SIZE + 8, p.getY() * CELL_SIZE + 8, 24, 24);
        }
    }

    private State checkState(Turn curTurn) {
        int value = curTurn == Turn.O ? CELL_O : CELL_X;

        return checkState(value);
    }

    private State checkState(int valueCheck) {
        if (countXO == WIDTH * HEIGHT) return State.DRAW;

        int[] directX = {1, 1, 0, -1};
        int[] directY = {0, 1, 1, 1};

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int k = 0; k < directX.length; k++)
                    if (check5(x, y, directX[k], directY[k], valueCheck)) return State.WIN;
            }
        }
        return State.UNDEFINED;
    }

    private boolean check5(int u, int v, int xUnit, int yUnit, int value) {
        int cnt = 0;
        int x = u, y = v;
        while (cnt < 5) {
            x += xUnit;
            y += yUnit;
            if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) break;

            if (board[x][y] == value) cnt++;
            else break;
        }

        if (cnt == 5) {
            int k = 0;
            while (k++ < 5) {
                u += xUnit;
                v += yUnit;
                drawCell(getGraphics(), new Point(u, v, null), true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawGrid(g);
    }

    private void reset() {
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                board[i][j] = 0;
        Graphics g = getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE);
        drawGrid(g);
    }

    private void checkEndGame() {
        if (checkState(turn) == State.WIN) {
            String mess = "";
            if (turn == Turn.O) mess = "O Win!";
            else mess = "X Win!";
            JOptionPane.showMessageDialog(null, mess);
            reset();
        }
    }

    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            Point p = getPoint(e.getX(), e.getY(), turn);
            if (insideBoard(p) && board[p.getX()][p.getY()] == 0) {
                if (turn == Turn.X) {
                    turn = Turn.O;
                    board[p.getX()][p.getY()] = CELL_O;
                } else {
                    turn = Turn.X;
                    board[p.getX()][p.getY()] = CELL_X;
                }

                p.setXo(turn);
                board[p.getX()][p.getY()] = turn == Turn.O ? CELL_O : CELL_X;
                countXO++;

                drawCell(getGraphics(), p, true);

                checkEndGame();
            }


        }
    }

    public class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            Point p = getPoint(e.getX(), e.getY(), null);
            if (lastHover != null && lastHover.getX() == p.getX() && lastHover.getY() == p.getY()) return;

            if (lastHover != null) {
                drawCell(getGraphics(), lastHover, false);
            }
            drawCell(getGraphics(), p, true);
            lastHover = p;
        }
    }

    private boolean insideBoard(Point p) {
        return p.getX() >= 0 &&
                p.getX() < WIDTH &&
                p.getY() >= 0 &&
                p.getY() < HEIGHT;
    }

    public Point getPoint(int symX, int symY, Turn turn) {
        return new Point(symX / CELL_SIZE, symY / CELL_SIZE, turn);
    }
}

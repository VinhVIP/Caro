package com.vinh.caro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;

import static com.vinh.caro.utils.Constants.*;

/**
 * Create by VinhIT
 * On 23/07/2021
 */

public class DrawCanvas extends Canvas {

    private Paint paint;

    private int countXO = 0;
    private int lastX = -1, lastY = -1;

    Table table;

    private boolean isUserFirst, isXFirst;
    private int caroX = -1, caroO = -1;

    public DrawCanvas(Paint paint) {
        this.paint = paint;

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.WHITE);

        addMouseListener(new MyMouseAdapter());
        addMouseMotionListener(new MyMouseMotionAdapter());

        table = new Table();

    }

    public void setup(boolean isUserFirst, boolean isXFirst) {
        this.isUserFirst = isUserFirst;
        this.isXFirst = isXFirst;

        if (isUserFirst) {
            if (isXFirst) {
                caroX = USER;
                caroO = COMPUTER;
            } else {
                caroX = COMPUTER;
                caroO = USER;
            }
        } else {
            if (isXFirst) {
                caroX = COMPUTER;
                caroO = USER;
            } else {
                caroO = COMPUTER;
                caroX = USER;
            }

            computerFirst();
        }
    }

    private void computerFirst() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % NUM_COLS;
        int y = Math.abs(r.nextInt()) % NUM_ROWS;

        table.cell[x][y] = COMPUTER;
        drawCell(getGraphics(), x, y, true);
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(GRID_COLOR));

        for (int i = 0; i <= NUM_ROWS; i++) {
            g.drawLine(0, i * CELL_SIZE, NUM_COLS * CELL_SIZE, i * CELL_SIZE);
        }
        for (int i = 0; i <= NUM_COLS; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, NUM_ROWS * CELL_SIZE);
        }
    }

    private void drawCell(Graphics g, int x, int y, boolean isHover) {
        if (!insideBoard(x, y)) return;

        if (isHover) {
            g.setColor(new Color(CELL_HOVER_COLOR));
        } else {
            g.setColor(new Color(CELL_COLOR));
        }
        g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
        drawXO(x, y);
    }

    private void drawXO(int x, int y) {
        Graphics g = getGraphics();

        if (table.cell[x][y] == caroX) {
            g.setColor(Color.RED);
            g.drawLine(x * CELL_SIZE + 8, y * CELL_SIZE + 8, (x + 1) * CELL_SIZE - 8, (y + 1) * CELL_SIZE - 8);
            g.drawLine((x + 1) * CELL_SIZE - 8, y * CELL_SIZE + 8, x * CELL_SIZE + 8, (y + 1) * CELL_SIZE - 8);
        } else if (table.cell[x][y] == caroO) {
            g.setColor(Color.GREEN);
            g.drawOval(x * CELL_SIZE + 7, y * CELL_SIZE + 7, 26, 26);
            g.drawOval(x * CELL_SIZE + 8, y * CELL_SIZE + 8, 24, 24);
        }
    }

//    private State checkState(int valueCheck) {
//        if (countXO == WIDTH * HEIGHT) return State.DRAW;
//
//        int[] directX = {1, 1, 0, -1};
//        int[] directY = {0, 1, 1, 1};
//
//        for (int x = 0; x < WIDTH; x++) {
//            for (int y = 0; y < HEIGHT; y++) {
//                for (int k = 0; k < directX.length; k++)
//                    if (check5(board, x, y, directX[k], directY[k], valueCheck)) return State.WIN;
//            }
//        }
//        return State.UNDEFINED;
//    }
//
//    private boolean check5(int[][] board, int u, int v, int xUnit, int yUnit, int value) {
//        int cnt = 0;
//        int x = u, y = v;
//        while (cnt < 5) {
//            x += xUnit;
//            y += yUnit;
//            if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) break;
//
//            if (board[x][y] == value) cnt++;
//            else break;
//        }
//
//        if (cnt == 5) {
//            int k = 0;
//            while (k++ < 5) {
//                u += xUnit;
//                v += yUnit;
//                drawCell(getGraphics(), new Point(u, v, 0), true);
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkEnd(int[][] board, int u, int v, int xUnit, int yUnit, int value) {
//        int cnt = 0;
//        int x = u, y = v;
//        while (cnt < 5) {
//            x += xUnit;
//            y += yUnit;
//            if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) break;
//
//            if (board[x][y] == value) cnt++;
//            else break;
//        }
//        return cnt == 5;
//    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawGrid(g);
        for (int i = 0; i < NUM_COLS; i++)
            for (int j = 0; j < NUM_ROWS; j++)
                drawXO(i, j);
    }

    private void reset() {
        for (int i = 0; i < NUM_COLS; i++)
            for (int j = 0; j < NUM_ROWS; j++)
                table.cell[i][j] = 0;

        Graphics g = getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, NUM_COLS * CELL_SIZE, NUM_ROWS * CELL_SIZE);
        drawGrid(g);
    }

//    private void checkEndGame() {
//        if (checkState(CELL_O) == State.DRAW) {
//            JOptionPane.showMessageDialog(null, "DRAW");
//            reset();
//        } else if (checkState(CELL_O) == State.WIN) {
//            JOptionPane.showMessageDialog(null, "O Win");
//            reset();
//        } else if (checkState(CELL_X) == State.WIN) {
//            JOptionPane.showMessageDialog(null, "X Win");
//            reset();
//        }
//    }

//    private boolean isEndNode(int[][] state) {
//        int[] directX = {1, 1, 0, -1};
//        int[] directY = {0, 1, 1, 1};
//
//        for (int x = 0; x < WIDTH; x++) {
//            for (int y = 0; y < HEIGHT; y++) {
//                for (int k = 0; k < directX.length; k++)
//                    if (checkEnd(state, x, y, directX[k], directY[k], CELL_X) ||
//                            checkEnd(state, x, y, directX[k], directY[k], CELL_O)) return true;
//            }
//        }
//        return false;
//    }

//    private boolean isWin(int[][] state) {
//        int[] directX = {1, 1, 0, -1};
//        int[] directY = {0, 1, 1, 1};
//
//        for (int x = 0; x < WIDTH; x++) {
//            for (int y = 0; y < HEIGHT; y++) {
//                for (int k = 0; k < directX.length; k++)
//                    if (checkEnd(state, x, y, directX[k], directY[k], CELL_X) ||
//                            checkEnd(state, x, y, directX[k], directY[k], CELL_O)) return true;
//            }
//        }
//        return false;
//    }

//    private int checkTurn(int[][] state) {
//        int cnt = 0;
//        for (int i = 0; i < WIDTH; i++)
//            for (int j = 0; j < HEIGHT; j++) {
//                if (state[i][j] != 0) cnt++;
//            }
//
//        if (cnt % 2 == 0) return CELL_O;
//        return CELL_X;
//    }

//    private int value(int[][] state) {
//        if (isWin(state)) {
//            if (checkTurn(state) == CELL_O) return 1;
//            else return -1;
//        }
//        return 0;
//    }

//    private int[][] newState(int[][] state, int x, int y, int valueCell) {
//        int[][] a = new int[WIDTH][HEIGHT];
//        for (int i = 0; i < WIDTH; i++)
//            for (int j = 0; j < HEIGHT; j++) {
//                a[i][j] = state[i][j];
//            }
//
//        a[x][y] = valueCell;
//        return a;
//    }
//
//    private void ai() {
//        int min = Integer.MAX_VALUE;
//        int[][] minChild = newState(board, 0, 0, board[0][0]);
//
//        for (int i = 0; i < WIDTH; i++) {
//            for (int j = 0; j < HEIGHT; j++) {
//                if (board[i][j] == 0) {
//                    int[][] child = newState(board, i, j, CELL_O);
//
//                    int tmp = minimax(child, 5, true);
//
//                    if (tmp < min) {
//                        min = tmp;
//                        minChild = child;
//                        System.out.println("found : " + i + ";" + j);
//                    }
//                }
//            }
//        }
//
////        board = minChild;
//
//        for (int i = 0; i < WIDTH; i++) {
//            for (int j = 0; j < HEIGHT; j++) {
//                if (board[i][j] != minChild[i][j]) {
//                    board[i][j] = minChild[i][j];
//                    drawCell(getGraphics(), new Point(i, j, CELL_O), true);
//                }
//                System.out.print(board[j][i] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println("ok ok");
//
//        countXO++;
//
////        checkEndGame();
//    }
//
//    private int minimax(int[][] state, int depth, boolean isMaxPlayer) {
//        return alphaBeta(state, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaxPlayer);
//    }
//
//    private int alphaBeta(int[][] state, int depth, int a, int b, boolean isMaxPlayer) {
//        if (isEndNode(state) || depth == 0) {
//            return value(state);
//        }
//
//        if (isMaxPlayer) {
//            for (int i = 0; i < WIDTH; i++) {
//                for (int j = 0; j < HEIGHT; j++) {
//                    if (state[i][j] != 0) {
//                        int tmp = alphaBeta(newState(state, i, j, CELL_X), depth - 1, a, b, false);
//                        a = Math.max(a, tmp);
//                        if (a >= b) break;
//                    }
//                }
//            }
//            return a;
//        } else {
//            for (int i = 0; i < WIDTH; i++) {
//                for (int j = 0; j < HEIGHT; j++) {
//                    if (state[i][j] != 0) {
//                        int tmp = alphaBeta(newState(state, i, j, CELL_O), depth - 1, a, b, true);
//                        b = Math.min(a, tmp);
//                        if (a >= b) break;
//                    }
//                }
//            }
//            return b;
//        }
//    }
//
//    private int nextTurn() {
//        if (countXO % 2 == 0) return CELL_X;
//        return CELL_O;
//    }

    private boolean checkEndGame(int x, int y) {
        if (countXO == NUM_ROWS * NUM_COLS) {
            JOptionPane.showMessageDialog(null, "Draw!");
            reset();
            paint.newGame();
            return true;
        } else if (table.checkWin()) {
            drawCell(getGraphics(), x, y, false);

            x = table.wx;
            y = table.wy;
            int k = 0;
            while (k++ < 5) {
                drawCell(getGraphics(), x, y, true);
                x += table.wdx;
                y += table.wdy;
            }
            JOptionPane.showMessageDialog(null, "Computer win!");
            reset();
            paint.newGame();
            return true;
        }
        return false;
    }

    private int lastComputerX = -1, lastComputerY = -1;

    // -------- Mouse Adapter -----------------
    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            int x = e.getX() / CELL_SIZE;
            int y = e.getY() / CELL_SIZE;

            if (insideBoard(x, y) && table.cell[x][y] == 0) {

                table.cell[x][y] = USER;
                countXO++;
                drawCell(getGraphics(), x, y, true);

                drawCell(getGraphics(), lastComputerX, lastComputerY, false);

                if (!checkEndGame(x, y)) {
                    table.findSolution();

                    try {
                        Thread.sleep(300);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    table.cell[table.resX][table.resY] = COMPUTER;
                    countXO++;
                    drawCell(getGraphics(), table.resX, table.resY, true);

                    lastComputerX = table.resX;
                    lastComputerY = table.resY;

                    checkEndGame(x, y);
                }

            }
        }
    }


    public class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            int x = e.getX() / CELL_SIZE;
            int y = e.getY() / CELL_SIZE;

            if (lastX == x && lastY == y) {
                return;
            }
            if (lastX >= 0 || lastY >= 0) {
                drawCell(getGraphics(), lastX, lastY, false);
            }
            if (insideBoard(x, y)) {
                drawCell(getGraphics(), x, y, true);
            }

            lastX = x;
            lastY = y;
        }
    }

    private boolean insideBoard(int x, int y) {
        return x >= 0 &&
                x < NUM_COLS &&
                y >= 0 &&
                y < NUM_ROWS;
    }

}

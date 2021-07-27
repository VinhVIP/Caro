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

    private final Paint paint;

    private int countXO = 0;    // Đếm số lượng quân cờ đã được đánh
    private int lastX = -1, lastY = -1;     // Tọa độ chuột hover cuối cùng, dùng để highlight/bỏ highlight ô cờ

    private final Table table;    // Lớp cài đặt giải thuật tìm nước cờ đánh kế tiếp

    private boolean isUserFirst;
    private int caroX = -1, caroO = -1;
    private int lastComputerX = -1, lastComputerY = -1;     // Tọa độ máy đánh gần nhất, dùng để highlight/bỏ highlight ô cờ

    public DrawCanvas(Paint paint) {
        this.paint = paint;

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.WHITE);

        addMouseListener(new MyMouseAdapter());
        addMouseMotionListener(new MyMouseMotionAdapter());

        table = new Table();

    }

    /**
     * Cài đặt chế dộ chơi
     *
     * @param isUserFirst Người chơi đánh trước
     * @param isXFirst    Quân X đánh trước
     */
    public void setup(boolean isUserFirst, boolean isXFirst) {
        this.isUserFirst = isUserFirst;

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


    /**
     * Máy đánh trước, random 1 điểm bất kì trên bàn cờ
     */
    private void computerFirst() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % (NUM_COLS - NUM_COLS / 2) + NUM_COLS / 4;
        int y = Math.abs(r.nextInt()) % (NUM_ROWS - NUM_ROWS / 2) + NUM_ROWS / 4;

        table.cell[x][y] = COMPUTER;
        countXO++;
        drawCell(getGraphics(), x, y, true);

        lastComputerX = x;
        lastComputerY = y;
    }

    /**
     * Vẽ lưới bàn cờ
     *
     * @param g
     */
    private void drawGrid(Graphics g) {
        g.setColor(new Color(GRID_COLOR));

        for (int i = 0; i <= NUM_ROWS; i++) {
            g.drawLine(0, i * CELL_SIZE, NUM_COLS * CELL_SIZE, i * CELL_SIZE);
        }
        for (int i = 0; i <= NUM_COLS; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, NUM_ROWS * CELL_SIZE);
        }
    }

    /**
     * Vẽ ô cờ tại tọa độ xác định
     *
     * @param g
     * @param x       tọa độ X
     * @param y       tọa độ y
     * @param isHover ô được có đang được rê chuột hoặc highlight lên k
     */
    private void drawCell(Graphics g, int x, int y, boolean isHover) {
        if (!insideBoard(x, y)) return;

        if (isHover) {
            g.setColor(new Color(CELL_HOVER_COLOR));
        } else {
            g.setColor(new Color(CELL_COLOR));
        }
        g.fillRect(x * CELL_SIZE + 1, y * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
        drawXO(g, x, y);
    }


    /**
     * Vẽ quân X hoặc O tương ứng lên ô cờ
     *
     * @param g
     * @param x
     * @param y
     */
    private void drawXO(Graphics g, int x, int y) {

        if (table.cell[x][y] == caroX) {
            g.setColor(Color.RED);
            g.drawLine(x * CELL_SIZE + 9, y * CELL_SIZE + 9, (x + 1) * CELL_SIZE - 9, (y + 1) * CELL_SIZE - 9);
            g.drawLine(x * CELL_SIZE + 9, y * CELL_SIZE + 10, (x + 1) * CELL_SIZE - 10, (y + 1) * CELL_SIZE - 9);
            g.drawLine(x * CELL_SIZE + 10, y * CELL_SIZE + 9, (x + 1) * CELL_SIZE - 9, (y + 1) * CELL_SIZE - 10);

            g.drawLine((x + 1) * CELL_SIZE - 9, y * CELL_SIZE + 9, x * CELL_SIZE + 9, (y + 1) * CELL_SIZE - 9);
            g.drawLine((x + 1) * CELL_SIZE - 10, y * CELL_SIZE + 9, x * CELL_SIZE + 9, (y + 1) * CELL_SIZE - 10);
            g.drawLine((x + 1) * CELL_SIZE - 9, y * CELL_SIZE + 10, x * CELL_SIZE + 10, (y + 1) * CELL_SIZE - 9);
        } else if (table.cell[x][y] == caroO) {
            g.setColor(Color.GREEN);
            g.drawOval(x * CELL_SIZE + 7, y * CELL_SIZE + 7, 26, 26);
            g.drawOval(x * CELL_SIZE + 8, y * CELL_SIZE + 8, 24, 24);
            g.drawOval(x * CELL_SIZE + 9, y * CELL_SIZE + 9, 22, 22);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawGrid(g);
        for (int i = 0; i < NUM_COLS; i++)
            for (int j = 0; j < NUM_ROWS; j++)
                drawXO(g, i, j);
    }


    /**
     * Reset để chơi ván mới
     */
    private void reset() {
        countXO = 0;
        paint.setUserBoard("");
        paint.setComputerBoard("");

        for (int i = 0; i < NUM_COLS; i++)
            for (int j = 0; j < NUM_ROWS; j++)
                table.cell[i][j] = 0;

        Graphics g = getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, NUM_COLS * CELL_SIZE, NUM_ROWS * CELL_SIZE);
        drawGrid(g);
    }

    /**
     * Kiểm tra ván cờ có thể kết hay không?
     * Nếu có, hiển thị dialog thông báo kết quả
     * Và highlight đường 5 chiến thắng
     *
     * @param x tọa độ X của nước chơi cuối cùng của máy (dùng để bỏ highlight ô cờ)
     * @param y tọa độ Y của nước chơi cuối cùng của máy
     * @return ván cờ kết thúc (thắng, hòa) hay không?
     */
    private boolean checkEndGame(int x, int y) {
        if (countXO == NUM_ROWS * NUM_COLS) {
            JOptionPane.showMessageDialog(null, "Hòa!");
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

            String mess = "";

            if (countXO % 2 == 1) {
                // Người chơi trước thắng
                mess = (isUserFirst ? "Bạn" : "Máy") + " thắng!";
            } else {
                mess = (isUserFirst ? "Máy" : "Bạn") + " thắng!";
            }

            JOptionPane.showMessageDialog(null, mess);
            reset();
            paint.newGame();
            return true;
        }

        return false;
    }

    /**
     * Show dữ liệu heuristic
     */
    private void showScoreBoard() {
        StringBuilder comp = new StringBuilder();

        for (int y = 0; y < NUM_ROWS; y++) {
            for (int x = 0; x < NUM_COLS; x++) {
                comp.append(table.score[x][y]).append("\t");
            }
            comp.append("\n");
        }

        StringBuilder user = new StringBuilder();

        for (int y = 0; y < NUM_ROWS; y++) {
            for (int x = 0; x < NUM_COLS; x++) {
                user.append(table.scoreUser[x][y]).append("\t");
            }
            user.append("\n");
        }

        paint.setComputerBoard(comp.toString());
        paint.setUserBoard(user.toString());
    }


    // -------- Mouse Adapter -----------------
    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            int x = e.getX() / CELL_SIZE;
            int y = e.getY() / CELL_SIZE;

            if (insideBoard(x, y) && table.cell[x][y] == 0) {

                // Người chơi đánh
                table.cell[x][y] = USER;
                countXO++;
                drawCell(getGraphics(), x, y, true);

                drawCell(getGraphics(), lastComputerX, lastComputerY, false);

                if (!checkEndGame(x, y)) {
                    // Nếu người chơi đánh nhưng chưa thể kết thúc game
                    // Thì đến lượt máy đánh

                    // Tìm kiếm nước đi kế tiếp
                    table.findSolution();

                    showScoreBoard();

                    // Dừng 1 khoảng thời gian để người chơi kịp nhìn thấy máy đánh :))
                    try {
                        Thread.sleep(300);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    // Máy đánh
                    table.cell[table.resX][table.resY] = COMPUTER;
                    countXO++;
                    drawCell(getGraphics(), table.resX, table.resY, true);

                    lastComputerX = table.resX;
                    lastComputerY = table.resY;

                    // Kiểm tra xem máy đánh thì có kết thúc ván đấu hay chưa
                    checkEndGame(x, y);
                }

            }
        }
    }


    public class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);

            // Highlight ô cờ đang được rê chuột

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


}

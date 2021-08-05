package com.vinh.caro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;
import java.util.Stack;

import static com.vinh.caro.utils.Constants.*;

/**
 * Create by VinhIT
 * On 23/07/2021
 */

public class DrawCanvas extends Canvas {

    private final Paint paint;
    private final Board board;    // Lớp cài đặt giải thuật tìm nước cờ đánh kế tiếp
    private final Stack<Point> userPoints, compPoints;   // Stack lưu lại các nước đánh (undo)
    private final Random random;

    private int countXO = 0;    // Đếm số lượng quân cờ đã được đánh
    private final Point lastHoverPoint, lastCompPoint;

    private boolean isUserFirst;
    private int caroX = -1, caroO = -1;


    public DrawCanvas(Paint paint) {
        this.paint = paint;

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.WHITE);

        addMouseListener(new MyMouseAdapter());
        addMouseMotionListener(new MyMouseMotionAdapter());

        lastHoverPoint = new Point(-1, -1);
        lastCompPoint = new Point(-1, -1);

        userPoints = new Stack<>();
        compPoints = new Stack<>();

        random = new Random();

        board = new Board();
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
        int x = Math.abs(random.nextInt()) % (NUM_COLS - NUM_COLS / 2) + NUM_COLS / 4;
        int y = Math.abs(random.nextInt()) % (NUM_ROWS - NUM_ROWS / 2) + NUM_ROWS / 4;

        computerPlay(new Point(x, y));
    }

    /**
     * Người choi đánh lại
     */
    public void undo() {
        if (compPoints.size() > 0 && userPoints.size() > 0) {
            Point p;

            // Thu hồi nước đi của COMPUTER
            p = compPoints.pop();
            board.clear(p);
            drawCell(getGraphics(), p, false);

            // Thu hồi nước đi của USER
            p = userPoints.pop();
            board.clear(p);
            drawCell(getGraphics(), p, false);
        }
    }

    private void userPlay(Point p) {
        board.set(p, USER);
        countXO++;
        drawCell(getGraphics(), p, true);

        userPoints.push(p);
    }

    private void computerPlay(Point p) {
        board.set(p, COMPUTER);
        countXO++;
        drawCell(getGraphics(), p, true);

        lastCompPoint.move(p.x, p.y);
        compPoints.push(p);
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
     * @param p       tọa độ vẽ
     * @param isHover ô được vẽ có đang được highlight hay không
     */
    private void drawCell(Graphics g, Point p, boolean isHover) {
        if (!insideBoard(p)) return;

        if (isHover) {
            g.setColor(new Color(CELL_HOVER_COLOR));
        } else {
            g.setColor(new Color(CELL_COLOR));
        }
        g.fillRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
        drawXO(g, p);
    }


    /**
     * Vẽ quân X hoặc O tương ứng lên ô cờ
     *
     * @param g
     * @param p tọa độ vẽ
     */
    private void drawXO(Graphics g, Point p) {

        if (board.get(p) == caroX) {
            g.setColor(Color.RED);
            g.drawLine(p.x * CELL_SIZE + 9, p.y * CELL_SIZE + 9, (p.x + 1) * CELL_SIZE - 9, (p.y + 1) * CELL_SIZE - 9);
            g.drawLine(p.x * CELL_SIZE + 9, p.y * CELL_SIZE + 10, (p.x + 1) * CELL_SIZE - 10, (p.y + 1) * CELL_SIZE - 9);
            g.drawLine(p.x * CELL_SIZE + 10, p.y * CELL_SIZE + 9, (p.x + 1) * CELL_SIZE - 9, (p.y + 1) * CELL_SIZE - 10);

            g.drawLine((p.x + 1) * CELL_SIZE - 9, p.y * CELL_SIZE + 9, p.x * CELL_SIZE + 9, (p.y + 1) * CELL_SIZE - 9);
            g.drawLine((p.x + 1) * CELL_SIZE - 10, p.y * CELL_SIZE + 9, p.x * CELL_SIZE + 9, (p.y + 1) * CELL_SIZE - 10);
            g.drawLine((p.x + 1) * CELL_SIZE - 9, p.y * CELL_SIZE + 10, p.x * CELL_SIZE + 10, (p.y + 1) * CELL_SIZE - 9);
        } else if (board.get(p) == caroO) {
            g.setColor(Color.GREEN);
            g.drawOval(p.x * CELL_SIZE + 7, p.y * CELL_SIZE + 7, 26, 26);
            g.drawOval(p.x * CELL_SIZE + 8, p.y * CELL_SIZE + 8, 24, 24);
            g.drawOval(p.x * CELL_SIZE + 9, p.y * CELL_SIZE + 9, 22, 22);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawGrid(g);

        Point p = new Point();
        for (int i = 0; i < NUM_COLS; i++) {
            for (int j = 0; j < NUM_ROWS; j++) {
                p.move(i, j);
                drawXO(g, p);
            }
        }

    }


    /**
     * Reset để chơi ván mới
     */
    public void reset() {
        countXO = 0;
        paint.setUserBoard("");
        paint.setComputerBoard("");

        userPoints.clear();
        compPoints.clear();

        board.init();

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
     * @param p tọa độ nước chơi cuối cùng của máy (dùng để bỏ highlight ô cờ)
     * @return ván cờ kết thúc (thắng, hòa) hay không?
     */
    private boolean checkEndGame(Point p) {
        if (countXO == NUM_ROWS * NUM_COLS) {
            JOptionPane.showMessageDialog(null, "Hòa!");
            reset();
            paint.setupNewGame();
            return true;
        } else if (board.checkWin()) {
            drawCell(getGraphics(), p, false);
            drawCell(getGraphics(), lastHoverPoint, false);

            p.move(board.winPoint.x, board.winPoint.y);
            int k = 0;
            while (k++ < 5) {
                drawCell(getGraphics(), p, true);
                p.translate(board.wdx, board.wdy);
            }

            String mess;

            if (countXO % 2 == 1) {
                // Người chơi trước thắng
                mess = (isUserFirst ? "Bạn" : "Máy") + " thắng!";
            } else {
                mess = (isUserFirst ? "Máy" : "Bạn") + " thắng!";
            }

            JOptionPane.showMessageDialog(null, mess);
            reset();
            paint.setupNewGame();
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
                comp.append(board.scoreComp[x][y]).append("\t");
            }
            comp.append("\n");
        }

        StringBuilder user = new StringBuilder();

        for (int y = 0; y < NUM_ROWS; y++) {
            for (int x = 0; x < NUM_COLS; x++) {
                user.append(board.scoreUser[x][y]).append("\t");
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

            Point userSelectPoint = getBoardPoint(e.getX(), e.getY());
            Point computerSelectPoint;

            if (insideBoard(userSelectPoint) && board.get(userSelectPoint) == 0) {

                // Người chơi đánh
                userPlay(userSelectPoint);

                // Bỏ highlight nước đánh gần nhất của COMPUTER
                drawCell(getGraphics(), lastCompPoint, false);

                if (!checkEndGame(userSelectPoint)) {
                    // Nếu người chơi đánh nhưng chưa thể kết thúc game
                    // Thì đến lượt máy đánh

                    // Tìm kiếm nước đi kế tiếp
                    computerSelectPoint = board.findSolution();

                    showScoreBoard();

                    // Dừng 1 khoảng thời gian để người chơi kịp nhìn thấy máy đánh :))
                    try {
                        Thread.sleep(300);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    // Máy đánh
                    computerPlay(computerSelectPoint);

                    // Kiểm tra xem máy đánh thì có kết thúc ván đấu hay chưa
                    checkEndGame(computerSelectPoint);
                }

            }
        }
    }


    public class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);

            // Highlight ô cờ đang được rê chuột

            Point p = getBoardPoint(e.getX(), e.getY());

            if (lastHoverPoint.equals(p)) {
                return;
            }

            // Bỏ highlight ô cũ
            if (insideBoard(lastHoverPoint)) {
                drawCell(getGraphics(), lastHoverPoint, false);
            }

            // Highlight ô đang hover
            if (insideBoard(p)) {
                drawCell(getGraphics(), p, true);
                lastHoverPoint.move(p.x, p.y);
            }
        }
    }


}

package com.vinh.caro;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

import static com.vinh.caro.utils.Constants.*;

/**
 * Create by VinhIT
 * On 24/07/2021
 */

public class Board {
    private final int[][] cell;     // ma trận biểu diễn bàn cờ
    private final int m, n;

    public int[][] scoreComp;       // bảng giá trị điểm của COMPUTER của mỗi ô cờ trên bàn cờ
    public int[][] scoreUser;       // bảng giá trị điểm của USER của mỗi ô cờ trên bàn cờ

    public Point winPoint;          // Đường 5 chiến thắng bắt đầu từ ô (winPoint.x; winPoint.y) theo hướng (wdx, wdy)
    public int wdx, wdy;

    private final Random random;

    private final int[] directX = {0, 1, 1, 1};
    private final int[] directY = {1, 0, 1, -1};


    public Board() {
        m = NUM_ROWS;
        n = NUM_COLS;

        cell = new int[m][n];
        scoreComp = new int[m][n];
        scoreUser = new int[m][n];

        init();

        random = new Random();
        winPoint = new Point();
    }

    public void init() {
        // Gán mặc định bàn cờ chưa có gì
        fill(cell, 0);
    }

    private void fill(int[][] a, int value) {
        for (int[] row : a)
            Arrays.fill(row, value);
    }

    /**
     * Tính toán và cập nhật ước lượng giá trị điểm của mỗi ô trên đường duyệt 5
     *
     * @param point  điểm xuất phát duyệt
     * @param dx     hướng duyệt theo chiều X
     * @param dy     hướng duyệt theo chiều Y
     * @param score  mảng giá trị điểm tương ứng với player đang xét
     * @param player USER hay là COMPUTER
     */
    void calculate(Point point, int dx, int dy, int[][] score, int player) {
        Point p = new Point();

        p.move(point.x + 4 * dx, point.y + 4 * dy);
        if (!insideBoard(p)) return;

        p.move(point.x, point.y);

        int cntUser = 0, cntComputer = 0;
        // cntUser:   Số lượng quân cờ người đánh
        // cntComputer: Số lượng quân cờ máy đánh

        int k = 0;

        // Trên đường 5 ô xuất phát từ (x;y)
        // Đếm xem có bao nhiêu quân cờ của người và của máy
        while (k++ < 5) {
            if (cell[p.x][p.y] == USER)
                cntUser++;
            else if (cell[p.x][p.y] == COMPUTER)
                cntComputer++;
            p.translate(dx, dy);
        }

        // Nếu như trên đường 5 ô đều có của người và máy
        // Thì có nghĩa đường này k thể phân định thắng thua
        // => Không cần tính toán nữa
        if (cntUser > 0 && cntComputer > 0) return;

        // => Trường hợp chỉ có 1 loại quân cờ (Hoặc k có) trên đường đó

        int mark = 1; // Giá trị ước lượng sự điểm của nước cờ
        int cnt = 0;

        if (player == COMPUTER)
            cnt = cntComputer;
        else if (player == USER)
            cnt = cntUser;


        // Nếu không có quân cờ nào thuộc loại đang xét thì dừng
        if (cnt == 0) return;

        // Giá trị ước lượng = 10^(n-1)  (với n là số quân cờ của USER/COMPUTER tùy theo loại cờ đáng xét)
        while (--cnt > 0) mark *= 10;

        // isNotBlocked: là xác định đường 5 có bị chặn 2 đầu bởi quân cờ đối thủ hay k?
        boolean isNotBlocked = true;

        // Xét chặn 2 đầu trên đường duyệt 5 (k tính bị chặn bởi đường biên)

        if (insideBoard(p)) {
            isNotBlocked = cell[p.x][p.y] != player;
        }

        p.move(p.x - 6 * dx, p.y - 6 * dy);

        if (insideBoard(p)) {
            isNotBlocked = isNotBlocked && cell[p.x][p.y] != player;
        }

        // Nếu không bị chặn 2 đầu, ước lượng điểm x2
        if (isNotBlocked) mark *= 2;

        // Duyệt lại đường 5, cập nhật giá trị ước lượng điểm của mỗi ô
        p.move(point.x, point.y);
        k = 0;
        while (k++ < 5) {
            score[p.x][p.y] += mark;
            p.translate(dx, dy);
        }

    }


    /**
     * Hàm Heuristic
     * Duyệt toàn bộ các đường 5 trên bàn cờ để tính toán giá trị điểm
     *
     * @param score  mảng điểm tương ứng với player muốn xét
     * @param player USER hay COMPUTER
     */
    public void evaluate(int[][] score, int player) {
        int i, j, k;

        // Reset mảng giá trị
        fill(score, 0);


        // Duyệt mỗi ô trên bàn cờ
        // Tại mỗi ô duyệt các đường 5 có thể để tính toán ước lượng giá trị điểm của mỗi ô cờ
        Point p = new Point();

        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                p.move(i, j);
                for (k = 0; k < directX.length; k++) {
                    calculate(p, directX[k], directY[k], score, player);
                }
            }
        }
    }

    /**
     * Kiểm tra 2 giá trị có tương đương nhau hay không
     * Tương đương khi:
     * Cùng số lượng chữ số
     * Chữ số bắt đầu giống nhau
     * VD: 3514 và 3521 thì trả về true, còn 3514 và 2514 thì false
     *
     * @param a giá trị muốn so sánh
     * @param b giá trị muôn so sánh
     * @return có tương đương nhau hay không?
     */
    boolean equivalent(int a, int b) {
        String s1 = String.valueOf(a);
        String s2 = String.valueOf(b);
        if (s1.length() != s2.length()) return false;
        return s1.charAt(0) == s2.charAt(0);
    }


    /**
     * Tìm kiếm nước đánh tối ưu cho máy
     *
     * @return nước cờ để máy đánh
     */
    public Point findSolution() {
        int maxUser, maxComp;

        Point pMaxUser = new Point();
        Point pMaxComp = new Point();

        Point pResult = new Point();

        // Tính hàm heuristic độ điểm các nước cờ của USER
        evaluate(scoreUser, USER);

        // Tính hàm heuristic độ điểm các nước cờ của COMPUTER
        evaluate(scoreComp, COMPUTER);

        maxUser = maxComp = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (cell[i][j] == 0) {
                    // Tìm max cho USER
                    if (maxUser < scoreUser[i][j]) {
                        maxUser = scoreUser[i][j];
                        pMaxUser.move(i, j);
                    } else if (maxUser == scoreUser[i][j]) {
                        if (random.nextInt() % 2 == 1) {
                            pMaxUser.move(i, j);
                        }
                    }

                    // Tìm max cho COMPUTER
                    if (maxComp < scoreComp[i][j]) {
                        maxComp = scoreComp[i][j];
                        pMaxComp.move(i, j);
                    } else if (maxComp == scoreComp[i][j]) {
                        if (random.nextInt() % 2 == 1) {
                            pMaxComp.move(i, j);
                        }
                    }
                }
            }
        }

        if (equivalent(maxUser, maxComp)) {
            // Nếu độ điểm nước cờ của USER và COMPUTER tương đương nhau

            if (maxComp >= 1000) {
                // Nếu đã có 4 ô rồi thì đánh thêm 1 ô nữa cho thắng luôn :v
                pResult = pMaxComp;
            } else {
                // Duyệt mỗi ô cờ chưa được đánh
                // Chọn ô cờ có giá trị tổng điểm của USER và COMPUTER lớn nhất để đánh

                int max = 0, sum;

                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        if (cell[i][j] == 0) {
                            sum = scoreComp[i][j] + scoreUser[i][j];
                            if (max < sum) {
                                max = sum;
                                pResult.move(i, j);
                            } else if (max == sum && random.nextInt() % 2 == 1) {
                                pResult.move(i, j);
                            }
                        }
                    }
                }
            }
        } else {
            // Nếu độ điểm không tương đương
            // Thì chọn cái lớn hơn
            if (maxUser > maxComp) {
                pResult = pMaxUser;
            } else {
                pResult = pMaxComp;
            }
        }

        return pResult;
    }


    /**
     * Kiểm tra đường 5 muốn duyệt có tạo thành chiến thắng được hay không?
     *
     * @param pt điểm bắt đầu duyệt
     * @param dx hướng duyệt theo chiều X
     * @param dy hướng duyệt theo chiều Y
     * @return đường 5 có đúng 5 quân cờ cùng loại hay không?
     */
    public boolean check5(Point pt, int dx, int dy) {
        Point p = new Point(pt);

        int cnt = 1;
        int x = pt.x, y = pt.y;

        int k = 0;
        while (++k < 5) {
            p.translate(dx, dy);
            if (insideBoard(p) && cell[x][y] == cell[p.x][p.y]) {
                cnt++;
            }
        }
        return cnt == 5;
    }

    /**
     * Kiểm tra trên toàn bàn cờ đã có đường 5 chiến thắng hay chưa?
     * Lưu lại tọa độ và hướng của đường 5 chiến thắng
     * Tọa độ thắng bắt đầu là winPoint
     * Hướng thắng (wdx; wdy)
     *
     * @return Có kết thúc game hay không?
     */
    public boolean checkWin() {
        Point p = new Point();
        int i, j, k;

        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                if (cell[i][j] == 0) continue;

                p.move(i, j);

                for (k = 0; k < directX.length; k++) {
                    if (check5(p, directX[k], directY[k])) {
                        winPoint.move(i, j);
                        wdx = directX[k];
                        wdy = directY[k];
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void set(Point p, int value) {
        cell[p.x][p.y] = value;
    }

    public void set(int x, int y, int value) {
        cell[x][y] = value;
    }

    public int get(Point p) {
        return cell[p.x][p.y];
    }

    public int get(int x, int y) {
        return cell[x][y];
    }

    public void clear(Point p) {
        cell[p.x][p.y] = 0;
    }

}

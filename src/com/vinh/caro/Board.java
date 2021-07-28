package com.vinh.caro;

import java.awt.*;
import java.util.Random;

import static com.vinh.caro.utils.Constants.*;

/**
 * Create by VinhIT
 * On 24/07/2021
 */

public class Board {
    private int[][] cell;       // ma trận biểu diễn bàn cờ
    int[][] score;      // bảng giá trị nguy hiểm của mỗi ô cờ trên bàn cờ
    int[][] scoreUser;  // bảng giá trị nguy hiểm của USER mỗi ô cờ trên bàn cờ
    int m, n, type;     // type: nước cờ xét là của USER hay COMPUTER
    private int resX, resY;     // Giá trị tọa độ kết quả nước đánh kế tiếp sau khi tính toán xong
    int wx, wy, wdx, wdy;   // Đường 5 chiến thắng bắt đầu từ ô (wx; wy) theo hướng (wdx, wdy)

    private final Random r;

    int[] directX = {0, 1, 1, 1};
    int[] directY = {1, 0, 1, -1};


    public Board() {
        m = NUM_ROWS;
        n = NUM_COLS;

        cell = new int[m][n];
        score = new int[m][n];
        scoreUser = new int[m][n];

        init();

        r = new Random();
    }

    public void init() {
        // Gán mặc định bàn cở chưa có gì
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                cell[i][j] = 0;
    }

    /**
     * Tính toán ước lượng giá trị nguy hiểm của mỗi ô trên đường duyệt 5
     *
     * @param x  tọa độ X xuất phát duyệt
     * @param y  tọa độ Y xuất phát duyệt
     * @param dx hướng duyệt theo chiều X
     * @param dy hướng duyệt theo chiều Y
     */
    void calculate(int x, int y, int dx, int dy) {
        int cntUser = 0, cntComputer = 0;
        // cntUser:   Số lượng quân cờ người đánh
        // cntComputer: Số lượng quân cờ máy đánh

        int i, j, k;

        i = x;
        j = y;
        k = 0;
        // Trên đường 5 ô xuất phát từ (x;y)
        // Đếm xem có bao nhiêu quân cờ của và của máy
        while (k++ < 5) {
            if (cell[i][j] == USER)
                cntUser++;
            else if (cell[i][j] == COMPUTER)
                cntComputer++;
            i += dx;
            j += dy;
        }

        // Nếu như trên đường 5 ô đều có của người và máy
        // Thì có nghĩa đường này k thể phân định thắng thua
        // => Không cần tính toán nữa
        if (cntUser > 0 && cntComputer > 0) return;

        // => Trường hợp chỉ có 1 loại quân cờ (Hoặc k có) trên đường đó

        int value = 1; // Giá trị ước lượng sự nguy hiểm của nước cờ
        int cnt = 0;

        if (type == COMPUTER)
            cnt = cntComputer;
        else if (type == USER)
            cnt = cntUser;


        // Nếu không có quân cờ nào thuộc loại đang xét thì dừng
        if (cnt == 0) return;

        // Giá trị ước lượng = 10^(n-1)  (với n là số quân cờ của USER/COMPUTER tùy theo loại cờ đáng xét)
        while (--cnt > 0) value *= 10;

        // isNotBlocked: là xác định đường 5 có bị chặn 2 đầu bởi quân cờ đối thủ hay k?
        boolean isNotBlocked = true;

        // Xét chặn 2 đầu trên đường duyệt 5 (k tính bị chặn bởi đường biên)
        // (headX; headY) và (tailX; tailY) là điểm trước và sau của đường duyệt 5
        int headX, headY, tailX, tailY;

        tailX = i;
        tailY = j;

        headX = tailX - 6 * dx;
        headY = tailY - 6 * dy;

        if (insideBoard(headX, headY)) {
            isNotBlocked = cell[headX][headY] != type;
        }

        if (insideBoard(tailX, tailY)) {
            isNotBlocked = isNotBlocked && cell[tailX][tailY] != type;
        }

        // Nếu không bị chặn 2 đầu, ước lượng nguy hiểm x2
        if (isNotBlocked) value *= 2;

        // Duyệt lại đường 5, cập nhật giá trị ước lượng nguy hiểm của mỗi ô
        i = x;
        j = y;
        k = 0;
        while (k++ < 5) {
            score[i][j] += value;
            i += dx;
            j += dy;
        }

    }

    /**
     * Dự đoán, ước lượng hàm heuristic
     */
    public void evaluate() {
        int i, j;

        // Reset mảng giá trị
        for (i = 0; i < m; i++)
            for (j = 0; j < n; j++)
                score[i][j] = 0;


        // Duyệt mỗi ô trên bàn cờ
        // Tại mỗi ô duyệt các đường 5 có thể để tính toán ước lượng giá trị nguy hiểm của mỗi ô cờ
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                if (j + 4 < n) {
                    // Xuống
                    calculate(i, j, 0, 1);
                }
                if (i + 4 < m && j + 4 < n) {
                    // Chéo xuống phải
                    calculate(i, j, 1, 1);
                }
                if (i + 4 < m) {
                    // Sang phải
                    calculate(i, j, 1, 0);
                }
                if (i + 4 < m && j >= 4) {
                    // Chéo lên phải
                    calculate(i, j, 1, -1);
                }
            }
        }
    }

    /**
     * Kiểm tra 2 giá trị có tương đương nhau hay không
     * So sánh từng chữ số từ trái qua phải
     * VD: 3514 và 3521 thì trả về true, còn 3514 và 2514 thì false
     *
     * @param d1 giá trị muốn so sánh
     * @param d2 giá trị muôn so sánh
     * @return có tương đương nhau hay không?
     */
    boolean equivalent(int d1, int d2) {
        int e1, e2, t;
        t = 1000;
        for (int i = 0; i < 3; i++) {
            e1 = d1 / t;
            e2 = d2 / t;
            if (e1 > 0 || e2 > 0) {
                return e1 == e2;
            }
            t = t / 10;
        }
        return true;
    }

    /**
     * Tìm kiếm nước đánh tối ưu
     */
    public Point findSolution() {
        int max1, max2;
        int li1 = 1, lj1 = 1, li2 = 1, lj2 = 1;

        // Tính hàm heuristic độ nguy hiểm các nước cờ của USER
        type = USER;
        evaluate();

        max1 = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                scoreUser[i][j] = score[i][j]; // lưu lại giá trị và scoreUser
                if (cell[i][j] == 0) {
                    if (max1 < score[i][j]) {
                        max1 = score[i][j];
                        li1 = i;
                        lj1 = j;
                    } else if (max1 == score[i][j]) {
                        if (r.nextInt() % 2 == 1) {
                            li1 = i;
                            lj1 = j;
                        }
                    }
                }
            }
        }

        // Tính hàm heuristic độ nguy hiểm các nước cờ của COMPUTER
        type = COMPUTER;
        evaluate();

        max2 = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (cell[i][j] == 0) {
                    if (max2 < score[i][j]) {
                        max2 = score[i][j];
                        li2 = i;
                        lj2 = j;
                    } else if (max2 == score[i][j]) {
                        if (r.nextInt() % 2 == 1) {
                            li2 = i;
                            lj2 = j;
                        }
                    }
                }
            }
        }

        int max = 0, li = -1, lj = -1;

        // scoreUser[][]:    bảng độ nguy hiểm các nước cờ của USER
        // score[][]:        bảng độ nguy hiểm các nước cờ của COMPUTER

        if (equivalent(max1, max2)) {
            // Nếu độ nguy hiểm nước cờ của USER và COMPUTER tương đương nhau


            if (max2 >= 1000) {

                // Nếu độ nguy hiểm của COMPUTER cao thì ưu tiên đánh
                resX = li2;
                resY = lj2;
            } else {

                // Duyệt mỗi ô cờ chưa được đánh
                // Chọn ô cờ có giá trị tổng nguy hiểm của USER và COMPUTER lớn nhất để đánh
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        if (cell[i][j] == 0) {
                            if (max < score[i][j] + scoreUser[i][j]) {
                                max = score[i][j] + scoreUser[i][j];
                                li = i;
                                lj = j;
                            }
                        }
                    }
                }

                resX = li;
                resY = lj;
            }
        } else {
            // Nếu độ nguy hiểm không tương đương
            // Thì chọn cái lớn hơn
            if (max1 > max2) {
                resX = li1;
                resY = lj1;
            } else {
                resX = li2;
                resY = lj2;
            }
        }

        return new Point(resX, resY);
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
                System.out.println(cell[p.x][p.y] + " = " + cell[x][y]);
            }
        }
        return cnt == 5;
    }

    /**
     * Kiểm tra trên toàn bàn cờ đã có đường 5 chiến thắng hay chưa?
     * Lưu lại tọa độ và hướng của đường 5 chiến thắng
     * Tọa độ thắng bắt đầu (wx; wy)
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

                p.setLocation(i, j);

                for (k = 0; k < directX.length; k++) {
                    if (check5(p, directX[k], directY[k])) {
                        wx = i;
                        wy = j;
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

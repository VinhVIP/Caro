package com.vinh.caro;

import java.util.Random;

import static com.vinh.caro.utils.Constants.*;

/**
 * Create by VinhIT
 * On 24/07/2021
 */

public class Table {
    int[][] cell;       // ma trận biểu diễn bàn cờ
    int[][] score;      // bảng giá trị nguy hiểm của mỗi ô cờ trên bàn cờ
    int[][] scoreUser;  // bảng giá trị nguy hiểm của USER mỗi ô cờ trên bàn cờ
    int m, n, turn;
    int resX, resY;     // Giá trị tọa độ kết quả nước đánh kế tiếp sau khi tính toán xong
    int wx, wy, wdx, wdy;   // Đường 5 chiến thắng bắt đầu từ ô (wx; wy) theo hướng (wdx, wdy)

    private final Random r;

    public Table() {
        m = NUM_ROWS;
        n = NUM_COLS;

        cell = new int[m][n];
        score = new int[m][n];
        scoreUser = new int[m][n];

        // Gán mặc định bàn cở chưa có gì
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                cell[i][j] = 0;

        r = new Random();
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
        int cntPlayer = 0, cntComputer = 0;
        // cntPlayer:   Số lượng quân cờ người đánh
        // cntComputer: Số lượng quân cờ máy đánh

        int i, j, k;

        i = x;
        j = y;
        k = 0;
        // Trên đường 5 ô xuất phát từ (x;y)
        // Đếm xem có bao nhiêu quân cờ của và của máy
        while (k++ < 5) {
            if (cell[i][j] == USER)
                cntPlayer++;
            else if (cell[i][j] == COMPUTER)
                cntComputer++;
            i += dx;
            j += dy;
        }

        // Nếu như trên đường 5 ô đều có của người và máy
        // Thì có nghĩa đường này k thể phân định thắng thua
        // => Không cần tính toán nữa
        if (cntPlayer > 0 && cntComputer > 0) return;

        // => Nếu chỉ có 1 loại quân cờ (Hoặc k có) trên đường đó

        if (turn == COMPUTER) {
            // Nếu máy đánh thì gán số quân cờ người = máy
            cntPlayer = cntComputer;
        }

        // Nếu như người k có quân nào thì không cần xét
        // Do đường đánh 5 này k có gì nguy hiểm
        if (cntPlayer == 0) {
            return;
        }

        int value = 1; // Giá trị ước lượng sự nguy hiểm của nước cờ

        // Giá trị ước lượng = 10^n  (với n là số quân cờ của người chơi)
        while (--cntPlayer > 0) value *= 10;

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
            isNotBlocked = cell[headX][headY] != turn;
        }

        if (insideBoard(tailX, tailY)) {
            isNotBlocked = isNotBlocked && cell[tailX][tailY] != turn;
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
        int e1, e2, t, i;
        t = 1000;
        for (i = 1; i <= 3; i++) {
            e1 = d1 / t;
            e2 = d2 / t;
            if ((e1 > 0) || (e2 > 0)) {
                return e1 == e2;
            }
            t = t / 10;
        }
        return true;
    }

    /**
     * Tìm kiếm nước đánh tối ưu
     */
    public void findSolution() {
        int max1, max2;
        int li1 = 1, lj1 = 1, li2 = 1, lj2 = 1;

        // Tính hàm heuristic độ nguy hiểm các nước cờ của USER
        turn = USER;
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
        turn = COMPUTER;
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
    }


    /**
     * Kiểm tra đường 5 muốn duyệt có tạo thành chiến thắng được hay không?
     *
     * @param x  vị trí X bắt đầu duyệt
     * @param y  vị trí Y bắt đầu duyệt
     * @param dx hướng duyệt theo chiều X
     * @param dy hướng duyệt theo chiều Y
     * @return đường 5 có đúng 5 quân cờ cùng loại hay không?
     */
    public boolean check5(int x, int y, int dx, int dy) {
        int cnt = 1;
        int i = x, j = y;

        for (int k = 1; k < 5; k++) {
            i += dx;
            j += dy;
            if (insideBoard(i, j) && cell[x][y] == cell[i][j]) {
                cnt++;
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
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (cell[i][j] == 0) continue;

                if (check5(i, j, 0, 1)) {
                    wx = i;
                    wy = j;
                    wdx = 0;
                    wdy = 1;
                    return true;
                }
                if (check5(i, j, 1, 0)) {
                    wx = i;
                    wy = j;
                    wdx = 1;
                    wdy = 0;
                    return true;
                }
                if (check5(i, j, 1, 1)) {
                    wx = i;
                    wy = j;
                    wdx = 1;
                    wdy = 1;
                    return true;
                }
                if (check5(i, j, 1, -1)) {
                    wx = i;
                    wy = j;
                    wdx = 1;
                    wdy = -1;
                    return true;
                }
            }
        }
        return false;
    }

    public void showResult() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                System.out.print(score[row][col]);
            }
        }
    }

}

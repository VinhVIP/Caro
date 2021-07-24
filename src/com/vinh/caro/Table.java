package com.vinh.caro;

import java.util.Random;

/**
 * Create by VinhIT
 * On 24/07/2021
 */

public class Table {
    int[][] cell;
    int[][] score;
    int[][] score1;
    int m, n, kh, resX, resY;
    int wx, wy, wdx, wdy;
    // kh: Giắ trị cell của ô ( 1 hoặc 2 : X hoặc O)
    Random r;

    public Table() {
        m = DrawCanvas.WIDTH;
        n = DrawCanvas.HEIGHT;

        cell = new int[m][n];
        score = new int[m][n];
        score1 = new int[m][n];

        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                cell[i][j] = 0;

        r = new Random();
    }

    void tinh(int x, int y, int dx, int dy) {
        int i, j, k, d1, d2, s;

        i = x;
        j = y;
        d1 = d2 = 0;
        // d1: Số lượng quân cờ người đánh
        // d2: Số lượng quân cờ máy đánh

        // Trên đường 5 ô xuất phát từ (x;y)
        // Đếm xem có bao nhiêu quân cờ của người (d1), của máy (d2)
        for (k = 0; k <= 4; k++) {
            if (cell[i][j] == 1) {
                // Human
                d1++;
            } else if (cell[i][j] == 2) {
                //Computer
                d2++;
            }
            i += dx;
            j += dy;
        }

        // Nếu như trên đường 5 ô đều có của người và máy
        // Thì có nghĩa đường này k thể phân định thắng thua
        // Không cần tính toán nữa
        if (d1 > 0 && d2 > 0) return;

        // Nếu chỉ có 1 loại quân cờ (Hoặc k có) trên đường đó

        if (kh == 2) {
            // Nếu máy đánh thì gán số quân cờ người = máy
            d1 = d2;
        }

        // Nếu như người k có quân nào thì dừng
        if (d1 == 0) {
            return;
        }

        s = 1; // Giá trị ước lượng
        for (k = 2; k <= d1; k++) {
            s *= 10;
        }

        // ok là xác định đường 5 có bị chặn 2 đầu bởi quân cờ đối định
        // với quân cờ đang xét hay không?
        boolean ok = true;

        // (i,j) là ô ngoài mút cuối cùng trên đường duyệt 5
        // Gọi nó là con của đường 5 cũng đc

        if (i >= 0 && i < m && j >= 0 && j < n) {
            ok = (cell[i][j] != kh);
        }

        // (i,j) là ô nằm ngoài mút đầu tiên của đường duyệt 5
        // (i,j) k thuộc đường 5
        // Gọi nó là cha của đường 5 cũng đc
        // Cha và con ở trên kia là 2 ô chặn 2 đầu đường 5
        i = i - 6 * dx;
        j = j - 6 * dy;

        if (i >= 0 && i < m && j >= 0 && j < n) {
            ok = ok && (cell[i][j] != kh);
        }

        // Nếu không bị chặn 2 đầu, ước lượng x2
        if (ok) s *= 2;


        // Duyệt là đường 5
        i = x;
        j = y;
        for (k = 0; k <= 4; k++) {
            // tăng giá trị ước lượng của mỗi ô thêm s
            score[i][j] += s;
            i += dx;
            j += dy;
        }
    }

    // Dự đoán, ước lượng hàm heuristic
    public void evaluate() {
        int i, j;

        for (i = 0; i < m; i++)
            for (j = 0; j < n; j++)
                score[i][j] = 0;

        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                if (j + 4 < n) {
                    // Xuống
                    tinh(i, j, 0, 1);
                }
                if (i + 4 < m && j + 4 < n) {
                    // Chéo xuống phải
                    tinh(i, j, 1, 1);
                }
                if (i + 4 < m) {
                    // Sang phải
                    tinh(i, j, 1, 0);
                }
                if (i + 4 < m && j >= 4) {
                    // Chéo lên phải
                    tinh(i, j, 1, -1);
                }
            }
        }
    }

    boolean equivalent(int d1, int d2) {
        int e1, e2, t, i;
        t = 1000;
        for (i = 1; i <= 3; i++) {
            e1 = d1 / t;
            e2 = d2 / t;
            if ((e1 > 0) || (e2 > 0)) {
                if (e1 == e2) {
                    return true;
                } else {
                    return false;
                }
            }
            t = t / 10;
        }
        return true;
    }

    public void findSolution() {
        int max1, max2;
        int li1 = 1, lj1 = 1, li2 = 1, lj2 = 1;

        max1 = 0;
        kh = 1;
        evaluate();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                score1[i][j] = score[i][j];
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

        max2 = 0;
        kh = 2;
        evaluate();
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

        if (equivalent(max1, max2)) {
            if (max2 >= 1000) {
                resX = li2;
                resY = lj2;
            } else {
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        if (cell[i][j] == 0) {
                            if (max < score[i][j] + score1[i][j]) {
                                max = score[i][j] + score1[i][j];
                                li = i;
                                lj = j;
                            }
                        }
                    }
                }
                resX = li;
                resY = lj;

                if (resX == -1 || resY == -1) {
                    if (max1 > max2) {
                        resX = li1;
                        resY = lj1;
                    } else {
                        resX = li2;
                        resY = lj2;
                    }
                }
            }
        } else {
            if (max1 > max2) {
                resX = li1;
                resY = lj1;
            } else {
                resX = li2;
                resY = lj2;
            }
        }
    }

    public boolean check5(int i, int j, int dx, int dy) {
        int cnt = 1;
        int x = i, y = j;
        for (int k = 1; k <= 4; k++) {
            x += dx;
            y += dy;
            if (x >= 0 && x < m && y >= 0 && y < n) {
                if (cell[x][y] == cell[i][j]) cnt++;
            }
        }
        return cnt == 5;
    }

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

}

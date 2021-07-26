package com.vinh.caro.utils;

/**
 * Create by VinhIT
 * On 25/07/2021
 */

public class Constants {
    public static final int USER = 1;
    public static final int COMPUTER = 2;

    public static final int NUM_ROWS = 20;
    public static final int NUM_COLS = 20;

    public static final int CELL_SIZE = 40;

    public static final int GRID_COLOR = 0x47A4BA;
    public static final int CELL_HOVER_COLOR = 0xFFE5F1EC;
    public static final int CELL_COLOR = 0xffffff;
    public static final int CELL_HIGHLIGHT_COLOR = 0xffff00;

    public static final int BOARD_WIDTH = NUM_COLS * CELL_SIZE + 10;
    public static final int BOARD_HEIGHT = NUM_ROWS * CELL_SIZE + 10;

    public static boolean insideBoard(int x, int y) {
        return x >= 0 && x < NUM_COLS &&
                y >= 0 && y < NUM_ROWS;
    }
}

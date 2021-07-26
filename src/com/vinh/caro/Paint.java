package com.vinh.caro;

import javax.swing.*;

/**
 * Create by VinhIT
 * On 23/07/2021
 */

public class Paint extends JFrame {
    private JPanel mainPanel;
    private JLabel labelComp;
    private JLabel labelUser;
    private JPanel rootPanel;

    private DrawCanvas canvas;

    public Paint() {
        setTitle("Caro AI beta");
        setSize(1100, 880);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        canvas = new DrawCanvas(this);
        mainPanel.add(canvas);

        newGame();

        add(rootPanel);
        setLocation(250, 50);
    }

    public void newGame(){
        new Prepare(this);
    }

    public void setup(boolean isUserFirst, boolean isXFirst) {
        canvas.setup(isUserFirst, isXFirst);
    }

}

package com.vinh.caro;

import javax.swing.*;

/**
 * Create by VinhIT
 * On 23/07/2021
 */

public class Paint extends JFrame {
    private JPanel mainPanel;
    private JTextArea txtComputer;
    private JPanel rootPanel;
    private JTextArea txtUser;
    private JButton btnUndo;
    private JButton btnInfo;

    private final DrawCanvas canvas;

    public Paint() {
        setTitle("Caro AI beta");
        setSize(1600, 900);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        txtComputer.setTabSize(5);
        txtUser.setTabSize(5);

        btnUndo.addActionListener(v -> undo());
        btnInfo.addActionListener(v -> showInfo());

        canvas = new DrawCanvas(this);
        mainPanel.add(canvas);


        add(rootPanel);
        setLocation(150, 50);
        setVisible(true);

        newGame();
    }

    public void undo() {
        canvas.undo();
    }

    public void showInfo() {
        new Information();
    }

    public void newGame() {
        new Prepare(this);
    }

    public void setup(boolean isUserFirst, boolean isXFirst) {
        canvas.setup(isUserFirst, isXFirst);
    }

    public void setComputerBoard(String content) {
        txtComputer.setText(content);
    }

    public void setUserBoard(String content) {
        txtUser.setText(content);
    }
}

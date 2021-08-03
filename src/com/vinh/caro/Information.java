package com.vinh.caro;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Information extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton btnClipBoard;
    private JLabel labelSrc;

    public Information() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        btnClipBoard.addActionListener(e -> {
            String src = labelSrc.getText();
            StringSelection stringSelection = new StringSelection(src);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });


        setTitle("Thông tin");
        setSize(700, 300);
        setLocation(800, 300);
        pack();
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }
}

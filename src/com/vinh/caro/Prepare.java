package com.vinh.caro;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Prepare extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JRadioButton radioUser;
    private JRadioButton radioComputer;
    private JRadioButton radioX;
    private JRadioButton radioO;

    private final ButtonGroup groupPlayer;
    private final ButtonGroup groupXO;

    private final Paint paint;

    public Prepare(Paint paint) {
        this.paint = paint;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        groupPlayer = new ButtonGroup();
        groupPlayer.add(radioUser);
        groupPlayer.add(radioComputer);
        radioUser.setSelected(true);

        groupXO = new ButtonGroup();
        groupXO.add(radioX);
        groupXO.add(radioO);
        radioX.setSelected(true);

        this.setTitle("Cài đặt");
        this.pack();
        this.setLocation(700, 300);
        this.setVisible(true);

    }

    private void onOK() {
        paint.setup(radioUser.isSelected(), radioX.isSelected());
        dispose();
    }

    private void onCancel() {
        paint.setup(true, true);
        dispose();
    }

}

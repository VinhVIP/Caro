package com.vinh.caro;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Information extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton btnOpenSource;
    private JLabel labelSrc;

    public Information() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        btnOpenSource.addActionListener(e -> {
            try {
                openWebpage(new URL(labelSrc.getText()));
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            }
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


    public boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

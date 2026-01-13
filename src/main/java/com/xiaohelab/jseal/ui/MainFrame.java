package com.xiaohelab.jseal.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;

    public MainFrame(){
        initFrame();
        initComponents();
    }

    private void initFrame(){
        setTitle("JSeal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
    }
    
    private void initComponents(){
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);


    }
}

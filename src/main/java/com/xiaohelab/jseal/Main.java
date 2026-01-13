package com.xiaohelab.jseal;

import com.formdev.flatlaf.FlatLightLaf;
import com.xiaohelab.jseal.ui.MainFrame; //导入主界面类
import org.bouncycastle.jce.provider.BouncyCastleProvider; //导入BouncyCastle加密提供者包

import javax.swing.*; //导入Swing包
import java.security.Security; //导入安全包

public class Main{
    public static void main(String[] args){
        //注册BouncyCastle，以支持加密算法
        Security.addProvider(new BouncyCastleProvider());

        //设置FlatLaf主题
        try{
            FlatLightLaf.setup();
        }catch(Exception e){
            System.err.println("Failed to setup FlatLaf theme");
        }

        //启动主界面
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
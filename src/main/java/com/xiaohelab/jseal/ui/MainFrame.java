package com.xiaohelab.jseal.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.xiaohelab.jseal.ui.panel.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private Map<String, JButton> menuButtons = new HashMap<>();
    private JButton activeMenuBtn = null;

    // 功能面板
    private EncryptionPanel encryptionPanel;
    private DecryptionPanel decryptionPanel;
    private PermissionPanel permissionPanel;
    private SignaturePanel signaturePanel;
    private CertificatePanel certificatePanel;
    private BatchPanel batchPanel;
    private HistoryPanel historyPanel;

    public MainFrame() {
        initWindow();
        initLayout();
    }

    private void initWindow() {
        setTitle("JSeal - PDF安全工具");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
    }

    private void initLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // 1. 创建左侧侧边栏
        JPanel sidebar = createSidebar();
        contentPane.add(sidebar, BorderLayout.WEST);

        // 2. 创建右侧内容区 (CardLayout)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // 初始化所有功能面板
        encryptionPanel = new EncryptionPanel();
        decryptionPanel = new DecryptionPanel();
        permissionPanel = new PermissionPanel();
        signaturePanel = new SignaturePanel();
        certificatePanel = new CertificatePanel();
        batchPanel = new BatchPanel();
        historyPanel = new HistoryPanel();

        // 添加面板到CardLayout
        cardPanel.add(encryptionPanel, "nav_encrypt");
        cardPanel.add(decryptionPanel, "nav_decrypt");
        cardPanel.add(permissionPanel, "nav_perm");
        cardPanel.add(signaturePanel, "nav_sign");
        cardPanel.add(certificatePanel, "nav_cert");
        cardPanel.add(batchPanel, "nav_batch");
        cardPanel.add(historyPanel, "nav_history");

        contentPane.add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        // 侧边栏背景色稍深一点，区分层次
        sidebar.setBackground(new Color(40, 44, 52)); 

        // A. Logo 区域
        JLabel logoLabel = new JLabel("JSeal Security", JLabel.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        sidebar.add(logoLabel, BorderLayout.NORTH);

        // B. 菜单列表
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false); // 透明背景

        // 添加导航按钮
        addMenuButton(menuContainer, "🔒 加密保护", "nav_encrypt", true); // 默认选中
        addMenuButton(menuContainer, "🔓 移除密码", "nav_decrypt", false);
        addMenuButton(menuContainer, "🛡️ 权限修改", "nav_perm", false);
        addMenuButton(menuContainer, "✍️ 数字签名", "nav_sign", false);
        addMenuButton(menuContainer, "📜 证书管理", "nav_cert", false);
        menuContainer.add(Box.createVerticalStrut(20)); // 分隔符
        addMenuButton(menuContainer, "📦 批量处理", "nav_batch", false);
        addMenuButton(menuContainer, "📋 操作历史", "nav_history", false);

        // 把菜单放在 ScrollPane 里，防止小屏幕显示不全
        JScrollPane scrollPane = new JScrollPane(menuContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        sidebar.add(scrollPane, BorderLayout.CENTER);
        
        // C. 底部版权
        JLabel versionLabel = new JLabel("v1.0.0", JLabel.CENTER);
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        sidebar.add(versionLabel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addMenuButton(JPanel container, String text, String cardName, boolean isActive) {
        JButton btn = new JButton(text);
        
        // --- 样式设置 ---
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        btn.setForeground(new Color(180, 180, 180));
        btn.setBackground(isActive ? new Color(60, 65, 75) : new Color(40, 44, 52));
        btn.setBorder(new EmptyBorder(12, 25, 12, 10)); // 增加左边距
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 让按钮占满宽度
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 45));
        
        // --- 点击事件 ---
        btn.addActionListener(e -> {
            // 切换页面
            cardLayout.show(cardPanel, cardName);
            // 切换按钮样式
            updateActiveButton(cardName);
        });

        menuButtons.put(cardName, btn);
        container.add(btn);
        
        if (isActive) {
            activeMenuBtn = btn;
            btn.setForeground(Color.WHITE);
        }
    }
    
    // 更新选中状态的高亮
    private void updateActiveButton(String activeCardName) {
        if (activeMenuBtn != null) {
            activeMenuBtn.setBackground(new Color(40, 44, 52)); // 还原旧按钮
            activeMenuBtn.setForeground(new Color(180, 180, 180));
        }
        JButton current = menuButtons.get(activeCardName);
        if (current != null) {
            current.setBackground(new Color(60, 65, 75)); // 高亮新按钮
            current.setForeground(Color.WHITE);
            activeMenuBtn = current;
        }
    }

    /**
     * 获取历史记录面板（供其他面板添加记录）
     */
    public HistoryPanel getHistoryPanel() {
        return historyPanel;
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
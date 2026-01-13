package com.xiaohelab.jseal.ui.panel;

import com.xiaohelab.jseal.common.enums.EncryptionAlgorithm;
import com.xiaohelab.jseal.service.PdfEncryptionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.EnumSet;

/**
 * PDF加密面板
 */
public class EncryptionPanel extends JPanel {

    private JTextField filePathField;
    private JPasswordField userPasswordField;
    private JPasswordField ownerPasswordField;
    private JComboBox<EncryptionAlgorithm> algorithmCombo;
    private JTextField outputPathField;
    private JButton encryptButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    private File selectedFile;
    private final PdfEncryptionService encryptionService = new PdfEncryptionService();

    public EncryptionPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("PDF 加密保护");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // 文件选择区域
        contentPanel.add(createFileSelectionPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 密码设置区域
        contentPanel.add(createPasswordPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 加密选项区域
        contentPanel.add(createOptionsPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 输出设置区域
        contentPanel.add(createOutputPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // 操作按钮区域
        contentPanel.add(createActionPanel());

        add(contentPanel, BorderLayout.CENTER);

        // 状态栏
        JPanel statusPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        statusLabel = new JLabel(" ");
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createFileSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(new TitledBorder("选择PDF文件"));

        filePathField = new JTextField();
        filePathField.setEditable(false);
        JButton browseButton = new JButton("浏览...");
        browseButton.addActionListener(e -> selectFile());

        panel.add(filePathField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("密码设置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户密码
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("用户密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        userPasswordField = new JPasswordField(20);
        panel.add(userPasswordField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("(打开PDF时需要)"), gbc);

        // 所有者密码
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("所有者密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        ownerPasswordField = new JPasswordField(20);
        panel.add(ownerPasswordField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("(修改权限时需要)"), gbc);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        return panel;
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("加密选项"));

        panel.add(new JLabel("加密算法:"));
        algorithmCombo = new JComboBox<>(EncryptionAlgorithm.values());
        algorithmCombo.setSelectedItem(EncryptionAlgorithm.AES_256);
        panel.add(algorithmCombo);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(new TitledBorder("输出设置"));

        outputPathField = new JTextField();
        JButton browseButton = new JButton("浏览...");
        browseButton.addActionListener(e -> selectOutputFile());

        panel.add(new JLabel("输出文件:"), BorderLayout.WEST);
        panel.add(outputPathField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        encryptButton = new JButton("🔒 开始加密");
        encryptButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        encryptButton.setPreferredSize(new Dimension(150, 40));
        encryptButton.addActionListener(e -> performEncryption());

        JButton clearButton = new JButton("清除");
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearForm());

        panel.add(encryptButton);
        panel.add(clearButton);

        return panel;
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF文件", "pdf"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            
            // 自动设置输出文件名
            String outputName = selectedFile.getName().replace(".pdf", "_encrypted.pdf");
            File outputFile = new File(selectedFile.getParent(), outputName);
            outputPathField.setText(outputFile.getAbsolutePath());
        }
    }

    private void selectOutputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF文件", "pdf"));
        if (selectedFile != null) {
            chooser.setCurrentDirectory(selectedFile.getParentFile());
        }
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }
            outputPathField.setText(file.getAbsolutePath());
        }
    }

    private void performEncryption() {
        // 验证输入
        if (selectedFile == null || !selectedFile.exists()) {
            showError("请选择一个有效的PDF文件");
            return;
        }

        char[] userPwd = userPasswordField.getPassword();
        char[] ownerPwd = ownerPasswordField.getPassword();

        if (userPwd.length == 0 && ownerPwd.length == 0) {
            showError("请至少设置一个密码");
            return;
        }

        String outputPath = outputPathField.getText().trim();
        if (outputPath.isEmpty()) {
            showError("请指定输出文件路径");
            return;
        }

        // 禁用按钮，显示进度
        encryptButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        statusLabel.setText("正在加密...");

        // 后台执行加密
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                File outputFile = new File(outputPath);
                EncryptionAlgorithm algorithm = 
                        (EncryptionAlgorithm) algorithmCombo.getSelectedItem();

                encryptionService.encryptWithPassword(
                        selectedFile,
                        outputFile,
                        new String(userPwd),
                        new String(ownerPwd),
                        algorithm
                );
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                encryptButton.setEnabled(true);
                try {
                    get();
                    statusLabel.setText("✅ 加密成功！");
                    JOptionPane.showMessageDialog(EncryptionPanel.this,
                            "PDF加密成功！\n输出文件: " + outputPath,
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    statusLabel.setText("❌ 加密失败");
                    showError("加密失败: " + e.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void clearForm() {
        selectedFile = null;
        filePathField.setText("");
        userPasswordField.setText("");
        ownerPasswordField.setText("");
        outputPathField.setText("");
        statusLabel.setText(" ");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}

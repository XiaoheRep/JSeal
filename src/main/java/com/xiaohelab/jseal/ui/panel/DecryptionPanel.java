package com.xiaohelab.jseal.ui.panel;

import com.xiaohelab.jseal.service.PdfDecryptionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * PDF解密面板
 */
public class DecryptionPanel extends JPanel {

    private JTextField filePathField;
    private JPasswordField passwordField;
    private JTextField outputPathField;
    private JButton decryptButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel fileInfoLabel;

    private File selectedFile;
    private final PdfDecryptionService decryptionService = new PdfDecryptionService();

    public DecryptionPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("PDF 移除密码");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // 文件选择区域
        contentPanel.add(createFileSelectionPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 文件信息区域
        contentPanel.add(createFileInfoPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 密码输入区域
        contentPanel.add(createPasswordPanel());
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
        panel.setBorder(new TitledBorder("选择加密的PDF文件"));

        filePathField = new JTextField();
        filePathField.setEditable(false);
        JButton browseButton = new JButton("浏览...");
        browseButton.addActionListener(e -> selectFile());

        panel.add(filePathField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return panel;
    }

    private JPanel createFileInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("文件信息"));

        fileInfoLabel = new JLabel("请选择PDF文件");
        fileInfoLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        panel.add(fileInfoLabel, BorderLayout.CENTER);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(new TitledBorder("输入密码"));

        panel.add(new JLabel("PDF密码:"));
        passwordField = new JPasswordField(25);
        panel.add(passwordField);

        JButton verifyButton = new JButton("验证");
        verifyButton.addActionListener(e -> verifyPassword());
        panel.add(verifyButton);

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

        decryptButton = new JButton("🔓 移除密码");
        decryptButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        decryptButton.setPreferredSize(new Dimension(150, 40));
        decryptButton.addActionListener(e -> performDecryption());

        JButton clearButton = new JButton("清除");
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearForm());

        panel.add(decryptButton);
        panel.add(clearButton);

        return panel;
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF文件", "pdf"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());

            // 检查文件是否加密
            boolean encrypted = decryptionService.isEncrypted(selectedFile);
            if (encrypted) {
                fileInfoLabel.setText("🔒 此文件已加密，需要密码才能解锁");
            } else {
                fileInfoLabel.setText("✅ 此文件未加密，无需解密");
            }

            // 自动设置输出文件名
            String outputName = selectedFile.getName().replace(".pdf", "_decrypted.pdf");
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

    private void verifyPassword() {
        if (selectedFile == null) {
            showError("请先选择PDF文件");
            return;
        }

        String password = new String(passwordField.getPassword());
        boolean valid = decryptionService.verifyPassword(selectedFile, password);

        if (valid) {
            statusLabel.setText("✅ 密码正确");
            JOptionPane.showMessageDialog(this, "密码验证成功！", 
                    "验证成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setText("❌ 密码错误");
            JOptionPane.showMessageDialog(this, "密码错误，请重试", 
                    "验证失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDecryption() {
        // 验证输入
        if (selectedFile == null || !selectedFile.exists()) {
            showError("请选择一个有效的PDF文件");
            return;
        }

        String password = new String(passwordField.getPassword());
        String outputPath = outputPathField.getText().trim();

        if (outputPath.isEmpty()) {
            showError("请指定输出文件路径");
            return;
        }

        // 禁用按钮，显示进度
        decryptButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        statusLabel.setText("正在解密...");

        // 后台执行解密
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                File outputFile = new File(outputPath);
                decryptionService.decrypt(selectedFile, outputFile, password);
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                decryptButton.setEnabled(true);
                try {
                    get();
                    statusLabel.setText("✅ 解密成功！");
                    JOptionPane.showMessageDialog(DecryptionPanel.this,
                            "PDF解密成功！密码保护已移除。\n输出文件: " + outputPath,
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    statusLabel.setText("❌ 解密失败");
                    showError("解密失败: " + e.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void clearForm() {
        selectedFile = null;
        filePathField.setText("");
        passwordField.setText("");
        outputPathField.setText("");
        fileInfoLabel.setText("请选择PDF文件");
        statusLabel.setText(" ");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}

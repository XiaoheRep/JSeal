package com.xiaohelab.jseal.ui.panel;

import com.xiaohelab.jseal.service.CertificateService;
import com.xiaohelab.jseal.service.PdfSignatureService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * PDF数字签名面板
 */
public class SignaturePanel extends JPanel {

    private JTextField filePathField;
    private JComboBox<CertificateItem> certificateCombo;
    private JPasswordField certPasswordField;
    private JTextField reasonField;
    private JTextField locationField;
    private JTextField contactField;
    private JTextField outputPathField;
    private JButton signButton;
    private JButton verifyButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JTextArea signatureInfoArea;

    private File selectedFile;
    private File certificateDir;
    private final PdfSignatureService signatureService = new PdfSignatureService();
    private final CertificateService certificateService = new CertificateService();

    public SignaturePanel() {
        initCertificateDir();
        initUI();
        loadCertificates();
    }

    private void initCertificateDir() {
        String userHome = System.getProperty("user.home");
        certificateDir = new File(userHome, ".jseal/certificates");
        if (!certificateDir.exists()) {
            certificateDir.mkdirs();
        }
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("PDF 数字签名");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // 文件选择区域
        contentPanel.add(createFileSelectionPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 证书选择区域
        contentPanel.add(createCertificatePanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 签名信息区域
        contentPanel.add(createSignatureInfoPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 输出设置区域
        contentPanel.add(createOutputPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 已有签名信息区域
        contentPanel.add(createExistingSignaturesPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // 操作按钮区域
        contentPanel.add(createActionPanel());

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

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

    private JPanel createCertificatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("签名证书"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 证书选择
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("选择证书:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        certificateCombo = new JComboBox<>();
        panel.add(certificateCombo, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> loadCertificates());
        panel.add(refreshBtn, gbc);

        // 证书密码
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("证书密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        certPasswordField = new JPasswordField(20);
        panel.add(certPasswordField, gbc);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        return panel;
    }

    private JPanel createSignatureInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("签名信息 (可选)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 签名原因
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("签名原因:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        reasonField = new JTextField("文档审批", 30);
        panel.add(reasonField, gbc);

        // 签名位置
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("签名位置:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        locationField = new JTextField("", 30);
        panel.add(locationField, gbc);

        // 联系信息
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("联系信息:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        contactField = new JTextField("", 30);
        panel.add(contactField, gbc);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
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

    private JPanel createExistingSignaturesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("已有签名信息"));

        signatureInfoArea = new JTextArea(5, 40);
        signatureInfoArea.setEditable(false);
        signatureInfoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        signatureInfoArea.setText("选择PDF文件后点击\"验证签名\"查看已有签名");

        panel.add(new JScrollPane(signatureInfoArea), BorderLayout.CENTER);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        signButton = new JButton("✍️ 签名");
        signButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        signButton.setPreferredSize(new Dimension(120, 40));
        signButton.addActionListener(e -> performSign());

        verifyButton = new JButton("🔍 验证签名");
        verifyButton.setPreferredSize(new Dimension(120, 40));
        verifyButton.addActionListener(e -> verifySignatures());

        JButton clearButton = new JButton("清除");
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearForm());

        panel.add(signButton);
        panel.add(verifyButton);
        panel.add(clearButton);

        return panel;
    }

    private void loadCertificates() {
        certificateCombo.removeAllItems();

        File[] files = certificateDir.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".p12") || name.toLowerCase().endsWith(".pfx"));

        if (files != null && files.length > 0) {
            for (File file : files) {
                certificateCombo.addItem(new CertificateItem(file));
            }
        } else {
            certificateCombo.addItem(new CertificateItem(null));
        }
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF文件", "pdf"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());

            // 自动设置输出文件名
            String outputName = selectedFile.getName().replace(".pdf", "_signed.pdf");
            File outputFile = new File(selectedFile.getParent(), outputName);
            outputPathField.setText(outputFile.getAbsolutePath());

            // 检查是否已签名
            try {
                if (signatureService.isSigned(selectedFile)) {
                    statusLabel.setText("ℹ️ 此文件已包含数字签名");
                } else {
                    statusLabel.setText("📄 此文件尚未签名");
                }
            } catch (Exception e) {
                statusLabel.setText("⚠️ 无法读取文件信息");
            }
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

    private void performSign() {
        // 验证输入
        if (selectedFile == null || !selectedFile.exists()) {
            showError("请选择一个有效的PDF文件");
            return;
        }

        CertificateItem certItem = (CertificateItem) certificateCombo.getSelectedItem();
        if (certItem == null || certItem.getFile() == null) {
            showError("请选择一个证书，如果没有证书请先在\"证书管理\"中生成");
            return;
        }

        char[] certPassword = certPasswordField.getPassword();
        if (certPassword.length == 0) {
            showError("请输入证书密码");
            return;
        }

        String outputPath = outputPathField.getText().trim();
        if (outputPath.isEmpty()) {
            showError("请指定输出文件路径");
            return;
        }

        // 禁用按钮，显示进度
        signButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        statusLabel.setText("正在签名...");

        // 后台执行签名
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 加载证书
                KeyStore keyStore = certificateService.loadKeyStore(
                        certItem.getFile(), certPassword);
                String alias = keyStore.aliases().nextElement();

                // 执行签名
                File outputFile = new File(outputPath);
                signatureService.sign(
                        selectedFile,
                        outputFile,
                        keyStore,
                        alias,
                        certPassword,
                        reasonField.getText().trim(),
                        locationField.getText().trim(),
                        contactField.getText().trim()
                );
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                signButton.setEnabled(true);
                try {
                    get();
                    statusLabel.setText("✅ 签名成功！");
                    JOptionPane.showMessageDialog(SignaturePanel.this,
                            "PDF签名成功！\n输出文件: " + outputPath,
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    statusLabel.setText("❌ 签名失败");
                    showError("签名失败: " + e.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void verifySignatures() {
        if (selectedFile == null || !selectedFile.exists()) {
            showError("请先选择一个PDF文件");
            return;
        }

        try {
            List<PdfSignatureService.SignatureInfo> signatures =
                    signatureService.verifySignatures(selectedFile);

            if (signatures.isEmpty()) {
                signatureInfoArea.setText("此PDF文件没有数字签名");
            } else {
                StringBuilder sb = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                int i = 1;
                for (PdfSignatureService.SignatureInfo sig : signatures) {
                    sb.append("=== 签名 #").append(i++).append(" ===\n");
                    if (sig.getName() != null) sb.append("签名者: ").append(sig.getName()).append("\n");
                    if (sig.getReason() != null) sb.append("原因: ").append(sig.getReason()).append("\n");
                    if (sig.getLocation() != null) sb.append("位置: ").append(sig.getLocation()).append("\n");
                    if (sig.getContactInfo() != null) sb.append("联系方式: ").append(sig.getContactInfo()).append("\n");
                    if (sig.getSignDate() != null) sb.append("签名时间: ").append(sdf.format(sig.getSignDate())).append("\n");
                    sb.append("\n");
                }
                signatureInfoArea.setText(sb.toString());
            }
        } catch (Exception e) {
            signatureInfoArea.setText("无法读取签名信息: " + e.getMessage());
        }
    }

    private void clearForm() {
        selectedFile = null;
        filePathField.setText("");
        certPasswordField.setText("");
        reasonField.setText("文档审批");
        locationField.setText("");
        contactField.setText("");
        outputPathField.setText("");
        signatureInfoArea.setText("选择PDF文件后点击\"验证签名\"查看已有签名");
        statusLabel.setText(" ");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 证书下拉项
     */
    private static class CertificateItem {
        private final File file;

        public CertificateItem(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return file != null ? file.getName() : "(无可用证书，请先生成)";
        }
    }
}

package com.xiaohelab.jseal.ui.panel;

import com.xiaohelab.jseal.service.CertificateService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 证书管理面板
 */
public class CertificatePanel extends JPanel {

    private JTable certificateTable;
    private DefaultTableModel tableModel;
    private JTextArea detailArea;
    private JButton generateButton;
    private JButton importButton;
    private JButton deleteButton;
    private JLabel statusLabel;

    private List<CertificateEntry> certificates = new ArrayList<>();
    private final CertificateService certificateService = new CertificateService();
    private File certificateDir;

    public CertificatePanel() {
        initCertificateDir();
        initUI();
        loadCertificates();
    }

    private void initCertificateDir() {
        // 在用户目录下创建证书存储目录
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
        JLabel titleLabel = new JLabel("证书管理");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容 - 左右分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

        // 左侧 - 证书列表
        splitPane.setLeftComponent(createCertificateListPanel());

        // 右侧 - 证书详情
        splitPane.setRightComponent(createDetailPanel());

        add(splitPane, BorderLayout.CENTER);

        // 底部状态栏
        statusLabel = new JLabel("证书存储位置: " + certificateDir.getAbsolutePath());
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createCertificateListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("我的证书"));

        // 证书表格
        String[] columns = {"名称", "状态", "过期日期"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        certificateTable = new JTable(tableModel);
        certificateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        certificateTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedCertificateDetails();
            }
        });

        panel.add(new JScrollPane(certificateTable), BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        generateButton = new JButton("+ 生成证书");
        generateButton.addActionListener(e -> showGenerateCertificateDialog());

        importButton = new JButton("📥 导入");
        importButton.addActionListener(e -> importCertificate());

        JButton exportButton = new JButton("📤 导出");
        exportButton.addActionListener(e -> exportCertificate());

        deleteButton = new JButton("🗑️ 删除");
        deleteButton.addActionListener(e -> deleteCertificate());

        JButton refreshButton = new JButton("🔄 刷新");
        refreshButton.addActionListener(e -> loadCertificates());

        buttonPanel.add(generateButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("证书详情"));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        detailArea.setText("选择一个证书查看详情");

        panel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        return panel;
    }

    private void loadCertificates() {
        certificates.clear();
        tableModel.setRowCount(0);

        File[] files = certificateDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".p12") || name.toLowerCase().endsWith(".pfx"));

        if (files != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (File file : files) {
                CertificateEntry entry = new CertificateEntry();
                entry.setFile(file);
                entry.setName(file.getName());

                // 尝试获取证书信息（需要密码，这里先显示基本信息）
                entry.setStatus("未验证");
                entry.setExpireDate("-");

                certificates.add(entry);
                tableModel.addRow(new Object[]{
                        entry.getName(),
                        entry.getStatus(),
                        entry.getExpireDate()
                });
            }
        }

        statusLabel.setText("已加载 " + certificates.size() + " 个证书");
    }

    private void showSelectedCertificateDetails() {
        int selectedRow = certificateTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= certificates.size()) {
            detailArea.setText("选择一个证书查看详情");
            return;
        }

        CertificateEntry entry = certificates.get(selectedRow);

        // 弹出密码输入框
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, 
                new Object[]{"输入证书密码:", passwordField},
                "验证证书", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            char[] password = passwordField.getPassword();
            try {
                CertificateService.CertificateInfo info = 
                        certificateService.getCertificateInfo(entry.getFile(), password);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                StringBuilder sb = new StringBuilder();
                sb.append("=== 证书信息 ===\n\n");
                sb.append("别名: ").append(info.getAlias()).append("\n\n");
                sb.append("主题: ").append(info.getSubject()).append("\n\n");
                sb.append("颁发者: ").append(info.getIssuer()).append("\n\n");
                sb.append("序列号: ").append(info.getSerialNumber()).append("\n\n");
                sb.append("算法: ").append(info.getAlgorithm()).append("\n\n");
                sb.append("有效期:\n");
                sb.append("  从: ").append(sdf.format(info.getNotBefore())).append("\n");
                sb.append("  至: ").append(sdf.format(info.getNotAfter())).append("\n\n");
                sb.append("状态: ").append(info.getValidityStatus()).append("\n");

                detailArea.setText(sb.toString());

                // 更新表格
                tableModel.setValueAt(info.getValidityStatus(), selectedRow, 1);
                tableModel.setValueAt(new SimpleDateFormat("yyyy-MM-dd")
                        .format(info.getNotAfter()), selectedRow, 2);

            } catch (Exception e) {
                detailArea.setText("无法读取证书信息:\n" + e.getMessage());
            }
        }
    }

    private void showGenerateCertificateDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "生成自签名证书", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 通用名称
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("通用名称 (CN):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField cnField = new JTextField(20);
        panel.add(cnField, gbc);

        // 组织
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("组织 (O):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField orgField = new JTextField(20);
        panel.add(orgField, gbc);

        // 国家
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("国家代码 (C):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField countryField = new JTextField("CN", 20);
        panel.add(countryField, gbc);

        // 有效期
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("有效期 (天):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JSpinner validDaysSpinner = new JSpinner(new SpinnerNumberModel(365, 30, 3650, 30));
        panel.add(validDaysSpinner, gbc);

        // 密钥长度
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("密钥长度:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JComboBox<Integer> keySizeCombo = new JComboBox<>(new Integer[]{2048, 4096});
        panel.add(keySizeCombo, gbc);

        // 证书别名
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("证书别名:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField aliasField = new JTextField("my-cert", 20);
        panel.add(aliasField, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("证书密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPasswordField pwdField = new JPasswordField(20);
        panel.add(pwdField, gbc);

        // 确认密码
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        panel.add(new JLabel("确认密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JPasswordField confirmPwdField = new JPasswordField(20);
        panel.add(confirmPwdField, gbc);

        // 按钮
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton generateBtn = new JButton("生成");
        JButton cancelBtn = new JButton("取消");

        generateBtn.addActionListener(e -> {
            // 验证输入
            String cn = cnField.getText().trim();
            String org = orgField.getText().trim();
            String country = countryField.getText().trim();
            String alias = aliasField.getText().trim();
            char[] pwd = pwdField.getPassword();
            char[] confirmPwd = confirmPwdField.getPassword();

            if (cn.isEmpty() || org.isEmpty() || alias.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请填写所有必填项", 
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (pwd.length < 6) {
                JOptionPane.showMessageDialog(dialog, "密码长度至少6位", 
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!new String(pwd).equals(new String(confirmPwd))) {
                JOptionPane.showMessageDialog(dialog, "两次密码不一致", 
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                File outputFile = new File(certificateDir, alias + ".p12");
                certificateService.generateSelfSignedCertificate(
                        cn, org, country,
                        (int) validDaysSpinner.getValue(),
                        (int) keySizeCombo.getSelectedItem(),
                        alias, pwd, outputFile
                );

                JOptionPane.showMessageDialog(dialog, 
                        "证书生成成功！\n文件: " + outputFile.getAbsolutePath(),
                        "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCertificates();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                        "生成证书失败: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(generateBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void importCertificate() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                "PKCS12证书 (*.p12, *.pfx)", "p12", "pfx"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File sourceFile = chooser.getSelectedFile();
            File destFile = new File(certificateDir, sourceFile.getName());

            try {
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "证书导入成功！", 
                        "成功", JOptionPane.INFORMATION_MESSAGE);
                loadCertificates();
            } catch (Exception e) {
                showError("导入失败: " + e.getMessage());
            }
        }
    }

    private void exportCertificate() {
        int selectedRow = certificateTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("请先选择一个证书");
            return;
        }

        CertificateEntry entry = certificates.get(selectedRow);

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(entry.getName()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File destFile = chooser.getSelectedFile();
            try {
                java.nio.file.Files.copy(entry.getFile().toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "证书导出成功！", 
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showError("导出失败: " + e.getMessage());
            }
        }
    }

    private void deleteCertificate() {
        int selectedRow = certificateTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("请先选择一个证书");
            return;
        }

        CertificateEntry entry = certificates.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除证书 \"" + entry.getName() + "\" 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (entry.getFile().delete()) {
                JOptionPane.showMessageDialog(this, "证书已删除", 
                        "成功", JOptionPane.INFORMATION_MESSAGE);
                loadCertificates();
                detailArea.setText("选择一个证书查看详情");
            } else {
                showError("删除失败");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 获取证书目录
     */
    public File getCertificateDir() {
        return certificateDir;
    }

    /**
     * 证书条目类
     */
    private static class CertificateEntry {
        private File file;
        private String name;
        private String status;
        private String expireDate;

        public File getFile() { return file; }
        public void setFile(File file) { this.file = file; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getExpireDate() { return expireDate; }
        public void setExpireDate(String expireDate) { this.expireDate = expireDate; }
    }
}

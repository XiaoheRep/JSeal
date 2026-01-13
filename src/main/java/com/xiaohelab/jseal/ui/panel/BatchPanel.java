package com.xiaohelab.jseal.ui.panel;

import com.xiaohelab.jseal.common.enums.EncryptionAlgorithm;
import com.xiaohelab.jseal.service.PdfDecryptionService;
import com.xiaohelab.jseal.service.PdfEncryptionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * 批量处理面板
 */
public class BatchPanel extends JPanel {

    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> operationCombo;
    private JPasswordField passwordField;
    private JTextField outputDirField;
    private JButton processButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    private List<File> selectedFiles = new ArrayList<>();
    private final PdfEncryptionService encryptionService = new PdfEncryptionService();
    private final PdfDecryptionService decryptionService = new PdfDecryptionService();

    public BatchPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("批量处理");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        // 文件列表区域
        contentPanel.add(createFileListPanel(), BorderLayout.CENTER);

        // 右侧操作区域
        contentPanel.add(createOperationPanel(), BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);

        // 底部状态栏
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        statusLabel = new JLabel("准备就绪");
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createFileListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("PDF文件列表"));

        // 文件表格
        String[] columns = {"文件名", "大小", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fileTable = new JTable(tableModel);
        fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        panel.add(new JScrollPane(fileTable), BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JButton addButton = new JButton("➕ 添加文件");
        addButton.addActionListener(e -> addFiles());

        JButton addFolderButton = new JButton("📁 添加文件夹");
        addFolderButton.addActionListener(e -> addFolder());

        JButton removeButton = new JButton("➖ 移除选中");
        removeButton.addActionListener(e -> removeSelectedFiles());

        JButton clearButton = new JButton("🗑️ 清空列表");
        clearButton.addActionListener(e -> clearFiles());

        buttonPanel.add(addButton);
        buttonPanel.add(addFolderButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOperationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("操作设置"));
        panel.setPreferredSize(new Dimension(280, 0));

        // 操作类型
        JPanel opPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        opPanel.add(new JLabel("操作类型:"));
        operationCombo = new JComboBox<>(new String[]{
                "加密 (添加密码)",
                "解密 (移除密码)"
        });
        operationCombo.addActionListener(e -> updatePasswordLabel());
        opPanel.add(operationCombo);
        opPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(opPanel);

        panel.add(Box.createVerticalStrut(10));

        // 密码输入
        JPanel pwdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pwdPanel.add(new JLabel("密码:"));
        passwordField = new JPasswordField(15);
        pwdPanel.add(passwordField);
        pwdPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(pwdPanel);

        panel.add(Box.createVerticalStrut(10));

        // 输出目录
        JPanel outPanel = new JPanel(new BorderLayout(5, 5));
        outPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        outPanel.add(new JLabel("输出目录:"), BorderLayout.NORTH);
        outputDirField = new JTextField();
        outPanel.add(outputDirField, BorderLayout.CENTER);
        JButton browseBtn = new JButton("...");
        browseBtn.addActionListener(e -> selectOutputDir());
        outPanel.add(browseBtn, BorderLayout.EAST);
        outPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.add(outPanel);

        panel.add(Box.createVerticalStrut(10));

        // 后缀设置提示
        JLabel suffixLabel = new JLabel("<html><small>加密后文件添加 _encrypted 后缀<br>解密后文件添加 _decrypted 后缀</small></html>");
        suffixLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        panel.add(suffixLabel);

        panel.add(Box.createVerticalStrut(20));

        // 处理按钮
        processButton = new JButton("▶️ 开始处理");
        processButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        processButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        processButton.setMaximumSize(new Dimension(200, 45));
        processButton.addActionListener(e -> startBatchProcess());
        panel.add(processButton);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void addFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF文件", "pdf"));
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File file : chooser.getSelectedFiles()) {
                addFileToList(file);
            }
            updateStatus();
        }
    }

    private void addFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
            if (files != null) {
                for (File file : files) {
                    addFileToList(file);
                }
            }
            updateStatus();

            // 自动设置输出目录
            if (outputDirField.getText().isEmpty()) {
                outputDirField.setText(folder.getAbsolutePath());
            }
        }
    }

    private void addFileToList(File file) {
        if (!selectedFiles.contains(file)) {
            selectedFiles.add(file);
            String size = formatFileSize(file.length());
            tableModel.addRow(new Object[]{file.getName(), size, "待处理"});
        }
    }

    private void removeSelectedFiles() {
        int[] selectedRows = fileTable.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            selectedFiles.remove(selectedRows[i]);
            tableModel.removeRow(selectedRows[i]);
        }
        updateStatus();
    }

    private void clearFiles() {
        selectedFiles.clear();
        tableModel.setRowCount(0);
        updateStatus();
    }

    private void selectOutputDir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void updatePasswordLabel() {
        // 可以根据操作类型更新密码标签的提示
    }

    private void updateStatus() {
        statusLabel.setText("共 " + selectedFiles.size() + " 个文件");
    }

    private void startBatchProcess() {
        if (selectedFiles.isEmpty()) {
            showError("请先添加PDF文件");
            return;
        }

        char[] password = passwordField.getPassword();
        if (password.length == 0) {
            showError("请输入密码");
            return;
        }

        String outputDir = outputDirField.getText().trim();
        if (outputDir.isEmpty()) {
            showError("请指定输出目录");
            return;
        }

        File outDir = new File(outputDir);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        boolean isEncrypt = operationCombo.getSelectedIndex() == 0;

        // 禁用按钮
        processButton.setEnabled(false);
        progressBar.setValue(0);

        // 后台执行批量处理
        new SwingWorker<Void, Integer>() {
            private int successCount = 0;
            private int failCount = 0;

            @Override
            protected Void doInBackground() throws Exception {
                int total = selectedFiles.size();
                String passwordStr = new String(password);

                for (int i = 0; i < total; i++) {
                    File file = selectedFiles.get(i);
                    String suffix = isEncrypt ? "_encrypted.pdf" : "_decrypted.pdf";
                    String outputName = file.getName().replace(".pdf", suffix);
                    File outputFile = new File(outDir, outputName);

                    try {
                        if (isEncrypt) {
                            encryptionService.encryptWithPassword(
                                    file, outputFile, passwordStr, passwordStr,
                                    EncryptionAlgorithm.AES_256);
                        } else {
                            decryptionService.decrypt(file, outputFile, passwordStr);
                        }
                        updateTableStatus(i, "✅ 成功");
                        successCount++;
                    } catch (Exception e) {
                        updateTableStatus(i, "❌ 失败");
                        failCount++;
                    }

                    publish((i + 1) * 100 / total);
                }
                return null;
            }

            private void updateTableStatus(int row, String status) {
                SwingUtilities.invokeLater(() -> tableModel.setValueAt(status, row, 2));
            }

            @Override
            protected void process(List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
            }

            @Override
            protected void done() {
                processButton.setEnabled(true);
                progressBar.setValue(100);
                statusLabel.setText(String.format("处理完成: 成功 %d, 失败 %d", successCount, failCount));

                JOptionPane.showMessageDialog(BatchPanel.this,
                        String.format("批量处理完成！\n成功: %d 个\n失败: %d 个\n输出目录: %s",
                                successCount, failCount, outputDir),
                        "完成", JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}

package com.xiaohelab.jseal.ui.panel;

import com.xiaohelab.jseal.common.enums.PdfPermission;
import com.xiaohelab.jseal.service.PdfPermissionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PDF权限控制面板
 */
public class PermissionPanel extends JPanel {

    private JTextField filePathField;
    private JPasswordField ownerPasswordField;
    private JTextField outputPathField;
    private JButton applyButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    private Map<PdfPermission, JCheckBox> permissionCheckboxes = new HashMap<>();
    private File selectedFile;
    private final PdfPermissionService permissionService = new PdfPermissionService();

    public PermissionPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("PDF 权限控制");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // 文件选择区域
        contentPanel.add(createFileSelectionPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 权限设置区域
        contentPanel.add(createPermissionPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 密码设置区域
        contentPanel.add(createPasswordPanel());
        contentPanel.add(Box.createVerticalStrut(15));

        // 输出设置区域
        contentPanel.add(createOutputPanel());
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

    private JPanel createPermissionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("权限设置"));

        JPanel checkboxPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        checkboxPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 创建权限复选框
        for (PdfPermission permission : PdfPermission.values()) {
            JCheckBox checkbox = new JCheckBox(permission.getDisplayName());
            checkbox.setSelected(true); // 默认允许所有权限
            permissionCheckboxes.put(permission, checkbox);
            checkboxPanel.add(checkbox);
        }

        panel.add(checkboxPanel, BorderLayout.CENTER);

        // 快速选择按钮
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllBtn = new JButton("全选");
        selectAllBtn.addActionListener(e -> setAllPermissions(true));
        JButton deselectAllBtn = new JButton("全不选");
        deselectAllBtn.addActionListener(e -> setAllPermissions(false));
        JButton readOnlyBtn = new JButton("仅阅读");
        readOnlyBtn.addActionListener(e -> applyPreset(PdfPermissionService.getReadOnlyPreset()));
        JButton noPrintBtn = new JButton("禁止打印");
        noPrintBtn.addActionListener(e -> applyPreset(PdfPermissionService.getNoPrintPreset()));
        JButton noCopyBtn = new JButton("禁止复制");
        noCopyBtn.addActionListener(e -> applyPreset(PdfPermissionService.getNoCopyPreset()));

        quickPanel.add(selectAllBtn);
        quickPanel.add(deselectAllBtn);
        quickPanel.add(new JLabel(" | 预设:"));
        quickPanel.add(readOnlyBtn);
        quickPanel.add(noPrintBtn);
        quickPanel.add(noCopyBtn);

        panel.add(quickPanel, BorderLayout.SOUTH);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(new TitledBorder("所有者密码 (必填)"));

        panel.add(new JLabel("密码:"));
        ownerPasswordField = new JPasswordField(25);
        panel.add(ownerPasswordField);
        panel.add(new JLabel("(用于保护权限设置)"));

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

        applyButton = new JButton("🛡️ 应用权限");
        applyButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        applyButton.setPreferredSize(new Dimension(150, 40));
        applyButton.addActionListener(e -> applyPermissions());

        JButton clearButton = new JButton("清除");
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearForm());

        panel.add(applyButton);
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
            String outputName = selectedFile.getName().replace(".pdf", "_protected.pdf");
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

    private void setAllPermissions(boolean selected) {
        permissionCheckboxes.values().forEach(cb -> cb.setSelected(selected));
    }

    private void applyPreset(Set<PdfPermission> preset) {
        for (Map.Entry<PdfPermission, JCheckBox> entry : permissionCheckboxes.entrySet()) {
            entry.getValue().setSelected(preset.contains(entry.getKey()));
        }
    }

    private Set<PdfPermission> getSelectedPermissions() {
        Set<PdfPermission> permissions = EnumSet.noneOf(PdfPermission.class);
        for (Map.Entry<PdfPermission, JCheckBox> entry : permissionCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                permissions.add(entry.getKey());
            }
        }
        return permissions;
    }

    private void applyPermissions() {
        // 验证输入
        if (selectedFile == null || !selectedFile.exists()) {
            showError("请选择一个有效的PDF文件");
            return;
        }

        char[] ownerPwd = ownerPasswordField.getPassword();
        if (ownerPwd.length == 0) {
            showError("请输入所有者密码");
            return;
        }

        String outputPath = outputPathField.getText().trim();
        if (outputPath.isEmpty()) {
            showError("请指定输出文件路径");
            return;
        }

        Set<PdfPermission> permissions = getSelectedPermissions();

        // 禁用按钮，显示进度
        applyButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        statusLabel.setText("正在应用权限...");

        // 后台执行
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                File outputFile = new File(outputPath);
                permissionService.setPermissions(
                        selectedFile,
                        outputFile,
                        new String(ownerPwd),
                        "", // 用户密码为空
                        permissions,
                        256 // AES-256
                );
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                applyButton.setEnabled(true);
                try {
                    get();
                    statusLabel.setText("✅ 权限设置成功！");
                    JOptionPane.showMessageDialog(PermissionPanel.this,
                            "PDF权限设置成功！\n输出文件: " + outputPath,
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    statusLabel.setText("❌ 操作失败");
                    showError("设置权限失败: " + e.getCause().getMessage());
                }
            }
        }.execute();
    }

    private void clearForm() {
        selectedFile = null;
        filePathField.setText("");
        ownerPasswordField.setText("");
        outputPathField.setText("");
        setAllPermissions(true);
        statusLabel.setText(" ");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}

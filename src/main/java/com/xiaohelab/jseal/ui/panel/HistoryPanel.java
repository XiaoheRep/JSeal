package com.xiaohelab.jseal.ui.panel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 操作历史面板
 */
public class HistoryPanel extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JTextArea detailArea;
    private JLabel statusLabel;

    private List<HistoryRecord> historyRecords = new ArrayList<>();
    private File historyFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HistoryPanel() {
        initHistoryFile();
        initUI();
        loadHistory();
    }

    private void initHistoryFile() {
        String userHome = System.getProperty("user.home");
        File jsealDir = new File(userHome, ".jseal");
        if (!jsealDir.exists()) {
            jsealDir.mkdirs();
        }
        historyFile = new File(jsealDir, "history.json");
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("操作历史");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 主内容 - 上下分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);

        // 上方 - 历史记录表格
        splitPane.setTopComponent(createHistoryTablePanel());

        // 下方 - 详情
        splitPane.setBottomComponent(createDetailPanel());

        add(splitPane, BorderLayout.CENTER);

        // 底部状态栏
        statusLabel = new JLabel(" ");
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createHistoryTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("历史记录"));

        // 表格
        String[] columns = {"时间", "操作类型", "文件名", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedRecordDetails();
            }
        });

        // 设置列宽
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JButton refreshButton = new JButton("🔄 刷新");
        refreshButton.addActionListener(e -> loadHistory());

        JButton clearButton = new JButton("🗑️ 清空历史");
        clearButton.addActionListener(e -> clearHistory());

        JButton exportButton = new JButton("📤 导出");
        exportButton.addActionListener(e -> exportHistory());

        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("详细信息"));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        detailArea.setText("选择一条记录查看详情");

        panel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        return panel;
    }

    private void loadHistory() {
        historyRecords.clear();
        tableModel.setRowCount(0);

        if (historyFile.exists()) {
            try (Reader reader = new FileReader(historyFile)) {
                java.lang.reflect.Type listType = new TypeToken<ArrayList<HistoryRecord>>(){}.getType();
                List<HistoryRecord> loaded = gson.fromJson(reader, listType);
                if (loaded != null) {
                    historyRecords.addAll(loaded);
                }
            } catch (Exception e) {
                // 加载失败，使用空列表
            }
        }

        // 倒序显示（最新的在前面）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = historyRecords.size() - 1; i >= 0; i--) {
            HistoryRecord record = historyRecords.get(i);
            tableModel.addRow(new Object[]{
                    sdf.format(new Date(record.getTimestamp())),
                    record.getOperationType(),
                    record.getFileName(),
                    record.isSuccess() ? "✅ 成功" : "❌ 失败"
            });
        }

        statusLabel.setText("共 " + historyRecords.size() + " 条记录");
    }

    private void showSelectedRecordDetails() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0) {
            detailArea.setText("选择一条记录查看详情");
            return;
        }

        // 因为是倒序显示，需要转换索引
        int recordIndex = historyRecords.size() - 1 - selectedRow;
        if (recordIndex < 0 || recordIndex >= historyRecords.size()) {
            return;
        }

        HistoryRecord record = historyRecords.get(recordIndex);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("=== 操作详情 ===\n\n");
        sb.append("操作类型: ").append(record.getOperationType()).append("\n\n");
        sb.append("操作时间: ").append(sdf.format(new Date(record.getTimestamp()))).append("\n\n");
        sb.append("输入文件: ").append(record.getInputFile()).append("\n\n");
        sb.append("输出文件: ").append(record.getOutputFile()).append("\n\n");
        sb.append("状态: ").append(record.isSuccess() ? "成功" : "失败").append("\n\n");
        if (record.getMessage() != null && !record.getMessage().isEmpty()) {
            sb.append("消息: ").append(record.getMessage()).append("\n");
        }

        detailArea.setText(sb.toString());
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要清空所有历史记录吗？",
                "确认清空", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            historyRecords.clear();
            saveHistory();
            loadHistory();
            detailArea.setText("选择一条记录查看详情");
            JOptionPane.showMessageDialog(this, "历史记录已清空", 
                    "完成", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportHistory() {
        if (historyRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有历史记录可导出", 
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("jseal_history.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                writer.println("JSeal 操作历史导出");
                writer.println("导出时间: " + sdf.format(new Date()));
                writer.println("=".repeat(60));
                writer.println();

                for (HistoryRecord record : historyRecords) {
                    writer.println("时间: " + sdf.format(new Date(record.getTimestamp())));
                    writer.println("操作: " + record.getOperationType());
                    writer.println("文件: " + record.getFileName());
                    writer.println("输入: " + record.getInputFile());
                    writer.println("输出: " + record.getOutputFile());
                    writer.println("状态: " + (record.isSuccess() ? "成功" : "失败"));
                    if (record.getMessage() != null) {
                        writer.println("消息: " + record.getMessage());
                    }
                    writer.println("-".repeat(40));
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "导出成功！\n文件: " + file.getAbsolutePath(),
                        "完成", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 添加历史记录（供其他面板调用）
     */
    public void addRecord(String operationType, String inputFile, String outputFile,
                          boolean success, String message) {
        HistoryRecord record = new HistoryRecord();
        record.setTimestamp(System.currentTimeMillis());
        record.setOperationType(operationType);
        record.setInputFile(inputFile);
        record.setOutputFile(outputFile);
        record.setFileName(new File(inputFile).getName());
        record.setSuccess(success);
        record.setMessage(message);

        historyRecords.add(record);
        saveHistory();

        // 如果面板可见，刷新显示
        if (isVisible()) {
            loadHistory();
        }
    }

    private void saveHistory() {
        try (Writer writer = new FileWriter(historyFile)) {
            gson.toJson(historyRecords, writer);
        } catch (Exception e) {
            // 保存失败，忽略
        }
    }

    /**
     * 历史记录实体类
     */
    public static class HistoryRecord {
        private long timestamp;
        private String operationType;
        private String fileName;
        private String inputFile;
        private String outputFile;
        private boolean success;
        private String message;

        // Getters and Setters
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getOperationType() { return operationType; }
        public void setOperationType(String operationType) { this.operationType = operationType; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getInputFile() { return inputFile; }
        public void setInputFile(String inputFile) { this.inputFile = inputFile; }
        public String getOutputFile() { return outputFile; }
        public void setOutputFile(String outputFile) { this.outputFile = outputFile; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

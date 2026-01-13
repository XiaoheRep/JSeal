package com.xiaohelab.jseal.model;

import java.time.LocalDateTime;

/**
 * 历史记录
 */
public class HistoryRecord {
    private String id;
    private String operationType;
    private String inputFile;
    private String outputFile;
    private LocalDateTime timestamp;
    private boolean success;
    private String message;

    public HistoryRecord() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getInputFile() { return inputFile; }
    public void setInputFile(String inputFile) { this.inputFile = inputFile; }

    public String getOutputFile() { return outputFile; }
    public void setOutputFile(String outputFile) { this.outputFile = outputFile; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final HistoryRecord record = new HistoryRecord();

        public Builder id(String id) { record.id = id; return this; }
        public Builder operationType(String operationType) { record.operationType = operationType; return this; }
        public Builder inputFile(String inputFile) { record.inputFile = inputFile; return this; }
        public Builder outputFile(String outputFile) { record.outputFile = outputFile; return this; }
        public Builder timestamp(LocalDateTime timestamp) { record.timestamp = timestamp; return this; }
        public Builder success(boolean success) { record.success = success; return this; }
        public Builder message(String message) { record.message = message; return this; }
        public HistoryRecord build() { return record; }
    }
}

package com.xiaohelab.jseal.model;

/**
 * 任务执行结果
 */
public class TaskResult {
    private boolean success;
    private String message;
    private String inputFile;
    private String outputFile;
    private long duration;
    private Exception exception;

    public TaskResult() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getInputFile() { return inputFile; }
    public void setInputFile(String inputFile) { this.inputFile = inputFile; }

    public String getOutputFile() { return outputFile; }
    public void setOutputFile(String outputFile) { this.outputFile = outputFile; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public Exception getException() { return exception; }
    public void setException(Exception exception) { this.exception = exception; }

    public static TaskResult success(String message) {
        TaskResult result = new TaskResult();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    public static TaskResult success(String inputFile, String outputFile) {
        TaskResult result = new TaskResult();
        result.setSuccess(true);
        result.setInputFile(inputFile);
        result.setOutputFile(outputFile);
        return result;
    }

    public static TaskResult failure(String message) {
        TaskResult result = new TaskResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static TaskResult failure(Exception e) {
        TaskResult result = new TaskResult();
        result.setSuccess(false);
        result.setMessage(e.getMessage());
        result.setException(e);
        return result;
    }
}

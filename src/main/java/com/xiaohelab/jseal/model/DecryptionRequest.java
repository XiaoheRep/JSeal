package com.xiaohelab.jseal.model;

import java.io.File;

/**
 * 解密请求参数
 */
public class DecryptionRequest {
    private File inputFile;
    private File outputFile;
    private String password;

    public DecryptionRequest() {}

    public File getInputFile() { return inputFile; }
    public void setInputFile(File inputFile) { this.inputFile = inputFile; }

    public File getOutputFile() { return outputFile; }
    public void setOutputFile(File outputFile) { this.outputFile = outputFile; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DecryptionRequest request = new DecryptionRequest();

        public Builder inputFile(File inputFile) { request.inputFile = inputFile; return this; }
        public Builder outputFile(File outputFile) { request.outputFile = outputFile; return this; }
        public Builder password(String password) { request.password = password; return this; }
        public DecryptionRequest build() { return request; }
    }
}

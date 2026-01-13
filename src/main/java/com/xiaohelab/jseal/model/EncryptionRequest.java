package com.xiaohelab.jseal.model;

import com.xiaohelab.jseal.common.enums.EncryptionAlgorithm;
import com.xiaohelab.jseal.common.enums.PdfPermission;

import java.io.File;
import java.util.Set;

/**
 * 加密请求参数
 */
public class EncryptionRequest {
    private File inputFile;
    private File outputFile;
    private String userPassword;
    private String ownerPassword;
    private EncryptionAlgorithm algorithm;
    private Set<PdfPermission> permissions;

    public EncryptionRequest() {}

    public File getInputFile() { return inputFile; }
    public void setInputFile(File inputFile) { this.inputFile = inputFile; }

    public File getOutputFile() { return outputFile; }
    public void setOutputFile(File outputFile) { this.outputFile = outputFile; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getOwnerPassword() { return ownerPassword; }
    public void setOwnerPassword(String ownerPassword) { this.ownerPassword = ownerPassword; }

    public EncryptionAlgorithm getAlgorithm() { return algorithm; }
    public void setAlgorithm(EncryptionAlgorithm algorithm) { this.algorithm = algorithm; }

    public Set<PdfPermission> getPermissions() { return permissions; }
    public void setPermissions(Set<PdfPermission> permissions) { this.permissions = permissions; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final EncryptionRequest request = new EncryptionRequest();

        public Builder inputFile(File inputFile) { request.inputFile = inputFile; return this; }
        public Builder outputFile(File outputFile) { request.outputFile = outputFile; return this; }
        public Builder userPassword(String userPassword) { request.userPassword = userPassword; return this; }
        public Builder ownerPassword(String ownerPassword) { request.ownerPassword = ownerPassword; return this; }
        public Builder algorithm(EncryptionAlgorithm algorithm) { request.algorithm = algorithm; return this; }
        public Builder permissions(Set<PdfPermission> permissions) { request.permissions = permissions; return this; }
        public EncryptionRequest build() { return request; }
    }
}

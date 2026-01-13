package com.xiaohelab.jseal.model;

import java.io.File;
import java.security.KeyStore;

/**
 * 签名请求参数
 */
public class SignatureRequest {
    private File inputFile;
    private File outputFile;
    private KeyStore keyStore;
    private String alias;
    private String password;
    private String reason;
    private String location;
    private String contactInfo;

    public SignatureRequest() {}

    public File getInputFile() { return inputFile; }
    public void setInputFile(File inputFile) { this.inputFile = inputFile; }

    public File getOutputFile() { return outputFile; }
    public void setOutputFile(File outputFile) { this.outputFile = outputFile; }

    public KeyStore getKeyStore() { return keyStore; }
    public void setKeyStore(KeyStore keyStore) { this.keyStore = keyStore; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SignatureRequest request = new SignatureRequest();

        public Builder inputFile(File inputFile) { request.inputFile = inputFile; return this; }
        public Builder outputFile(File outputFile) { request.outputFile = outputFile; return this; }
        public Builder keyStore(KeyStore keyStore) { request.keyStore = keyStore; return this; }
        public Builder alias(String alias) { request.alias = alias; return this; }
        public Builder password(String password) { request.password = password; return this; }
        public Builder reason(String reason) { request.reason = reason; return this; }
        public Builder location(String location) { request.location = location; return this; }
        public Builder contactInfo(String contactInfo) { request.contactInfo = contactInfo; return this; }
        public SignatureRequest build() { return request; }
    }
}

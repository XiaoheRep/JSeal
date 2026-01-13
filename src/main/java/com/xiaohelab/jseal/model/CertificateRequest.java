package com.xiaohelab.jseal.model;

import java.io.File;

/**
 * 证书请求参数
 */
public class CertificateRequest {
    private String commonName;
    private String organization;
    private String country;
    private int validDays;
    private int keySize;
    private String alias;
    private String password;
    private File outputFile;

    public CertificateRequest() {}

    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public int getValidDays() { return validDays; }
    public void setValidDays(int validDays) { this.validDays = validDays; }

    public int getKeySize() { return keySize; }
    public void setKeySize(int keySize) { this.keySize = keySize; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public File getOutputFile() { return outputFile; }
    public void setOutputFile(File outputFile) { this.outputFile = outputFile; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CertificateRequest request = new CertificateRequest();

        public Builder commonName(String commonName) { request.commonName = commonName; return this; }
        public Builder organization(String organization) { request.organization = organization; return this; }
        public Builder country(String country) { request.country = country; return this; }
        public Builder validDays(int validDays) { request.validDays = validDays; return this; }
        public Builder keySize(int keySize) { request.keySize = keySize; return this; }
        public Builder alias(String alias) { request.alias = alias; return this; }
        public Builder password(String password) { request.password = password; return this; }
        public Builder outputFile(File outputFile) { request.outputFile = outputFile; return this; }
        public CertificateRequest build() { return request; }
    }
}

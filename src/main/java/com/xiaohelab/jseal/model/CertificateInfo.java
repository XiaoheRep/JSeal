package com.xiaohelab.jseal.model;

import java.util.Date;

/**
 * 证书信息
 */
public class CertificateInfo {
    private String alias;
    private String subjectDN;
    private String issuerDN;
    private Date validFrom;
    private Date validTo;
    private String serialNumber;
    private boolean valid;

    public CertificateInfo() {}

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getSubjectDN() { return subjectDN; }
    public void setSubjectDN(String subjectDN) { this.subjectDN = subjectDN; }

    public String getIssuerDN() { return issuerDN; }
    public void setIssuerDN(String issuerDN) { this.issuerDN = issuerDN; }

    public Date getValidFrom() { return validFrom; }
    public void setValidFrom(Date validFrom) { this.validFrom = validFrom; }

    public Date getValidTo() { return validTo; }
    public void setValidTo(Date validTo) { this.validTo = validTo; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CertificateInfo info = new CertificateInfo();

        public Builder alias(String alias) { info.alias = alias; return this; }
        public Builder subjectDN(String subjectDN) { info.subjectDN = subjectDN; return this; }
        public Builder issuerDN(String issuerDN) { info.issuerDN = issuerDN; return this; }
        public Builder validFrom(Date validFrom) { info.validFrom = validFrom; return this; }
        public Builder validTo(Date validTo) { info.validTo = validTo; return this; }
        public Builder serialNumber(String serialNumber) { info.serialNumber = serialNumber; return this; }
        public Builder valid(boolean valid) { info.valid = valid; return this; }
        public CertificateInfo build() { return info; }
    }
}

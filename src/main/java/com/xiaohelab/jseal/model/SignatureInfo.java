package com.xiaohelab.jseal.model;

import java.util.Date;

/**
 * 签名信息
 */
public class SignatureInfo {
    private String signerName;
    private Date signDate;
    private String reason;
    private String location;
    private boolean valid;
    private String algorithm;

    public SignatureInfo() {}

    public String getSignerName() { return signerName; }
    public void setSignerName(String signerName) { this.signerName = signerName; }

    public Date getSignDate() { return signDate; }
    public void setSignDate(Date signDate) { this.signDate = signDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SignatureInfo info = new SignatureInfo();

        public Builder signerName(String signerName) { info.signerName = signerName; return this; }
        public Builder signDate(Date signDate) { info.signDate = signDate; return this; }
        public Builder reason(String reason) { info.reason = reason; return this; }
        public Builder location(String location) { info.location = location; return this; }
        public Builder valid(boolean valid) { info.valid = valid; return this; }
        public Builder algorithm(String algorithm) { info.algorithm = algorithm; return this; }
        public SignatureInfo build() { return info; }
    }
}

package com.xiaohelab.jseal.common.enums;

/**
 * PDF加密算法枚举
 */
public enum EncryptionAlgorithm {
    AES_128(128, "AES-128 (兼容性好)"),
    AES_256(256, "AES-256 (更安全)");

    private final int keyLength;
    private final String displayName;

    EncryptionAlgorithm(int keyLength, String displayName) {
        this.keyLength = keyLength;
        this.displayName = displayName;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

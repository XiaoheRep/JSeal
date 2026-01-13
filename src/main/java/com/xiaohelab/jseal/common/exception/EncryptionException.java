package com.xiaohelab.jseal.common.exception;

/**
 * PDF加密异常
 */
public class EncryptionException extends JsealException {

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.xiaohelab.jseal.common.exception;

/**
 * PDF解密异常
 */
public class DecryptionException extends JsealException {

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}

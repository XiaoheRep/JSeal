package com.xiaohelab.jseal.common.exception;

/**
 * 数字签名异常
 */
public class SignatureException extends JsealException {

    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}

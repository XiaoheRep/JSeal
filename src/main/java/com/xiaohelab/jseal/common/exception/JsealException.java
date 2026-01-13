package com.xiaohelab.jseal.common.exception;

/**
 * JSeal 基础异常类
 */
public class JsealException extends Exception {

    public JsealException(String message) {
        super(message);
    }

    public JsealException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.dev.backendapi.exception;

public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String layer;

    protected BaseException(String message, String errorCode, String layer) {
        super(message);
        this.errorCode = errorCode;
        this.layer = layer;
    }

    protected BaseException(String message, Throwable cause, String errorCode, String layer) {
        super(message, cause);
        this.errorCode = errorCode;
        this.layer = layer;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getLayer() {
        return layer;
    }
}

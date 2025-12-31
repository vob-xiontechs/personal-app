package com.dev.backendapi.exception;

public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR", "BUSINESS");
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause, "BUSINESS_ERROR", "BUSINESS");
    }

    public BusinessException(String message, String errorCode) {
        super(message, errorCode, "BUSINESS");
    }
}

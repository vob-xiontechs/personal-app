package com.dev.backendapi.exception;

public class BusinessException extends BaseException {

    public BusinessException(String errorCode) {
        super(errorCode, "BUSINESS");
    }

    public BusinessException(String errorCode, Throwable cause) {
        super(errorCode, "BUSINESS", cause);
    }

    public BusinessException(String customMessage, String errorCode) {
        super(customMessage, errorCode, "BUSINESS");
    }
}

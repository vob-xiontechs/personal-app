package com.dev.backendapi.exception;

public class ServiceException extends BaseException {

    public ServiceException(String errorCode) {
        super(errorCode, "SERVICE");
    }

    public ServiceException(String errorCode, Throwable cause) {
        super(errorCode, "SERVICE", cause);
    }

    public ServiceException(String customMessage, String errorCode) {
        super(customMessage, errorCode, "SERVICE");
    }
}

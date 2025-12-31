package com.dev.backendapi.exception;

public class ServiceException extends BaseException {

    public ServiceException(String message) {
        super(message, "SERVICE_ERROR", "SERVICE");
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause, "SERVICE_ERROR", "SERVICE");
    }

    public ServiceException(String message, String errorCode) {
        super(message, errorCode, "SERVICE");
    }
}

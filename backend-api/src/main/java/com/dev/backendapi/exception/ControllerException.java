package com.dev.backendapi.exception;

public class ControllerException extends BaseException {

    public ControllerException(String message) {
        super(message, "CONTROLLER_ERROR", "CONTROLLER");
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause, "CONTROLLER_ERROR", "CONTROLLER");
    }

    public ControllerException(String message, String errorCode) {
        super(message, errorCode, "CONTROLLER");
    }
}

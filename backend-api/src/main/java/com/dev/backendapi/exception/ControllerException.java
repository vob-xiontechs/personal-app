package com.dev.backendapi.exception;

public class ControllerException extends BaseException {

    public ControllerException(String errorCode) {
        super(errorCode, "CONTROLLER");
    }

    public ControllerException(String errorCode, Throwable cause) {
        super(errorCode, "CONTROLLER", cause);
    }

    public ControllerException(String customMessage, String errorCode) {
        super(customMessage, errorCode, "CONTROLLER");
    }
}

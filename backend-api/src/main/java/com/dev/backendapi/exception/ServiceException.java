package com.dev.backendapi.exception;

import com.dev.backendapi.config.ErrorMessageConfig;

public class ServiceException extends BaseException {

    public ServiceException(String errorCode, ErrorMessageConfig config) {
        super(errorCode, "SERVICE", config);
    }

    public ServiceException(String errorCode, ErrorMessageConfig config, Throwable cause) {
        super(errorCode, "SERVICE", config, cause);
    }

    public ServiceException(String customMessage, String errorCode, ErrorMessageConfig config) {
        super(customMessage, errorCode, "SERVICE", config);
    }
}

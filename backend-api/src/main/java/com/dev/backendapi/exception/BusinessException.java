package com.dev.backendapi.exception;

import com.dev.backendapi.config.ErrorMessageConfig;

public class BusinessException extends BaseException {

    public BusinessException(String errorCode, ErrorMessageConfig config) {
        super(errorCode, "BUSINESS", config);
    }

    public BusinessException(String errorCode, ErrorMessageConfig config, Throwable cause) {
        super(errorCode, "BUSINESS", config, cause);
    }

    public BusinessException(String customMessage, String errorCode, ErrorMessageConfig config) {
        super(customMessage, errorCode, "BUSINESS", config);
    }
}

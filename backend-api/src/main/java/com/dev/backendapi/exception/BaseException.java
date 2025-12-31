package com.dev.backendapi.exception;

import com.dev.backendapi.config.ErrorMessageConfig;

public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String layer;
    private final ErrorMessageConfig errorMessageConfig;

    protected BaseException(String errorCode, String layer, ErrorMessageConfig errorMessageConfig) {
        super(getConfiguredMessage(errorCode, layer, errorMessageConfig));
        this.errorCode = errorCode;
        this.layer = layer;
        this.errorMessageConfig = errorMessageConfig;
    }

    protected BaseException(String errorCode, String layer, ErrorMessageConfig errorMessageConfig, Throwable cause) {
        super(getConfiguredMessage(errorCode, layer, errorMessageConfig), cause);
        this.errorCode = errorCode;
        this.layer = layer;
        this.errorMessageConfig = errorMessageConfig;
    }

    protected BaseException(String customMessage, String errorCode, String layer, ErrorMessageConfig errorMessageConfig) {
        super(customMessage);
        this.errorCode = errorCode;
        this.layer = layer;
        this.errorMessageConfig = errorMessageConfig;
    }

    private static String getConfiguredMessage(String errorCode, String layer, ErrorMessageConfig config) {
        if (config != null) {
            return config.getMessage(layer, errorCode);
        }
        return "An error occurred in " + layer + " layer";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getLayer() {
        return layer;
    }

    public String getLayerDescription() {
        if (errorMessageConfig != null) {
            return errorMessageConfig.getLayerDescription(layer);
        }
        return layer;
    }
}

package com.dev.backendapi.exception;

import com.dev.backendapi.config.ErrorMessageConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ErrorMessageProvider {

    private static ErrorMessageConfig errorMessageConfig;

    @Autowired
    public void setErrorMessageConfig(ErrorMessageConfig config) {
        ErrorMessageProvider.errorMessageConfig = config;
    }

    public static String getMessage(String layer, String errorCode) {
        if (errorMessageConfig != null) {
            return errorMessageConfig.getMessage(layer, errorCode);
        }
        return "An error occurred in " + layer + " layer";
    }

    public static String getLayerDescription(String layer) {
        if (errorMessageConfig != null) {
            return errorMessageConfig.getLayerDescription(layer);
        }
        return layer;
    }
}

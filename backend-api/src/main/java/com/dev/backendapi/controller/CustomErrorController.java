package com.dev.backendapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(WebRequest webRequest) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(
            webRequest,
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.BINDING_ERRORS)
        );

        HttpStatus status = getStatus(errorAttributes);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorAttributes.getOrDefault("message", "Unknown error"));
        errorResponse.put("type", "ERROR");
        errorResponse.put("status", status.value());

        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus getStatus(Map<String, Object> errorAttributes) {
        Integer statusCode = (Integer) errorAttributes.get("status");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (IllegalArgumentException e) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

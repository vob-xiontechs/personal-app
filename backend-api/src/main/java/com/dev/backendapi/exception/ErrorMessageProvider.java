package com.dev.backendapi.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessageProvider {

    private static final Map<String, Map<String, String>> ERROR_MESSAGES = new HashMap<>();
    private static final Map<String, String> LAYER_DESCRIPTIONS = new HashMap<>();

    static {
        // Initialize BUSINESS layer messages
        Map<String, String> businessMessages = new HashMap<>();
        businessMessages.put("BUSINESS_ERROR", "Business rule violation occurred");
        businessMessages.put("EMAIL_EXISTS", "Email address is already registered");
        businessMessages.put("INVALID_OPERATION", "Operation cannot be performed");
        businessMessages.put("INSUFFICIENT_PERMISSIONS", "Insufficient permissions for this operation");
        businessMessages.put("RESOURCE_NOT_AVAILABLE", "Requested resource is not available");
        ERROR_MESSAGES.put("BUSINESS", businessMessages);

        // Initialize SERVICE layer messages
        Map<String, String> serviceMessages = new HashMap<>();
        serviceMessages.put("SERVICE_ERROR", "Service processing error");
        serviceMessages.put("VALIDATION_FAILED", "Input validation failed");
        serviceMessages.put("BUSINESS_LOGIC_ERROR", "Business logic processing failed");
        serviceMessages.put("EXTERNAL_SERVICE_ERROR", "External service communication failed");
        serviceMessages.put("TIMEOUT_ERROR", "Operation timed out");
        ERROR_MESSAGES.put("SERVICE", serviceMessages);

        // Initialize REPOSITORY layer messages
        Map<String, String> repositoryMessages = new HashMap<>();
        repositoryMessages.put("REPOSITORY_ERROR", "Database operation failed");
        repositoryMessages.put("CONNECTION_ERROR", "Database connection error");
        repositoryMessages.put("CONSTRAINT_VIOLATION", "Data constraint violation");
        repositoryMessages.put("DATA_INTEGRITY_ERROR", "Data integrity error");
        repositoryMessages.put("DUPLICATE_KEY_ERROR", "Duplicate key violation");
        repositoryMessages.put("FOREIGN_KEY_ERROR", "Foreign key constraint violation");
        ERROR_MESSAGES.put("REPOSITORY", repositoryMessages);

        // Initialize CONTROLLER layer messages
        Map<String, String> controllerMessages = new HashMap<>();
        controllerMessages.put("CONTROLLER_ERROR", "Request processing error");
        controllerMessages.put("INVALID_REQUEST", "Invalid request format");
        controllerMessages.put("MISSING_PARAMETER", "Required parameter is missing");
        controllerMessages.put("INVALID_REQUEST_BODY", "Invalid request body");
        controllerMessages.put("UNSUPPORTED_CONTENT_TYPE", "Unsupported content type");
        ERROR_MESSAGES.put("CONTROLLER", controllerMessages);

        // Authentication messages are now handled by AuthConstants

        // Initialize VALIDATION layer messages
        Map<String, String> validationMessages = new HashMap<>();
        validationMessages.put("VALIDATION_ERROR", "Input validation error");
        validationMessages.put("FIELD_VALIDATION_ERROR", "Field validation failed");
        validationMessages.put("REQUIRED_FIELD_MISSING", "Required field is missing");
        validationMessages.put("INVALID_FIELD_FORMAT", "Invalid field format");
        validationMessages.put("FIELD_TOO_LONG", "Field value is too long");
        validationMessages.put("FIELD_TOO_SHORT", "Field value is too short");
        ERROR_MESSAGES.put("VALIDATION", validationMessages);

        // Initialize ENTITY layer messages
        Map<String, String> entityMessages = new HashMap<>();
        entityMessages.put("ENTITY_ERROR", "Entity processing error");
        entityMessages.put("ENTITY_NOT_FOUND", "Entity not found");
        entityMessages.put("ENTITY_VALIDATION_ERROR", "Entity validation failed");
        entityMessages.put("ENTITY_CONSTRAINT_ERROR", "Entity constraint violation");
        entityMessages.put("ENTITY_STATE_ERROR", "Invalid entity state");
        ERROR_MESSAGES.put("ENTITY", entityMessages);

        // Initialize INFRASTRUCTURE layer messages
        Map<String, String> infrastructureMessages = new HashMap<>();
        infrastructureMessages.put("INFRASTRUCTURE_ERROR", "Infrastructure error");
        infrastructureMessages.put("EXTERNAL_SERVICE_ERROR", "External service unavailable");
        infrastructureMessages.put("CONFIGURATION_ERROR", "Configuration error");
        infrastructureMessages.put("NETWORK_ERROR", "Network communication error");
        infrastructureMessages.put("CACHE_ERROR", "Cache operation failed");
        infrastructureMessages.put("MESSAGE_QUEUE_ERROR", "Message queue error");
        ERROR_MESSAGES.put("INFRASTRUCTURE", infrastructureMessages);

        // Initialize layer descriptions
        LAYER_DESCRIPTIONS.put("BUSINESS", "Business Logic Layer");
        LAYER_DESCRIPTIONS.put("SERVICE", "Service Layer");
        LAYER_DESCRIPTIONS.put("REPOSITORY", "Data Access Layer");
        LAYER_DESCRIPTIONS.put("CONTROLLER", "Presentation Layer");
        LAYER_DESCRIPTIONS.put("VALIDATION", "Validation Layer");
        LAYER_DESCRIPTIONS.put("ENTITY", "Domain Entity Layer");
        LAYER_DESCRIPTIONS.put("INFRASTRUCTURE", "Infrastructure Layer");

        System.out.println("✅ ErrorMessageProvider initialized with static messages");
        System.out.println("📋 Available layers: " + ERROR_MESSAGES.keySet());
        System.out.println("📋 BUSINESS EMAIL_EXISTS: " + getMessage("BUSINESS", "EMAIL_EXISTS"));
    }

    // Static method for backward compatibility with BaseException
    public static String getMessage(String layer, String errorCode) {
        System.out.println("🔍 Getting message for layer: " + layer + ", code: " + errorCode);

        if (ERROR_MESSAGES.containsKey(layer)) {
            Map<String, String> layerMessages = ERROR_MESSAGES.get(layer);
            if (layerMessages.containsKey(errorCode)) {
                String message = layerMessages.get(errorCode);
                System.out.println("✅ Found message: " + message);
                return message;
            }
        }

        // Fallback
        System.out.println("❌ Message not found, using fallback");
        return "An error occurred in " + layer + " layer";
    }

    public static String getLayerDescription(String layer) {
        return LAYER_DESCRIPTIONS.getOrDefault(layer, layer);
    }
}

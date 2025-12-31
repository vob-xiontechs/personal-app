package com.dev.backendapi.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.errors")
public class ErrorMessageConfig {

    private Map<String, Map<String, String>> messages;
    private Map<String, String> layers;
    private Map<String, String> defaults;

    public Map<String, Map<String, String>> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Map<String, String>> messages) {
        this.messages = messages;
    }

    public Map<String, String> getLayers() {
        return layers;
    }

    public void setLayers(Map<String, String> layers) {
        this.layers = layers;
    }

    public Map<String, String> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, String> defaults) {
        this.defaults = defaults;
    }

    /**
     * Get error message for specific layer and error code
     */
    public String getMessage(String layer, String errorCode) {
        System.out.println("🔍 Getting message for layer: " + layer + ", code: " + errorCode);
        System.out.println("📋 Messages map: " + (messages != null ? messages.keySet() : "null"));

        if (messages != null && messages.containsKey(layer)) {
            Map<String, String> layerMessages = messages.get(layer);
            System.out.println("📋 Layer messages for " + layer + ": " + (layerMessages != null ? layerMessages.keySet() : "null"));

            if (layerMessages != null && layerMessages.containsKey(errorCode)) {
                String message = layerMessages.get(errorCode);
                System.out.println("✅ Found message: " + message);
                return message;
            }
        }

        // Fallback to defaults
        if (defaults != null && defaults.containsKey(errorCode)) {
            String defaultMessage = defaults.get(errorCode);
            System.out.println("⚠️ Using default message: " + defaultMessage);
            return defaultMessage;
        }

        // Ultimate fallback
        System.out.println("❌ No message found, using fallback");
        return "An error occurred";
    }

    /**
     * Get layer description
     */
    public String getLayerDescription(String layer) {
        if (layers != null && layers.containsKey(layer)) {
            return layers.get(layer);
        }
        return layer;
    }
}

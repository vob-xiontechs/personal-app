package com.dev.backendapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:error-messages.yml")
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
        if (messages != null && messages.containsKey(layer)) {
            Map<String, String> layerMessages = messages.get(layer);
            if (layerMessages != null && layerMessages.containsKey(errorCode)) {
                return layerMessages.get(errorCode);
            }
        }

        // Fallback to defaults
        if (defaults != null && defaults.containsKey(errorCode)) {
            return defaults.get(errorCode);
        }

        // Ultimate fallback
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

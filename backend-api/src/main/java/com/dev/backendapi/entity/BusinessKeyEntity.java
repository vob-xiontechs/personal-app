package com.dev.backendapi.entity;

import java.util.UUID;

import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Business key management for entities - unique business identifiers.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BusinessKeyEntity {

    /**
     * Business Key - Unique identifier for business domain logic.
     */
    @Column(name = "user_id", unique = true, nullable = false, updatable = false, length = 36)
    private String userId;

    /**
     * Generate a unique business key using UUID.
     */
    protected String generateBusinessKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get business identifier with validation.
     */
    public String getBusinessIdentifier() {
        if (!StringUtils.hasText(this.userId)) {
            throw new IllegalStateException("Business identifier is not set");
        }
        return this.userId;
    }

    /**
     * Check if business key is set.
     */
    public boolean hasBusinessKey() {
        return StringUtils.hasText(this.userId);
    }
}

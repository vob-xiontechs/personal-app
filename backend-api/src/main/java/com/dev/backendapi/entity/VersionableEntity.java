package com.dev.backendapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Version control for entities - optimistic locking support
 */
@MappedSuperclass
@Data
@SuperBuilder
public abstract class VersionableEntity {

    /**
     * Optimistic locking version - prevents concurrent modification conflicts
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Get optimistic lock version with null safety
     */
    public Long getOptimisticLockVersion() {
        return this.version != null ? this.version : 0L;
    }

    /**
     * Check if entity has been persisted (has version)
     */
    public boolean isVersioned() {
        return this.version != null;
    }
}

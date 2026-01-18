package com.dev.backendapi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Soft delete support for entities - logical deletion without physical removal
 */
@MappedSuperclass
@Data
@SuperBuilder
public abstract class SoftDeletableEntity {

    /**
     * Soft delete flag - marks entity as logically deleted
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    /**
     * Soft delete timestamp - when entity was marked as deleted
     */
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    /**
     * Perform soft delete with timestamp tracking
     */
    public void performSoftDelete() {
        if (Boolean.TRUE.equals(this.isDeleted)) {
            throw new IllegalStateException("Entity is already marked as deleted");
        }
        this.isDeleted = Boolean.TRUE;
        this.deletedAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Restore soft-deleted entity
     */
    public void restore() {
        if (!Boolean.TRUE.equals(this.isDeleted)) {
            throw new IllegalStateException("Entity is not marked as deleted");
        }
        this.isDeleted = Boolean.FALSE;
        this.deletedAt = null;
    }

    /**
     * Check if entity is soft deleted with null safety
     */
    public boolean isSoftDeleted() {
        return Boolean.TRUE.equals(this.isDeleted);
    }

    /**
     * Check if entity is active (not soft deleted)
     */
    public boolean isActive() {
        return !Boolean.TRUE.equals(this.isDeleted);
    }
}

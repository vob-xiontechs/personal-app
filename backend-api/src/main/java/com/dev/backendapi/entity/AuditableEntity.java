package com.dev.backendapi.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Audit fields for entities - tracks creation and modification
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuditableEntity extends BusinessKeyEntity {

    /**
     * Creator identifier - tracks who created this entity
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    /**
     * Creation timestamp - when entity was first created
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Timestamp createdAt;

    /**
     * Last modifier identifier - tracks who last modified this entity
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Last modification timestamp - when entity was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    /**
     * Check if entity has been modified since creation
     */
    public boolean hasBeenModified() {
        return this.updatedAt != null &&
               this.createdAt != null &&
               !this.updatedAt.equals(this.createdAt);
    }

    /**
     * Get entity age in hours
     */
    public long getAgeInHours() {
        if (this.createdAt == null) return 0;
        long diff = System.currentTimeMillis() - this.createdAt.getTime();
        return diff / (1000 * 60 * 60);
    }
}

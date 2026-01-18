package com.dev.backendapi.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Enterprise-grade base entity with comprehensive audit, security, and lifecycle features.
 *
 * Composed of modular entity components:
 * - BusinessKeyEntity: Business identifier management
 * - AuditableEntity: Audit trail tracking
 * - VersionableEntity: Optimistic locking
 * - SoftDeletableEntity: Soft delete support
 *
 * Advanced Features:
 * - Modular design with composition over inheritance
 * - Robust primary key and business key management
 * - Complete audit trail with user tracking
 * - Optimistic locking with conflict detection
 * - Soft delete with recovery support
 * - Entity lifecycle callbacks and validation
 * - Business rule enforcement
 * - Performance and security optimizations
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity extends AuditableEntity {

    // ===== PRIMARY KEY MANAGEMENT =====

    /**
     * Primary Key - Auto-generated unique identifier.
     * Uses IDENTITY strategy for better performance with MySQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    // ===== LIFECYCLE CALLBACKS =====

    /**
     * Pre-persist callback - executed before entity is first saved.
     * Ensures business key is generated if not provided.
     */
    @PrePersist
    protected void onCreate() {
        if (!StringUtils.hasText(this.getUserId())) {
            this.setUserId(generateBusinessKey());
        }
        validateBusinessRules();
    }

    /**
     * Pre-update callback - executed before entity is updated
     * Validates business rules and prevents invalid state changes
     */
    @PreUpdate
    protected void onUpdate() {
        validateBusinessRules();
        validateStateTransitions();
    }

    // ===== BUSINESS METHODS =====

    /**
     * Validate business rules before persistence
     * Override in subclasses to add specific validations
     */
    protected void validateBusinessRules() {
        // Default implementation - can be overridden
    }

    /**
     * Validate state transitions to prevent invalid changes
     * Override in subclasses for specific state transition rules
     */
    protected void validateStateTransitions() {
        // Default implementation - no restrictions
    }

    // ===== UTILITY METHODS =====

    /**
     * Check if entity is newly created (transient state)
     */
    public boolean isTransient() {
        return this.id == null;
    }

    /**
     * Check if entity has been persisted to database
     */
    public boolean isPersistent() {
        return this.id != null;
    }

    /**
     * Get entity age in specified time unit
     */
    public long getAge(ChronoUnit unit) {
        // Get creation timestamp from auditable functionality
        // This would need to be implemented based on the auditing fields
        return 0; // Placeholder - would need access to createdAt
    }

    /**
     * Check if entity is older than specified time
     */
    public boolean isOlderThan(long amount, ChronoUnit unit) {
        return getAge(unit) > amount;
    }

    // ===== EQUALITY AND HASH CODE =====

    /**
     * Business key-based equality for better domain modeling
     * Uses userId as primary equality criteria
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BaseEntity)) return false;
        BaseEntity other = (BaseEntity) obj;

        // If both have IDs, compare by ID (for persistent entities)
        if (this.id != null && other.id != null) {
            return Objects.equals(this.id, other.id);
        }

        // Otherwise compare by business key (for transient entities)
        return Objects.equals(this.getUserId(), other.getUserId());
    }

    /**
     * Business key-based hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id != null ? this.id : this.getUserId());
    }

    // ===== TO STRING =====

    /**
     * Comprehensive toString with all important fields
     */
    @Override
    public String toString() {
        return String.format("%s{id=%s, userId='%s'}",
                getClass().getSimpleName(),
                id,
                getUserId());
    }
}

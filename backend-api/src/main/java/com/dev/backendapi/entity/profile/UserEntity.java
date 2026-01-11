package com.dev.backendapi.entity.profile;

import com.dev.backendapi.constants.profile.ProfileConstants;
import com.dev.backendapi.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * User entity extending BaseEntity for comprehensive audit and common field support
 */
@Entity
@Table(name = ProfileConstants.USER_TABLE_NAME)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    // Domain-specific fields
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Account verification fields
    @Column
    private String verifyOtp;

    @Column
    private Boolean isAccountVerified = true;  // Temporarily set to true for testing

    @Column
    private Long verifyOtpExpireAt;

    // Password reset fields
    @Column
    private String resetOtp;

    @Column
    private Long resetOtpExpireAt;

    /**
     * Business method to check if user account is verified
     * @return true if account verification is complete
     */
    public boolean isUserAccountVerified() {
        return Boolean.TRUE.equals(this.isAccountVerified);
    }

    /**
     * Business method to verify account
     */
    public void verifyAccount() {
        this.isAccountVerified = true;
        this.verifyOtp = null;
        this.verifyOtpExpireAt = null;
    }

    /**
     * Business method to check if OTP is expired
     */
    public boolean isOtpExpired(long currentTime) {
        return this.verifyOtpExpireAt != null && this.verifyOtpExpireAt < currentTime;
    }

    /**
     * Business method to set verification OTP
     */
    public void setVerificationOtp(String otp, long expireTime) {
        this.verifyOtp = otp;
        this.verifyOtpExpireAt = expireTime;
        this.isAccountVerified = false;
    }
}

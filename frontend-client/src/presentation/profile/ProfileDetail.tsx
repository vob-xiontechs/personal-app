import React from "react";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";
import "./profile-detail.scss";

interface ProfileDetailProps {
  profile: ProfileResponse | null;
  loading: boolean;
  error: string | null;
  onClose: () => void;
}

export const ProfileDetail: React.FC<ProfileDetailProps> = ({
  profile,
  loading,
  error,
  onClose,
}) => {
  if (!profile && !loading && !error) return null;

  const handleBackdropClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="profile-detail-overlay" onClick={handleBackdropClick}>
      <div className="profile-detail-modal">
        <button className="profile-detail-close" onClick={onClose}>
          ×
        </button>

        {loading && (
          <div className="profile-detail-loading">
            <div className="loading-spinner"></div>
            <p>Loading profile details...</p>
          </div>
        )}

        {error && (
          <div className="profile-detail-error">
            <div className="error-icon">⚠️</div>
            <h3>Error</h3>
            <p>{error}</p>
            <button className="retry-btn" onClick={onClose}>
              Close
            </button>
          </div>
        )}

        {profile && !loading && !error && (
          <div className="profile-detail-content">
            <div className="profile-detail-header">
              <div className="avatar-large">
                {profile.name.charAt(0).toUpperCase()}
              </div>
              <div className="header-info">
                <h2 className="profile-name">{profile.name}</h2>
                <div className={`status-badge ${profile.isAccountVerified ? 'verified' : 'unverified'}`}>
                  {profile.isAccountVerified ? '✓ Verified Account' : '✗ Unverified Account'}
                </div>
              </div>
            </div>

            <div className="profile-detail-body">
              <div className="detail-section">
                <h3>Profile Information</h3>
                <div className="info-grid">
                  <div className="info-item">
                    <label>User ID</label>
                    <span className="mono-text">{profile.userId}</span>
                  </div>
                  <div className="info-item">
                    <label>Full Name</label>
                    <span>{profile.name}</span>
                  </div>
                  <div className="info-item">
                    <label>Email Address</label>
                    <span>{profile.email}</span>
                  </div>
                  <div className="info-item">
                    <label>Account Status</label>
                    <span className={profile.isAccountVerified ? 'status-verified' : 'status-unverified'}>
                      {profile.isAccountVerified ? 'Verified' : 'Not Verified'}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div className="profile-detail-actions">
              <button className="btn-secondary" onClick={onClose}>
                Close
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

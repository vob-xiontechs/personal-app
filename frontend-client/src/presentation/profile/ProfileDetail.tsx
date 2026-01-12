import React, { useState, useEffect } from "react";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";
import type { UpdateProfileRequest } from "../../domain/profile/dto/UpdateProfileRequest";
import "./profile-detail.scss";

interface ProfileDetailProps {
  profile: ProfileResponse | null;
  loading: boolean;
  error: string | null;
  onClose: () => void;
  onUpdate?: (userId: string, request: UpdateProfileRequest) => Promise<void>;
}

export const ProfileDetail: React.FC<ProfileDetailProps> = ({
  profile,
  loading,
  error,
  onClose,
  onUpdate,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState<UpdateProfileRequest>({
    name: '',
    email: '',
    newPassword: '',
    confirmPassword: '',
    currentPassword: ''
  });
  const [updateLoading, setUpdateLoading] = useState(false);
  const [updateError, setUpdateError] = useState<string | null>(null);

  useEffect(() => {
    if (profile) {
      setEditForm({
        name: profile.name,
        email: profile.email,
        newPassword: '',
        confirmPassword: '',
        currentPassword: '' // Don't prefill for security
      });
    }
  }, [profile]);

  if (!profile && !loading && !error) return null;

  const handleEdit = () => {
    setIsEditing(true);
    setUpdateError(null);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setUpdateError(null);
    if (profile) {
      setEditForm({
        name: profile.name,
        email: profile.email,
        newPassword: '',
        confirmPassword: '',
        currentPassword: '',
      });
    }
  };

  const handleSave = async () => {
    if (!profile || !onUpdate) return;

    setUpdateLoading(true);
    setUpdateError(null);

    try {
      await onUpdate(profile.userId, editForm);
      setIsEditing(false);
      // The parent component will handle refreshing the profile data
    } catch (error: any) {
      setUpdateError(error.message);
    } finally {
      setUpdateLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEditForm(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

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
                <h2 className="profile-name">
                  {isEditing ? 'Edit Profile' : profile.name}
                </h2>
                <div className={`status-badge ${profile.isAccountVerified ? 'verified' : 'unverified'}`}>
                  {profile.isAccountVerified ? 'Verified Account' : 'Unverified Account'}
                </div>
              </div>
            </div>

            <div className="profile-detail-body">
              {isEditing ? (
                <div className="edit-section">
                  <h3>Edit Profile Information</h3>
                  {updateError && (
                    <div className="error-message">
                      <div className="error-icon">⚠️</div>
                      <span>{updateError}</span>
                    </div>
                  )}
                  <div className="edit-form">
                    <div className="form-group">
                      <label htmlFor="currentPassword">Current Password *</label>
                      <input
                        type="password"
                        id="currentPassword"
                        name="currentPassword"
                        value={editForm.currentPassword}
                        onChange={handleInputChange}
                        placeholder="Enter your current password"
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="name">Full Name</label>
                      <input
                        type="text"
                        id="name"
                        name="name"
                        value={editForm.name}
                        onChange={handleInputChange}
                        placeholder="Enter full name"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="email">Email Address</label>
                      <input
                        type="email"
                        id="email"
                        name="email"
                        value={editForm.email}
                        onChange={handleInputChange}
                        placeholder="Enter email address"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="newPassword">New Password (leave empty to keep current)</label>
                      <input
                        type="password"
                        id="newPassword"
                        name="newPassword"
                        value={editForm.newPassword}
                        onChange={handleInputChange}
                        placeholder="Enter new password"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="confirmPassword">Confirm New Password</label>
                      <input
                        type="password"
                        id="confirmPassword"
                        name="confirmPassword"
                        value={editForm.confirmPassword}
                        onChange={handleInputChange}
                        placeholder="Confirm new password"
                      />
                    </div>
                  </div>
                </div>
              ) : (
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
              )}
            </div>

            <div className="profile-detail-actions">
              {isEditing ? (
                <>
                  <button
                    className="btn-secondary"
                    onClick={handleCancel}
                    disabled={updateLoading}
                  >
                    Cancel
                  </button>
                  <button
                    className="btn-primary"
                    onClick={handleSave}
                    disabled={updateLoading}
                  >
                    {updateLoading ? 'Saving...' : 'Save Changes'}
                  </button>
                </>
              ) : (
                <>
                  {onUpdate && (
                    <button className="btn-primary" onClick={handleEdit}>
                      Edit Profile
                    </button>
                  )}
                  <button className="btn-secondary" onClick={onClose}>
                    Close
                  </button>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

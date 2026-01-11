import React from "react";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";
import "./profile-list.scss";

interface ProfileListProps {
  profiles: ProfileResponse[];
  loading: boolean;
  error: string | null;
  onRefresh: () => void;
}

export const ProfileList: React.FC<ProfileListProps> = ({
  profiles,
  loading,
  error,
  onRefresh,
}) => {
  // Loading state
  if (loading) {
    return (
      <div className="profile-list__loading">
        <div className="loading-spinner"></div>
        <div>Loading profiles...</div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="profile-list__error">
        <div className="error-icon">⚠️</div>
        <div className="error-text">Error: {error}</div>
        <button className="retry-btn" onClick={onRefresh}>Retry</button>
      </div>
    );
  }

  return (
    <div className="profile-list">
      <div className="profile-list__header">
        <h2 className="profile-list__title">Profile List</h2>
        <button className="profile-list__refresh-btn" onClick={onRefresh}>
          Refresh List
        </button>
      </div>

      {profiles.length === 0 ? (
        <div className="profile-list__empty">
          <div className="empty-icon">👤</div>
          <div className="empty-text">No profiles found.</div>
        </div>
      ) : (
        <ul className="profile-list__items">
          {profiles.map((profile) => (
            <li key={profile.userId} className="profile-list__item">
              <div className="profile-list__item-content">
                <div className="avatar">
                  {profile.name.charAt(0).toUpperCase()}
                </div>
                <div className="details">
                  <h3 className="name">{profile.name}</h3>
                  <p className="email">{profile.email}</p>
                </div>
                <div className={`status ${profile.isAccountVerified ? 'verified' : 'unverified'}`}>
                  {profile.isAccountVerified ? 'Verified' : 'Not Verified'}
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

import React from "react";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";

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
  if (loading) {
    return <div>Loading profiles...</div>;
  }

  if (error) {
    return (
      <div>
        <div>Error: {error}</div>
        <button onClick={onRefresh}>Retry</button>
      </div>
    );
  }

  return (
    <div>
      <button onClick={onRefresh} style={{ marginBottom: '10px' }}>Refresh List</button>
      {profiles.length === 0 ? (
        <p>No profiles found.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {profiles.map((profile) => (
            <li key={profile.userId} style={{ padding: '10px', border: '1px solid #ddd', marginBottom: '5px', borderRadius: '4px' }}>
              <div>
                <strong>{profile.name}</strong> - {profile.email}
                {profile.isAccountVerified ? (
                  <span style={{ color: "green", fontWeight: 'bold' }}> ✓ Verified</span>
                ) : (
                  <span style={{ color: "red", fontWeight: 'bold' }}> ✗ Not Verified</span>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

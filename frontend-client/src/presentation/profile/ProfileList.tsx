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
      <h2>Profile List</h2>
      <button onClick={onRefresh}>Refresh</button>
      {profiles.length === 0 ? (
        <p>No profiles found.</p>
      ) : (
        <ul>
          {profiles.map((profile) => (
            <li key={profile.userId}>
              <div>
                <strong>{profile.name}</strong> - {profile.email}
                {profile.isAccountVerified ? (
                  <span style={{ color: "green" }}> (Verified)</span>
                ) : (
                  <span style={{ color: "red" }}> (Not Verified)</span>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

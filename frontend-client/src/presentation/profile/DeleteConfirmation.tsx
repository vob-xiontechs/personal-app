import React from "react";
import "./delete-confirmation.scss";

interface DeleteConfirmationProps {
  profileName: string;
  isOpen: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  loading?: boolean;
}

export const DeleteConfirmation: React.FC<DeleteConfirmationProps> = ({
  profileName,
  isOpen,
  onConfirm,
  onCancel,
  loading = false,
}) => {
  if (!isOpen) return null;

  const handleBackdropClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget && !loading) {
      onCancel();
    }
  };

  return (
    <div className="delete-confirmation-overlay" onClick={handleBackdropClick}>
      <div className="delete-confirmation-modal">
        <div className="delete-confirmation-header">
          <div className="warning-icon">⚠️</div>
          <h2>Delete Profile</h2>
        </div>

        <div className="delete-confirmation-body">
          <p className="warning-message">
            Are you sure you want to delete the profile for <strong>"{profileName}"</strong>?
          </p>
          <div className="warning-details">
            <p className="danger-text">⚠️ This action cannot be undone.</p>
            <p className="info-text">The profile will be permanently removed from the database.</p>
          </div>
        </div>

        <div className="delete-confirmation-actions">
          <button
            className="btn-cancel"
            onClick={onCancel}
            disabled={loading}
          >
            Cancel
          </button>
          <button
            className="btn-delete"
            onClick={onConfirm}
            disabled={loading}
          >
            {loading ? "Deleting..." : "Delete Profile"}
          </button>
        </div>
      </div>
    </div>
  );
};

import React, { useEffect } from "react";
import "./notification.scss";

export type NotificationType = "success" | "error" | "warning" | "info";

export interface NotificationItem {
  id: string;
  type: NotificationType;
  title: string;
  message?: string;
  duration?: number;
  persistent?: boolean;
}

interface NotificationProps {
  notification: NotificationItem;
  onClose: (id: string) => void;
}

const NotificationIcon = ({ type }: { type: NotificationType }) => {
  switch (type) {
    case "success":
      return "✅";
    case "error":
      return "❌";
    case "warning":
      return "⚠️";
    case "info":
      return "ℹ️";
    default:
      return "ℹ️";
  }
};

export const Notification: React.FC<NotificationProps> = ({
  notification,
  onClose,
}) => {
  useEffect(() => {
    if (!notification.persistent && notification.duration !== 0) {
      const timer = setTimeout(() => {
        onClose(notification.id);
      }, notification.duration || 5000);

      return () => clearTimeout(timer);
    }
  }, [notification, onClose]);

  return (
    <div className={`notification notification--${notification.type}`}>
      <div className="notification__icon">
        <NotificationIcon type={notification.type} />
      </div>

      <div className="notification__content">
        <div className="notification__title">{notification.title}</div>
        {notification.message && (
          <div className="notification__message">{notification.message}</div>
        )}
      </div>

      <button
        className="notification__close"
        onClick={() => onClose(notification.id)}
        aria-label="Close notification"
      >
        ×
      </button>

      {!notification.persistent && (
        <div className="notification__progress">
          <div
            className="notification__progress-bar"
            style={{
              animationDuration: `${notification.duration || 5000}ms`,
            }}
          />
        </div>
      )}
    </div>
  );
};

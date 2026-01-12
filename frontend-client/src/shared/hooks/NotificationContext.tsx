import React, { createContext, useContext, useState } from "react";
import type { ReactNode } from "react";
import type { NotificationItem, NotificationType } from "../components/Notification";

interface NotificationContextType {
  notifications: NotificationItem[];
  showNotification: (
    type: NotificationType,
    title: string,
    message?: string,
    duration?: number,
    persistent?: boolean
  ) => void;
  hideNotification: (id: string) => void;
  clearAll: () => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(
  undefined
);

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error("useNotification must be used within a NotificationProvider");
  }
  return context;
};

interface NotificationProviderProps {
  children: ReactNode;
}

export const NotificationProvider: React.FC<NotificationProviderProps> = ({
  children,
}) => {
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);

  const showNotification = (
    type: NotificationType,
    title: string,
    message?: string,
    duration: number = 5000,
    persistent: boolean = false
  ) => {
    const id = `notification-${Date.now()}-${Math.random()}`;
    const notification: NotificationItem = {
      id,
      type,
      title,
      message,
      duration,
      persistent,
    };

    setNotifications((prev) => [...prev, notification]);

    // Auto-hide for non-persistent notifications
    if (!persistent) {
      setTimeout(() => {
        hideNotification(id);
      }, duration);
    }
  };

  const hideNotification = (id: string) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  const clearAll = () => {
    setNotifications([]);
  };

  const value: NotificationContextType = {
    notifications,
    showNotification,
    hideNotification,
    clearAll,
  };

  return (
    <NotificationContext.Provider value={value}>
      {children}
    </NotificationContext.Provider>
  );
};

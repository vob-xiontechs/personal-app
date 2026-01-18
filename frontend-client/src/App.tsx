import { ProfileContainer } from "./presentation/profile/ProfileContainer";
import { NotificationProvider, useNotification } from "./shared/hooks/NotificationContext";
import { NotificationContainer } from "./shared/components/NotificationContainer";

function AppContent() {
  const { notifications, hideNotification } = useNotification();

  return (
    <>
      <ProfileContainer />
      <NotificationContainer
        notifications={notifications}
        onClose={hideNotification}
      />
    </>
  );
}

function App() {
  return (
    <NotificationProvider>
      <AppContent />
    </NotificationProvider>
  );
}

export default App;

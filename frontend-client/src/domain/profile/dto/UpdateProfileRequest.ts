export interface UpdateProfileRequest {
  name: string;
  email: string;
  newPassword?: string;
  confirmPassword?: string;
  currentPassword: string;
}

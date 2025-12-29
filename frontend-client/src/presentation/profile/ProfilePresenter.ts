import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";

export class ProfilePresenter {
  static initialState(): ProfileRequest {
    return { name: "", email: "", password: "" };
  }
}

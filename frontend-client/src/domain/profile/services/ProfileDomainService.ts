import type { ProfileRequest } from "../dto/ProfileRequest";

export class ProfileDomainService {
  validate(request: ProfileRequest): void {
    if (!request.email.includes("@")) {
      throw new Error("Invalid email");
    }

    if (request.password.length < 6) {
      throw new Error("Password must be at least 6 characters");
    }
  }
}

// domain/profile/repositories/ProfileRepository.ts
// domain/profile/repositories/ProfileRepository.ts
import type { ProfileRequest } from "../dto/ProfileRequest";

export interface ProfileRepository {
  create(request: ProfileRequest): Promise<void>;
}

// domain/profile/repositories/ProfileRepository.ts
import type { ProfileRequest } from "../dto/ProfileRequest";
import type { ProfileResponse } from "../dto/ProfileResponse";

export interface ProfileRepository {
  create(request: ProfileRequest): Promise<void>;
  getList(): Promise<ProfileResponse[]>;
}

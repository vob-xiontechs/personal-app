// domain/profile/repositories/ProfileRepository.ts
import type { ProfileRequest } from "../dto/ProfileRequest";
import type { UpdateProfileRequest } from "../dto/UpdateProfileRequest";
import type { ProfileResponse } from "../dto/ProfileResponse";

export interface ProfileRepository {
  create(request: ProfileRequest): Promise<void>;
  getList(): Promise<ProfileResponse[]>;
  getDetail(userId: string): Promise<ProfileResponse>;
  update(userId: string, request: UpdateProfileRequest): Promise<ProfileResponse>;
  delete(userId: string): Promise<void>;
}

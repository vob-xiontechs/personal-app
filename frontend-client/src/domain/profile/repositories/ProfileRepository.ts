// domain/profile/repositories/ProfileRepository.ts
import type { ProfileRequest } from "../dto/ProfileRequest";
import type { UpdateProfileRequest } from "../dto/UpdateProfileRequest";
import type { ProfileResponse } from "../dto/ProfileResponse";

export interface ProfileRepository {
  getList(): Promise<ProfileResponse[]>;
  getListPaginated(page: number, size: number, sortBy?: string, sortDirection?: string): Promise<PaginatedResponse>;
  getDetail(userId: string): Promise<ProfileResponse>;
  create(profile: ProfileRequest): Promise<void>;
  update(userId: string, profile: UpdateProfileRequest): Promise<ProfileResponse>;
  delete(userId: string): Promise<void>;
}

export interface PaginatedResponse {
  content: ProfileResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

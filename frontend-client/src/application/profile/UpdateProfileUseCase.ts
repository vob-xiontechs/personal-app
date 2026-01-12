import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { UpdateProfileRequest } from "../../domain/profile/dto/UpdateProfileRequest";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";

export class UpdateProfileUseCase {
  private readonly repository: ProfileRepository;

  constructor(repository: ProfileRepository) {
    this.repository = repository;
  }

  async execute(userId: string, request: UpdateProfileRequest): Promise<ProfileResponse> {
    return this.repository.update(userId, request);
  }
}

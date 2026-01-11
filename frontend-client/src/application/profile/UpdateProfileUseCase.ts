import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";

export class UpdateProfileUseCase {
  private readonly repository: ProfileRepository;

  constructor(repository: ProfileRepository) {
    this.repository = repository;
  }

  async execute(userId: string, request: ProfileRequest): Promise<ProfileResponse> {
    return this.repository.update(userId, request);
  }
}

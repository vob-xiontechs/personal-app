import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";

export class GetProfileDetailUseCase {
  private readonly repository: ProfileRepository;

  constructor(repository: ProfileRepository) {
    this.repository = repository;
  }

  async execute(userId: string): Promise<ProfileResponse> {
    return this.repository.getDetail(userId);
  }
}

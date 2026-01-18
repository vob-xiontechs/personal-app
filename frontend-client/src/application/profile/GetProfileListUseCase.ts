import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";

export class GetProfileListUseCase {
  private readonly repository: ProfileRepository;

  constructor(repository: ProfileRepository) {
    this.repository = repository;
  }

  async execute(): Promise<ProfileResponse[]> {
    return this.repository.getList();
  }
}

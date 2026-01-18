import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";

export class DeleteProfileUseCase {
  private readonly repository: ProfileRepository;

  constructor(repository: ProfileRepository) {
    this.repository = repository;
  }

  async execute(userId: string): Promise<void> {
    return this.repository.delete(userId);
  }
}

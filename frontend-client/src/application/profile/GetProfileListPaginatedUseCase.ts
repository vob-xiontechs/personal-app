import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";

export class GetProfileListPaginatedUseCase {
  private repository: ProfileRepository;

  constructor(repository: ProfileRepository) {
    this.repository = repository;
  }

  async execute(
    page: number,
    size: number,
    sortBy?: string,
    sortDirection?: string
  ) {
    return await this.repository.getListPaginated(page, size, sortBy, sortDirection);
  }
}

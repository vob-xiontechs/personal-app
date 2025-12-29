import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import { ProfileDomainService } from "../../domain/profile/services/ProfileDomainService";
import type { UseCase } from "../common/UseCase";

export class CreateProfileUseCase implements UseCase<ProfileRequest> {
  private readonly repository: ProfileRepository;
  private readonly domainService: ProfileDomainService;

  constructor(
    repository: ProfileRepository,
    domainService: ProfileDomainService
  ) {
    this.repository = repository;
    this.domainService = domainService;
  }

  async execute(request: ProfileRequest): Promise<void> {
    this.domainService.validate(request);
    await this.repository.create(request);
  }
}

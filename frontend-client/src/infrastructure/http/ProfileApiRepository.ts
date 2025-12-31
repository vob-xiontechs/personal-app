import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import { HttpClient } from "./HttpClient";
import { env } from "../config/env";
import { ProfileMockService } from "../../mocks/profileMocks";

export class ProfileApiRepository implements ProfileRepository {
  private readonly http: HttpClient;
  private readonly useMock: boolean;

  constructor(http: HttpClient, useMock: boolean = true) {
    this.http = http;
    this.useMock = useMock;
  }

  async create(request: ProfileRequest): Promise<void> {
    if (this.useMock) {
      // Use mock data for testing
      return ProfileMockService.createProfile(request);
    }

      // Real API call (when not using mock)
      try {
        await this.http.post<void>(
          `${env.API_BASE_URL}/api/v1.0/register`,
          request
        );
      } catch (error: any) {
        // HttpClient already handles error responses and throws appropriate errors
        throw error;
      }
  }
}

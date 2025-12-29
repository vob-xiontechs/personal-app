import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import { HttpClient } from "./HttpClient";
import { env } from "../config/env";
import { MockProfileApiService } from "../../mocks/mockData";

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
      return MockProfileApiService.createProfile(request);
    }

    // Real API call (when not using mock)
    const response = await this.http.post(
      `${env.API_BASE_URL}/api/profile`,
      request
    );

    if (!response.ok) {
      throw new Error("API error");
    }
  }
}

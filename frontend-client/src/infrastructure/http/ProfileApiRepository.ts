import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import { HttpClient } from "./HttpClient";
import { env } from "../config/env";

export class ProfileApiRepository implements ProfileRepository {
  private readonly http: HttpClient;

  constructor(http: HttpClient) {
    this.http = http;
  }

  async create(request: ProfileRequest): Promise<void> {
    const response = await this.http.post(
      `${env.API_BASE_URL}/api/profile`,
      request
    );

    if (!response.ok) {
      throw new Error("API error");
    }
  }
}

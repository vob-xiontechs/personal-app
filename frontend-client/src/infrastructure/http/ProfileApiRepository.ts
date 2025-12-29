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
    // Mock API response for testing
    console.log("Mock API Call - Creating profile:", request);

    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 1500));

    // Mock validation - simulate email already exists error for testing
    if (request.email === "test@error.com") {
      throw new Error("Email already exists");
    }

    // Mock validation - simulate server error for testing
    if (request.email === "test@server.com") {
      throw new Error("Server error occurred");
    }

    // Mock success response
    console.log("Mock API Success - Profile created successfully");
    return Promise.resolve();
  }
}

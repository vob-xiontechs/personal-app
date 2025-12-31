import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import axios from 'axios';
import type { AxiosResponse } from 'axios';
import { env } from "../config/env";

export class ProfileApiRepository implements ProfileRepository {
  private axiosInstance;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: env.API_BASE_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Response interceptor for error handling
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response) {
          // Server responded with error status
          const { status, data } = error.response;
          const message = data?.message || data?.error || `HTTP ${status}: Request failed`;
          throw new Error(message);
        } else if (error.request) {
          // Network error
          throw new Error('Network Error: Unable to connect to server');
        } else {
          // Other error
          throw new Error(error.message || 'An unexpected error occurred');
        }
      }
    );
  }

  async create(request: ProfileRequest): Promise<void> {
    try {
      const response: AxiosResponse = await this.axiosInstance.post(
        '/api/v1.0/profiles/register',
        request
      );

      if (response.status >= 200 && response.status < 300) {
        // Success
        return;
      }
    } catch (error: any) {
      // Error handling is done in the interceptor
      throw error;
    }
  }
}

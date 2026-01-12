import type { ProfileRepository, PaginatedResponse } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import type { UpdateProfileRequest } from "../../domain/profile/dto/UpdateProfileRequest";
import type { ProfileResponse } from "../../domain/profile/dto/ProfileResponse";
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

  async getList(): Promise<ProfileResponse[]> {
    try {
      const response: AxiosResponse = await this.axiosInstance.get(
        '/api/v1.0/profiles'
      );

      if (response.status >= 200 && response.status < 300) {
        // The response data should contain the list
        return response.data.data || response.data;
      }

      throw new Error('Failed to fetch profile list');
    } catch (error: any) {
      // Error handling is done in the interceptor
      throw error;
    }
  }

  async getListPaginated(page: number, size: number, sortBy?: string, sortDirection?: string): Promise<PaginatedResponse> {
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
      });

      if (sortBy) params.append('sortBy', sortBy);
      if (sortDirection) params.append('sortDirection', sortDirection);

      const response: AxiosResponse = await this.axiosInstance.get(
        `/api/v1.0/profiles?${params.toString()}`
      );

      if (response.status >= 200 && response.status < 300) {
        // The response data should contain the paginated response
        return response.data.data || response.data;
      }

      throw new Error('Failed to fetch paginated profile list');
    } catch (error: any) {
      // Error handling is done in the interceptor
      throw error;
    }
  }

  async getDetail(userId: string): Promise<ProfileResponse> {
    try {
      const response: AxiosResponse = await this.axiosInstance.get(
        `/api/v1.0/profiles/${userId}`
      );

      if (response.status >= 200 && response.status < 300) {
        // The response data should contain the profile details
        return response.data.data || response.data;
      }

      throw new Error('Failed to fetch profile details');
    } catch (error: any) {
      // Error handling is done in the interceptor
      throw error;
    }
  }

  async update(userId: string, request: UpdateProfileRequest): Promise<ProfileResponse> {
    try {
      // Map UpdateProfileRequest to the backend expected format
      const backendRequest = {
        name: request.name,
        email: request.email,
        newPassword: request.newPassword || undefined,
        confirmPassword: request.confirmPassword || undefined,
        currentPassword: request.currentPassword
      };

      const response: AxiosResponse = await this.axiosInstance.put(
        `/api/v1.0/profiles/${userId}`,
        backendRequest
      );

      if (response.status >= 200 && response.status < 300) {
        // The response data should contain the updated profile
        return response.data.data || response.data;
      }

      throw new Error('Failed to update profile');
    } catch (error: any) {
      // Error handling is done in the interceptor
      throw error;
    }
  }

  async delete(userId: string): Promise<void> {
    try {
      const response: AxiosResponse = await this.axiosInstance.delete(
        `/api/v1.0/profiles/${userId}`
      );

      if (response.status >= 200 && response.status < 300) {
        // Success - no content expected
        return;
      }

      throw new Error('Failed to delete profile');
    } catch (error: any) {
      // Error handling is done in the interceptor
      throw error;
    }
  }
}

import axios from 'axios';
import type { AxiosRequestConfig, AxiosResponse } from 'axios';
import { ApiClient, ApiError, NetworkError } from '../base/ApiClient';

/**
 * Axios-based API client implementation
 * Provides concrete implementation using Axios library
 */
export class AxiosClient extends ApiClient {
  private axiosInstance;

  constructor(baseUrl: string, timeout: number = 10000) {
    super(baseUrl, timeout);

    this.axiosInstance = axios.create({
      baseURL: baseUrl,
      timeout: timeout,
    });

    // Request interceptor to add headers
    this.axiosInstance.interceptors.request.use((config) => {
      // Add current headers to request
      config.headers = { ...config.headers, ...this.headers } as any;
      return config;
    });

    // Response interceptor for error handling
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      (error) => this.handleError(error)
    );
  }

  async get<T>(endpoint: string, params?: Record<string, any>): Promise<T> {
    try {
      const config: AxiosRequestConfig = {
        params,
      };
      const response: AxiosResponse<T> = await this.axiosInstance.get(endpoint, config);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async post<T>(endpoint: string, data?: any): Promise<T> {
    try {
      const response: AxiosResponse<T> = await this.axiosInstance.post(endpoint, data);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async put<T>(endpoint: string, data?: any): Promise<T> {
    try {
      const response: AxiosResponse<T> = await this.axiosInstance.put(endpoint, data);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async patch<T>(endpoint: string, data?: any): Promise<T> {
    try {
      const response: AxiosResponse<T> = await this.axiosInstance.patch(endpoint, data);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async delete<T>(endpoint: string): Promise<T> {
    try {
      const response: AxiosResponse<T> = await this.axiosInstance.delete(endpoint);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  /**
   * Override to use Axios-specific error handling
   */
  protected handleError(error: any): never {
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      throw new ApiError(status, data?.message || data?.error || 'Request failed', data);
    } else if (error.request) {
      // Network error
      throw new NetworkError(error.message || 'Network request failed');
    } else {
      // Other error
      throw new ApiError(0, error.message || 'Unknown error occurred');
    }
  }

  /**
   * Set base URL dynamically
   */
  setBaseUrl(baseUrl: string): void {
    this.baseUrl = baseUrl;
    this.axiosInstance.defaults.baseURL = baseUrl;
  }

  /**
   * Set timeout dynamically
   */
  setTimeout(timeout: number): void {
    this.timeout = timeout;
    this.axiosInstance.defaults.timeout = timeout;
  }
}

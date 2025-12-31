import { AxiosClient } from '../clients/AxiosClient';
import { ApiError, NetworkError } from '../base/ApiClient';

/**
 * REST Service class
 * Handles REST API operations using Axios client
 */
export class RESTService {
  private client: AxiosClient;
  private baseEndpoint: string;

  constructor(baseUrl: string, baseEndpoint: string = '/api/v1.0') {
    this.client = new AxiosClient(baseUrl);
    this.baseEndpoint = baseEndpoint;
  }

  /**
   * GET request
   */
  async get<T = any>(
    endpoint: string,
    params?: Record<string, any>
  ): Promise<T> {
    try {
      return await this.client.get<T>(
        `${this.baseEndpoint}${endpoint}`,
        params
      );
    } catch (error) {
      this.handleRESTError(error);
    }
  }

  /**
   * POST request
   */
  async post<T = any>(
    endpoint: string,
    data?: any
  ): Promise<T> {
    try {
      return await this.client.post<T>(
        `${this.baseEndpoint}${endpoint}`,
        data
      );
    } catch (error) {
      this.handleRESTError(error);
    }
  }

  /**
   * PUT request
   */
  async put<T = any>(
    endpoint: string,
    data?: any
  ): Promise<T> {
    try {
      return await this.client.put<T>(
        `${this.baseEndpoint}${endpoint}`,
        data
      );
    } catch (error) {
      this.handleRESTError(error);
    }
  }

  /**
   * PATCH request
   */
  async patch<T = any>(
    endpoint: string,
    data?: any
  ): Promise<T> {
    try {
      return await this.client.patch<T>(
        `${this.baseEndpoint}${endpoint}`,
        data
      );
    } catch (error) {
      this.handleRESTError(error);
    }
  }

  /**
   * DELETE request
   */
  async delete<T = any>(endpoint: string): Promise<T> {
    try {
      return await this.client.delete<T>(
        `${this.baseEndpoint}${endpoint}`
      );
    } catch (error) {
      this.handleRESTError(error);
    }
  }

  /**
   * File upload
   */
  async uploadFile<T = any>(
    endpoint: string,
    file: File,
    fieldName: string = 'file',
    additionalData?: Record<string, any>
  ): Promise<T> {
    const formData = new FormData();
    formData.append(fieldName, file);

    if (additionalData) {
      Object.entries(additionalData).forEach(([key, value]) => {
        formData.append(key, String(value));
      });
    }

    // Override content type for file uploads
    this.client.setHeader('Content-Type', 'multipart/form-data');

    try {
      const result = await this.client.post<T>(
        `${this.baseEndpoint}${endpoint}`,
        formData
      );
      return result;
    } catch (error) {
      this.handleRESTError(error);
    } finally {
      // Reset content type
      this.client.setHeader('Content-Type', 'application/json');
    }
  }

  /**
   * Handle REST-specific errors
   */
  private handleRESTError(error: any): never {
    if (error instanceof ApiError) {
      // Enhance error messages based on status codes
      switch (error.status) {
        case 400:
          throw new ValidationError(error.message, error.data);
        case 401:
          throw new AuthenticationError(error.message);
        case 403:
          throw new AuthorizationError(error.message);
        case 404:
          throw new NotFoundError(error.message);
        case 409:
          throw new ConflictError(error.message);
        case 422:
          throw new ValidationError(error.message, error.data);
        case 429:
          throw new RateLimitError(error.message);
        case 500:
        case 502:
        case 503:
        case 504:
          throw new ServerError(error.message);
        default:
          throw error;
      }
    }

    if (error instanceof NetworkError) {
      throw new NetworkError('REST API unavailable');
    }

    throw new ApiError(0, error.message || 'Unknown REST API error');
  }

  /**
   * Set authentication token for REST requests
   */
  setAuthToken(token: string): void {
    this.client.setAuthToken(token);
  }

  /**
   * Remove authentication token
   */
  removeAuthToken(): void {
    this.client.removeAuthToken();
  }

  /**
   * Set custom header
   */
  setHeader(key: string, value: string): void {
    this.client.setHeader(key, value);
  }

  /**
   * Set base endpoint
   */
  setBaseEndpoint(endpoint: string): void {
    this.baseEndpoint = endpoint;
  }
}

/**
 * Specific REST error classes
 */
export class ValidationError extends ApiError {
  public readonly validationErrors?: Record<string, string[]>;

  constructor(message: string, validationData?: any) {
    super(400, message, validationData);
    this.name = 'ValidationError';
    this.validationErrors = validationData?.errors || validationData?.validationErrors;
  }
}

export class AuthenticationError extends ApiError {
  constructor(message: string) {
    super(401, message);
    this.name = 'AuthenticationError';
  }
}

export class AuthorizationError extends ApiError {
  constructor(message: string) {
    super(403, message);
    this.name = 'AuthorizationError';
  }
}

export class NotFoundError extends ApiError {
  constructor(message: string) {
    super(404, message);
    this.name = 'NotFoundError';
  }
}

export class ConflictError extends ApiError {
  constructor(message: string) {
    super(409, message);
    this.name = 'ConflictError';
  }
}

export class RateLimitError extends ApiError {
  constructor(message: string) {
    super(429, message);
    this.name = 'RateLimitError';
  }
}

export class ServerError extends ApiError {
  constructor(message: string) {
    super(500, message);
    this.name = 'ServerError';
  }
}

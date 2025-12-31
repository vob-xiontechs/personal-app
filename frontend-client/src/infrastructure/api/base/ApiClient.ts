/**
 * Abstract base class for API clients
 * Provides common functionality and interface for all API implementations
 */
export abstract class ApiClient {
  protected baseUrl: string;
  protected timeout: number;
  protected headers: Record<string, string>;

  constructor(baseUrl: string, timeout: number = 10000) {
    this.baseUrl = baseUrl;
    this.timeout = timeout;
    this.headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
  }

  /**
   * Set authorization header
   */
  setAuthToken(token: string): void {
    this.headers['Authorization'] = `Bearer ${token}`;
  }

  /**
   * Remove authorization header
   */
  removeAuthToken(): void {
    delete this.headers['Authorization'];
  }

  /**
   * Set custom header
   */
  setHeader(key: string, value: string): void {
    this.headers[key] = value;
  }

  /**
   * Remove custom header
   */
  removeHeader(key: string): void {
    delete this.headers[key];
  }

  /**
   * Abstract methods to be implemented by concrete classes
   */
  abstract get<T>(endpoint: string, params?: Record<string, any>): Promise<T>;
  abstract post<T>(endpoint: string, data?: any): Promise<T>;
  abstract put<T>(endpoint: string, data?: any): Promise<T>;
  abstract patch<T>(endpoint: string, data?: any): Promise<T>;
  abstract delete<T>(endpoint: string): Promise<T>;

  /**
   * Build full URL
   */
  protected buildUrl(endpoint: string): string {
    return `${this.baseUrl}${endpoint}`;
  }

  /**
   * Handle API errors consistently
   */
  protected handleError(error: any): never {
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      throw new ApiError(status, data?.message || data?.error || 'Request failed', data);
    } else if (error.request) {
      // Network error
      throw new NetworkError('Network request failed');
    } else {
      // Other error
      throw new ApiError(0, error.message || 'Unknown error occurred');
    }
  }
}

/**
 * Custom API Error class
 */
export class ApiError extends Error {
  public readonly status: number;
  public readonly data?: any;

  constructor(status: number, message: string, data?: any) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }

  isClientError(): boolean {
    return this.status >= 400 && this.status < 500;
  }

  isServerError(): boolean {
    return this.status >= 500;
  }

  isValidationError(): boolean {
    return this.status === 400;
  }

  isUnauthorized(): boolean {
    return this.status === 401;
  }

  isForbidden(): boolean {
    return this.status === 403;
  }

  isNotFound(): boolean {
    return this.status === 404;
  }

  isConflict(): boolean {
    return this.status === 409;
  }
}

/**
 * Network Error class
 */
export class NetworkError extends Error {
  constructor(message: string = 'Network request failed') {
    super(message);
    this.name = 'NetworkError';
  }
}

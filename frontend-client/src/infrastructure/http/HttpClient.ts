export interface HttpClientConfig {
  baseURL?: string;
  timeout?: number;
  headers?: Record<string, string>;
}

export interface RequestOptions {
  headers?: Record<string, string>;
  timeout?: number;
  signal?: AbortSignal;
}

export class HttpClient {
  private baseURL: string;
  private defaultHeaders: Record<string, string>;
  private defaultTimeout: number;

  constructor(config: HttpClientConfig = {}) {
    this.baseURL = config.baseURL || '';
    this.defaultHeaders = {
      'Content-Type': 'application/json',
      ...config.headers,
    };
    this.defaultTimeout = config.timeout || 10000; // 10 seconds default
  }

  /**
   * Build full URL from relative path
   */
  private buildUrl(url: string): string {
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }
    return this.baseURL + url;
  }

  /**
   * Create request configuration
   */
  private createRequestConfig(
    method: string,
    body?: any,
    options: RequestOptions = {}
  ): RequestInit {
    const headers = {
      ...this.defaultHeaders,
      ...options.headers,
    };

    const config: RequestInit = {
      method,
      headers,
    };

    // Add body for non-GET requests
    if (body !== undefined && method !== 'GET') {
      if (typeof body === 'string') {
        config.body = body;
      } else {
        config.body = JSON.stringify(body);
      }
    }

    // Add timeout with AbortController
    if (options.timeout || this.defaultTimeout) {
      const controller = new AbortController();
      config.signal = options.signal || controller.signal;

      const timeout = options.timeout || this.defaultTimeout;
      setTimeout(() => controller.abort(), timeout);
    }

    return config;
  }

  /**
   * Handle HTTP response
   */
  private async handleResponse(response: Response): Promise<any> {
    const contentType = response.headers.get('content-type');

    if (!response.ok) {
      let errorMessage = `HTTP ${response.status}: ${response.statusText}`;

      try {
        if (contentType?.includes('application/json')) {
          const errorData = await response.json();
          errorMessage = errorData.message || errorData.error || errorMessage;
        } else {
          const textError = await response.text();
          if (textError) {
            errorMessage = textError;
          }
        }
      } catch (e) {
        // If we can't parse the error response, use the default message
      }

      throw new Error(errorMessage);
    }

    // Return empty response for 204 No Content
    if (response.status === 204) {
      return null;
    }

    // Parse JSON response
    if (contentType?.includes('application/json')) {
      return response.json();
    }

    // Return text for other content types
    return response.text();
  }

  /**
   * GET request
   */
  async get<T = any>(url: string, options: RequestOptions = {}): Promise<T> {
    const fullUrl = this.buildUrl(url);
    const config = this.createRequestConfig('GET', undefined, options);

    const response = await fetch(fullUrl, config);
    return this.handleResponse(response);
  }

  /**
   * POST request
   */
  async post<T = any, B = any>(url: string, body?: B, options: RequestOptions = {}): Promise<T> {
    const fullUrl = this.buildUrl(url);
    const config = this.createRequestConfig('POST', body, options);

    const response = await fetch(fullUrl, config);
    return this.handleResponse(response);
  }

  /**
   * PUT request
   */
  async put<T = any, B = any>(url: string, body?: B, options: RequestOptions = {}): Promise<T> {
    const fullUrl = this.buildUrl(url);
    const config = this.createRequestConfig('PUT', body, options);

    const response = await fetch(fullUrl, config);
    return this.handleResponse(response);
  }

  /**
   * PATCH request
   */
  async patch<T = any, B = any>(url: string, body?: B, options: RequestOptions = {}): Promise<T> {
    const fullUrl = this.buildUrl(url);
    const config = this.createRequestConfig('PATCH', body, options);

    const response = await fetch(fullUrl, config);
    return this.handleResponse(response);
  }

  /**
   * DELETE request
   */
  async delete<T = any>(url: string, options: RequestOptions = {}): Promise<T> {
    const fullUrl = this.buildUrl(url);
    const config = this.createRequestConfig('DELETE', undefined, options);

    const response = await fetch(fullUrl, config);
    return this.handleResponse(response);
  }

  /**
   * HEAD request
   */
  async head(url: string, options: RequestOptions = {}): Promise<Response> {
    const fullUrl = this.buildUrl(url);
    const config = this.createRequestConfig('HEAD', undefined, options);

    return fetch(fullUrl, config);
  }

  /**
   * OPTIONS request
   */
  async options(url: string, options: RequestOptions = {}): Promise<Response> {
    const fullUrl = this.buildUrl(url);
    const config = this.createRequestConfig('OPTIONS', undefined, options);

    return fetch(fullUrl, config);
  }

  /**
   * Raw fetch request (for advanced use cases)
   */
  async request(url: string, config: RequestInit): Promise<Response> {
    const fullUrl = this.buildUrl(url);
    return fetch(fullUrl, config);
  }

  /**
   * Set default headers
   */
  setDefaultHeader(key: string, value: string): void {
    this.defaultHeaders[key] = value;
  }

  /**
   * Remove default header
   */
  removeDefaultHeader(key: string): void {
    delete this.defaultHeaders[key];
  }

  /**
   * Set authorization token
   */
  setAuthToken(token: string, type: string = 'Bearer'): void {
    this.setDefaultHeader('Authorization', `${type} ${token}`);
  }

  /**
   * Clear authorization token
   */
  clearAuthToken(): void {
    this.removeDefaultHeader('Authorization');
  }
}

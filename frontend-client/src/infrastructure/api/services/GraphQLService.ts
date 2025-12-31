import { AxiosClient } from '../clients/AxiosClient';
import { ApiError, NetworkError } from '../base/ApiClient';

/**
 * GraphQL Service class
 * Handles GraphQL operations using Axios client
 */
export class GraphQLService {
  private client: AxiosClient;
  private endpoint: string;

  constructor(baseUrl: string, endpoint: string = '/graphql') {
    this.client = new AxiosClient(baseUrl);
    this.endpoint = endpoint;
  }

  /**
   * Execute GraphQL query
   */
  async query<T = any>(
    query: string,
    variables?: Record<string, any>,
    operationName?: string
  ): Promise<GraphQLResponse<T>> {
    const payload: GraphQLRequest = {
      query,
      variables,
      operationName,
    };

    try {
      const response = await this.client.post<GraphQLResponse<T>>(
        this.endpoint,
        payload
      );

      // Check for GraphQL errors
      if (response.errors && response.errors.length > 0) {
        const error = response.errors[0];
        throw new GraphQLError(error.message, error);
      }

      return response;
    } catch (error) {
      this.handleGraphQLError(error);
    }
  }

  /**
   * Execute GraphQL mutation
   */
  async mutate<T = any>(
    mutation: string,
    variables?: Record<string, any>,
    operationName?: string
  ): Promise<GraphQLResponse<T>> {
    const payload: GraphQLRequest = {
      query: mutation,
      variables,
      operationName,
    };

    try {
      const response = await this.client.post<GraphQLResponse<T>>(
        this.endpoint,
        payload
      );

      // Check for GraphQL errors
      if (response.errors && response.errors.length > 0) {
        const error = response.errors[0];
        throw new GraphQLError(error.message, error);
      }

      return response;
    } catch (error) {
      this.handleGraphQLError(error);
    }
  }

  /**
   * Handle GraphQL-specific errors
   */
  private handleGraphQLError(error: any): never {
    if (error instanceof GraphQLError) {
      throw error;
    }

    if (error instanceof ApiError) {
      if (error.isServerError()) {
        throw new GraphQLError(`Server Error: ${error.message}`, error);
      }
      throw error;
    }

    if (error instanceof NetworkError) {
      // Could fallback to different endpoint or retry logic here
      throw new GraphQLError('Network Error: GraphQL service unavailable', error);
    }

    throw new GraphQLError(error.message || 'Unknown GraphQL error', error);
  }

  /**
   * Set authentication token for GraphQL requests
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
}

/**
 * GraphQL Error class
 */
export class GraphQLError extends Error {
  public readonly originalError?: any;
  public readonly extensions?: Record<string, any>;
  public readonly locations?: Array<{ line: number; column: number }>;
  public readonly path?: string[];

  constructor(message: string, originalError?: any) {
    super(message);
    this.name = 'GraphQLError';
    this.originalError = originalError;

    if (originalError?.extensions) {
      this.extensions = originalError.extensions;
    }

    if (originalError?.locations) {
      this.locations = originalError.locations;
    }

    if (originalError?.path) {
      this.path = originalError.path;
    }
  }
}

/**
 * GraphQL Request interface
 */
export interface GraphQLRequest {
  query: string;
  variables?: Record<string, any>;
  operationName?: string;
}

/**
 * GraphQL Response interface
 */
export interface GraphQLResponse<T = any> {
  data?: T;
  errors?: Array<{
    message: string;
    locations?: Array<{ line: number; column: number }>;
    path?: string[];
    extensions?: Record<string, any>;
  }>;
  extensions?: Record<string, any>;
}

/**
 * GraphQL Error interface
 */
export interface GraphQLErrorData {
  message: string;
  locations?: Array<{ line: number; column: number }>;
  path?: string[];
  extensions?: Record<string, any>;
}

import type { ProfileRepository } from "../../domain/profile/repositories/ProfileRepository";
import type { ProfileRequest } from "../../domain/profile/dto/ProfileRequest";
import { GraphQLService, GraphQLError } from "../api/services/GraphQLService";
import { RESTService, ValidationError, ConflictError, ServerError } from "../api/services/RESTService";
import { env } from "../config/env";

export class ProfileHybridRepository implements ProfileRepository {
  private graphqlService: GraphQLService;
  private restService: RESTService;
  private useGraphQL: boolean;

  constructor(useGraphQL: boolean = true) {
    this.graphqlService = new GraphQLService(env.API_BASE_URL);
    this.restService = new RESTService(env.API_BASE_URL);
    this.useGraphQL = useGraphQL;
  }

  async create(request: ProfileRequest): Promise<void> {
    if (this.useGraphQL) {
      return this.createWithGraphQL(request);
    } else {
      return this.createWithREST(request);
    }
  }

  // GraphQL implementation using service class
  private async createWithGraphQL(request: ProfileRequest): Promise<void> {
    try {
      console.log('📡 Using GraphQL Service for registration');

      const mutation = `
        mutation RegisterUser($input: RegisterUserInput!) {
          registerUser(input: $input) {
            id
            userId
            name
            email
            isAccountVerified
            createdAt
            updatedAt
          }
        }
      `;

      const variables = {
        input: {
          name: request.name,
          email: request.email,
          password: request.password,
        },
      };

      const response = await this.graphqlService.mutate(mutation, variables);

      if (!response.data?.registerUser) {
        console.error('❌ No data returned:', response);
        throw new GraphQLError('GraphQL Registration failed: No data returned');
      }

      console.log('✅ GraphQL User registered successfully:', response.data.registerUser);

    } catch (error: any) {
      console.error('❌ GraphQL Service Error:', error);

      if (error instanceof GraphQLError) {
        // GraphQL-specific error
        throw error;
      }

      // Network or other error - fallback to REST
      console.warn('⚠️ GraphQL failed, falling back to REST:', error.message);
      return this.createWithREST(request);
    }
  }

  // REST implementation using service class
  private async createWithREST(request: ProfileRequest): Promise<void> {
    try {
      console.log('🔄 Using REST Service for registration');

      await this.restService.post('/register', request);

      console.log('✅ REST User registered successfully');

    } catch (error: any) {
      console.error('❌ REST Service Error:', error);

      // Handle specific REST errors
      if (error instanceof ValidationError) {
        throw new Error(`Validation Error: ${error.message}`);
      }

      if (error instanceof ConflictError) {
        throw new Error(`User already exists: ${error.message}`);
      }

      if (error instanceof ServerError) {
        throw new Error(`Server Error: ${error.message}`);
      }

      // Generic error
      throw new Error(error.message || 'REST registration failed');
    }
  }

  // Method to switch between GraphQL and REST
  setUseGraphQL(useGraphQL: boolean): void {
    this.useGraphQL = useGraphQL;
    console.log(`🔄 Switched to ${useGraphQL ? 'GraphQL' : 'REST'} mode`);
  }

  // Method to check current API mode
  isUsingGraphQL(): boolean {
    return this.useGraphQL;
  }

  // Method to set authentication for both services
  setAuthToken(token: string): void {
    this.graphqlService.setAuthToken(token);
    this.restService.setAuthToken(token);
  }

  // Method to remove authentication
  removeAuthToken(): void {
    this.graphqlService.removeAuthToken();
    this.restService.removeAuthToken();
  }
}

// Backward compatibility
export class ProfileGraphQLRepository extends ProfileHybridRepository {
  constructor() {
    super(true); // Always use GraphQL
  }
}

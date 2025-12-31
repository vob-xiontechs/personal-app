// GraphQL Type Definitions based on backend schema

// User Entity Types
export interface User {
  __typename?: string; // Apollo Client adds this
  id: string;
  userId: string;
  name: string;
  email: string;
  isAccountVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

// Pagination Types
export interface UserConnection {
  content: User[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Input Types
export interface RegisterUserInput {
  name: string;
  email: string;
  password: string;
}

export interface UpdateUserInput {
  name?: string;
  email?: string;
  isAccountVerified?: boolean;
}

// Query Variables
export interface GetUserQueryVariables {
  id: string;
}

export interface GetUsersQueryVariables {
  page?: number;
  size?: number;
}

// Mutation Variables
export interface RegisterUserMutationVariables {
  input: RegisterUserInput;
}

export interface UpdateUserMutationVariables {
  id: string;
  input: UpdateUserInput;
}

export interface DeleteUserMutationVariables {
  id: string;
}

// Query Response Types
export interface GetUserQueryResponse {
  user: User | null;
}

export interface GetUsersQueryResponse {
  users: UserConnection;
}

export interface GetCurrentUserQueryResponse {
  me: User | null;
}

// Mutation Response Types
export interface RegisterUserMutationResponse {
  registerUser: User;
}

export interface UpdateUserMutationResponse {
  updateUser: User;
}

export interface DeleteUserMutationResponse {
  deleteUser: boolean;
}

// Apollo Client Types
export interface GraphQLError {
  message: string;
  locations?: Array<{
    line: number;
    column: number;
  }>;
  path?: string[];
  extensions?: Record<string, any>;
}

export interface NetworkError {
  name: string;
  message: string;
  stack?: string;
}

// Error handling types
export interface ApolloError {
  graphQLErrors?: GraphQLError[];
  networkError?: NetworkError;
  message: string;
  extraInfo?: any;
}

// Form state types
export interface FormState {
  loading: boolean;
  error: string | null;
  success: boolean;
}

// API Response wrapper
export interface ApiResponse<T> {
  data?: T;
  error?: ApolloError;
  loading: boolean;
}

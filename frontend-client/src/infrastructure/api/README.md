# API Layer Architecture - OOP Design

## 🏗️ **Architecture Overview**

This API layer follows strict Object-Oriented Programming principles with proper abstraction, inheritance, and encapsulation.

```
src/infrastructure/api/
├── base/
│   └── ApiClient.ts           # Abstract base class
├── clients/
│   ├── AxiosClient.ts         # Axios implementation
│   └── FetchClient.ts         # Fetch API implementation
├── services/
│   ├── GraphQLService.ts      # GraphQL operations
│   └── RESTService.ts         # REST operations
└── README.md                  # This documentation
```

## 🎯 **Design Principles**

### **1. SOLID Principles**
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Easy to extend without modifying existing code
- **Liskov Substitution**: Implementations are interchangeable
- **Interface Segregation**: Focused interfaces
- **Dependency Inversion**: Depends on abstractions, not concretions

### **2. Clean Architecture**
- **Separation of Concerns**: HTTP client, business logic, data transformation
- **Dependency Injection**: Services injected into repositories
- **Error Handling**: Centralized and typed error management
- **Configuration**: Environment-based API configuration

## 📋 **Class Hierarchy**

### **Base Classes**

#### `ApiClient` (Abstract)
```typescript
abstract class ApiClient {
  // Common functionality for all API clients
  protected baseUrl: string;
  protected timeout: number;
  protected headers: Record<string, string>;

  // Abstract methods implemented by concrete classes
  abstract get<T>(endpoint: string, params?: any): Promise<T>;
  abstract post<T>(endpoint: string, data?: any): Promise<T>;
  // ... other HTTP methods

  // Common methods
  setAuthToken(token: string): void;
  removeAuthToken(): void;
  setHeader(key: string, value: string): void;
}
```

#### `ApiError` & `NetworkError`
```typescript
class ApiError extends Error {
  public readonly status: number;
  public readonly data?: any;

  // Utility methods
  isClientError(): boolean;
  isServerError(): boolean;
  isValidationError(): boolean;
  // ... other status checks
}

class NetworkError extends Error {
  // Network-specific error handling
}
```

### **Client Implementations**

#### `AxiosClient`
- Uses Axios library for HTTP requests
- Request/response interceptors
- Automatic header management
- Timeout and retry logic

#### `FetchClient`
- Native browser Fetch API
- AbortController for cancellation
- Manual header management
- Stream processing capabilities

### **Service Classes**

#### `GraphQLService`
```typescript
class GraphQLService {
  private client: AxiosClient;

  async query<T>(query: string, variables?: any): Promise<GraphQLResponse<T>>;
  async mutate<T>(mutation: string, variables?: any): Promise<GraphQLResponse<T>>;

  // Error handling specific to GraphQL
  private handleGraphQLError(error: any): never;
}
```

#### `RESTService`
```typescript
class RESTService {
  private client: AxiosClient;

  async get<T>(endpoint: string, params?: any): Promise<T>;
  async post<T>(endpoint: string, data?: any): Promise<T>;
  // ... other REST methods

  // Error handling specific to REST APIs
  private handleRESTError(error: any): never;
}
```

## 🔄 **Usage Examples**

### **Basic Usage**
```typescript
// Create service instance
const graphqlService = new GraphQLService('http://localhost:8080');
const restService = new RESTService('http://localhost:8080');

// Set authentication
graphqlService.setAuthToken('jwt-token');
restService.setAuthToken('jwt-token');

// Execute operations
const userData = await graphqlService.query(GET_USER_QUERY, { id: '123' });
const profileData = await restService.post('/register', userInput);
```

### **Hybrid Repository Pattern**
```typescript
class ProfileHybridRepository implements ProfileRepository {
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

  // Automatic fallback on errors
  private async createWithGraphQL(request: ProfileRequest): Promise<void> {
    try {
      // GraphQL implementation
    } catch (error) {
      // Fallback to REST
      return this.createWithREST(request);
    }
  }
}
```

## 🛡️ **Error Handling Strategy**

### **Error Classification**
- **Network Errors**: Connection issues, timeouts
- **GraphQL Errors**: Schema validation, field errors
- **REST Errors**: HTTP status codes, validation errors
- **Authentication Errors**: Unauthorized, forbidden access

### **Error Recovery**
- **Retry Logic**: Automatic retries for transient errors
- **Fallback Strategy**: GraphQL → REST fallback
- **User Feedback**: Clear, actionable error messages
- **Logging**: Comprehensive error logging for debugging

## ⚙️ **Configuration Management**

### **Environment Configuration**
```typescript
export const configureForEnvironment = (env: string): void => {
  switch (env) {
    case 'development':
      // Development settings
      break;
    case 'production':
      // Production settings
      break;
  }
};
```

### **Dynamic Configuration**
```typescript
// Runtime API switching
const repository = new ProfileHybridRepository();
repository.setUseGraphQL(false); // Switch to REST
repository.setUseGraphQL(true);  // Switch to GraphQL
```

## 📊 **Performance Optimizations**

### **Caching Strategy**
- **GraphQL**: Apollo Client caching
- **REST**: Custom caching with TTL
- **Memory Management**: Automatic cache cleanup

### **Request Optimization**
- **Batching**: Multiple operations in single request
- **Compression**: Response compression
- **Connection Pooling**: Axios connection reuse

### **Monitoring**
- **Request Metrics**: Response times, success rates
- **Error Tracking**: Error frequency and types
- **Performance Logging**: Slow query detection

## 🧪 **Testing Strategy**

### **Unit Tests**
```typescript
describe('GraphQLService', () => {
  it('should execute query successfully', async () => {
    // Mock client and test query execution
  });
});
```

### **Integration Tests**
```typescript
describe('ProfileHybridRepository', () => {
  it('should fallback to REST on GraphQL failure', async () => {
    // Test automatic fallback behavior
  });
});
```

## 🔧 **Extensibility**

### **Adding New Clients**
```typescript
export class CustomClient extends ApiClient {
  async get<T>(endpoint: string): Promise<T> {
    // Custom implementation
  }
  // ... implement other abstract methods
}
```

### **Adding New Services**
```typescript
export class WebSocketService {
  // Real-time data service
  connect(): void;
  subscribe(event: string, callback: Function): void;
}
```

## 📈 **Benefits Achieved**

✅ **Maintainability**: Clear separation of concerns
✅ **Testability**: Isolated, mockable components
✅ **Scalability**: Easy to add new APIs and clients
✅ **Reliability**: Comprehensive error handling and fallbacks
✅ **Performance**: Optimized requests and caching
✅ **Developer Experience**: Type-safe, well-documented APIs

This architecture provides a robust, scalable foundation for API interactions while maintaining clean, maintainable code.

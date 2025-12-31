/**
 * API Configuration - Hybrid GraphQL + Axios Setup
 */

export interface ApiConfig {
  useGraphQL: boolean;
  fallbackToAxios: boolean;
  graphqlEndpoint: string;
  restEndpoint: string;
  timeout: number;
}

// Default configuration
export const defaultApiConfig: ApiConfig = {
  useGraphQL: true,
  fallbackToAxios: true,
  graphqlEndpoint: '/graphql',
  restEndpoint: '/api/v1.0',
  timeout: 10000,
};

// Runtime configuration (can be changed dynamically)
let currentApiConfig: ApiConfig = { ...defaultApiConfig };

export const getApiConfig = (): ApiConfig => currentApiConfig;

export const setApiConfig = (config: Partial<ApiConfig>): void => {
  currentApiConfig = { ...currentApiConfig, ...config };
  console.log('🔧 API Configuration updated:', currentApiConfig);
};

export const switchToGraphQL = (): void => {
  setApiConfig({ useGraphQL: true });
  console.log('📡 Switched to GraphQL API');
};

export const switchToAxios = (): void => {
  setApiConfig({ useGraphQL: false });
  console.log('🔄 Switched to Axios/REST API');
};

export const toggleApiMode = (): void => {
  const newMode = !currentApiConfig.useGraphQL;
  setApiConfig({ useGraphQL: newMode });
  console.log(`🔄 Toggled to ${newMode ? 'GraphQL' : 'Axios'} API`);
};

// Utility functions
export const isUsingGraphQL = (): boolean => currentApiConfig.useGraphQL;
export const isUsingAxios = (): boolean => !currentApiConfig.useGraphQL;
export const hasFallback = (): boolean => currentApiConfig.fallbackToAxios;

// Environment-based configuration
export const configureForEnvironment = (env: string): void => {
  switch (env) {
    case 'development':
      setApiConfig({
        useGraphQL: true,
        fallbackToAxios: true,
        timeout: 30000, // Longer timeout for dev
      });
      break;

    case 'staging':
      setApiConfig({
        useGraphQL: true,
        fallbackToAxios: true,
        timeout: 15000,
      });
      break;

    case 'production':
      setApiConfig({
        useGraphQL: true,
        fallbackToAxios: false, // No fallback in production
        timeout: 10000,
      });
      break;

    default:
      // Keep default configuration
      break;
  }

  console.log(`🏭 Configured for ${env} environment:`, getApiConfig());
};

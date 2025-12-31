import { ApolloClient, InMemoryCache, HttpLink, from } from '@apollo/client';
import { onError } from '@apollo/client/link/error';
import { env } from '../config/env';

// Error handling link
const errorLink = onError((error: any) => {
  const { graphQLErrors, networkError } = error;
  if (graphQLErrors) {
    graphQLErrors.forEach((err: any) => {
      console.error(`[GraphQL error]: Message: ${err.message}`);
    });
  }

  if (networkError) {
    console.error(`[Network error]: ${networkError}`);
  }
});

// HTTP link for GraphQL endpoint
const httpLink = new HttpLink({
  uri: `${env.API_BASE_URL}/graphql`,
  credentials: 'same-origin', // or 'include' for cross-origin
});

// Combine links
const link = from([errorLink, httpLink]);

// Apollo Client configuration
export const apolloClient = new ApolloClient({
  link,
  cache: new InMemoryCache({
    // Custom type policies for better caching
    typePolicies: {
      Query: {
        fields: {
          // Define how to handle specific query fields
        },
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network',
      errorPolicy: 'ignore',
    },
    query: {
      fetchPolicy: 'cache-first',
      errorPolicy: 'all',
    },
    mutate: {
      errorPolicy: 'all',
      fetchPolicy: 'no-cache',
    },
  },
});

// Export for use in components
export default apolloClient;

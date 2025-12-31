import { gql } from '@apollo/client';

// GraphQL Queries
export const GET_USER = gql`
  query GetUser($id: ID!) {
    user(id: $id) {
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

export const GET_USERS = gql`
  query GetUsers($page: Int = 0, $size: Int = 10) {
    users(page: $page, size: $size) {
      content {
        id
        userId
        name
        email
        isAccountVerified
        createdAt
        updatedAt
      }
      totalElements
      totalPages
      size
      number
      first
      last
    }
  }
`;

export const GET_CURRENT_USER = gql`
  query GetCurrentUser {
    me {
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

// GraphQL Mutations
export const REGISTER_USER = gql`
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

export const UPDATE_USER = gql`
  mutation UpdateUser($id: ID!, $input: UpdateUserInput!) {
    updateUser(id: $id, input: $input) {
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

export const DELETE_USER = gql`
  mutation DeleteUser($id: ID!) {
    deleteUser(id: $id)
  }
`;

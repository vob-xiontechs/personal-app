import type { ProfileRequest } from "../domain/profile/dto/ProfileRequest";

// Mock data for testing profile creation
export const mockProfileData = {
  // Sample profiles for testing
  profiles: [
    {
      id: "1",
      name: "John Doe",
      email: "john.doe@example.com",
      password: "password123"
    },
    {
      id: "2",
      name: "Jane Smith",
      email: "jane.smith@example.com",
      password: "password456"
    }
  ],

  // Mock API responses
  responses: {
    success: {
      message: "Profile created successfully",
      status: 201
    },
    emailExists: {
      message: "Email already exists",
      status: 409
    },
    serverError: {
      message: "Server error occurred",
      status: 500
    },
    invalidData: {
      message: "Invalid data provided",
      status: 400
    }
  },

  // Test scenarios
  testScenarios: {
    success: {
      input: {
        name: "Test User",
        email: "test@example.com",
        password: "password123"
      },
      expected: "success"
    },
    emailExists: {
      input: {
        name: "Test User",
        email: "test@error.com",
        password: "password123"
      },
      expected: "Email already exists"
    },
    serverError: {
      input: {
        name: "Test User",
        email: "test@server.com",
        password: "password123"
      },
      expected: "Server error occurred"
    },
    invalidEmail: {
      input: {
        name: "Test User",
        email: "invalid-email",
        password: "password123"
      },
      expected: "Invalid email"
    },
    shortPassword: {
      input: {
        name: "Test User",
        email: "test@example.com",
        password: "123"
      },
      expected: "Password must be at least 6 characters"
    }
  }
};

// Mock API service for profile operations
export class MockProfileApiService {
  static delay = 1500; // 1.5 seconds delay to simulate network

  static async createProfile(request: ProfileRequest): Promise<void> {
    console.log("🎭 Mock API Call - Creating profile:", request);

    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, this.delay));

    // Mock validation scenarios
    if (request.email === "test@error.com") {
      console.error("❌ Mock API Error - Email already exists");
      throw new Error(mockProfileData.responses.emailExists.message);
    }

    if (request.email === "test@server.com") {
      console.error("❌ Mock API Error - Server error");
      throw new Error(mockProfileData.responses.serverError.message);
    }

    if (request.email === "test@invalid.com") {
      console.error("❌ Mock API Error - Invalid data");
      throw new Error(mockProfileData.responses.invalidData.message);
    }

    // Success case
    console.log("✅ Mock API Success - Profile created:", request);
    return Promise.resolve();
  }

  static async getProfiles() {
    console.log("🎭 Mock API Call - Getting profiles");
    await new Promise(resolve => setTimeout(resolve, this.delay));
    return Promise.resolve(mockProfileData.profiles);
  }

  static async getProfileById(id: string) {
    console.log("🎭 Mock API Call - Getting profile by ID:", id);
    await new Promise(resolve => setTimeout(resolve, this.delay));
    const profile = mockProfileData.profiles.find(p => p.id === id);
    return Promise.resolve(profile || null);
  }
}

// Utility functions for testing
export const mockUtils = {
  // Reset mock data to initial state
  reset: () => {
    console.log("🔄 Mock data reset to initial state");
  },

  // Add custom profile for testing
  addProfile: (profile: typeof mockProfileData.profiles[0]) => {
    mockProfileData.profiles.push(profile);
    console.log("➕ Mock profile added:", profile);
  },

  // Clear all profiles
  clearProfiles: () => {
    mockProfileData.profiles = [];
    console.log("🗑️ All mock profiles cleared");
  },

  // Set custom delay
  setDelay: (delay: number) => {
    MockProfileApiService.delay = delay;
    console.log("⏱️ Mock API delay set to:", delay, "ms");
  }
};

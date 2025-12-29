import type { ProfileRequest } from "../domain/profile/dto/ProfileRequest";

// Mock data specifically for profile creation testing
export const profileMockData = {
  // Sample valid profiles
  validProfiles: [
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

  // Test scenarios for profile creation
  testScenarios: {
    success: {
      input: {
        name: "Test User",
        email: "test@example.com",
        password: "password123"
      } as ProfileRequest,
      expected: "success"
    },
    emailExists: {
      input: {
        name: "Test User",
        email: "test@error.com",
        password: "password123"
      } as ProfileRequest,
      expected: "Email already exists"
    },
    serverError: {
      input: {
        name: "Test User",
        email: "test@server.com",
        password: "password123"
      } as ProfileRequest,
      expected: "Server error occurred"
    },
    invalidEmail: {
      input: {
        name: "Test User",
        email: "invalid-email",
        password: "password123"
      } as ProfileRequest,
      expected: "Invalid email"
    },
    shortPassword: {
      input: {
        name: "Test User",
        email: "test@example.com",
        password: "123"
      } as ProfileRequest,
      expected: "Password must be at least 6 characters"
    }
  }
};

// Mock service for profile operations
export class ProfileMockService {
  private static delay = 1500; // 1.5 seconds delay to simulate network

  static async createProfile(request: ProfileRequest): Promise<void> {
    console.log("🎭 [ProfileMock] Creating profile:", request);

    // Simulate network delay
    await new Promise(resolve => setTimeout(resolve, this.delay));

    // Mock validation scenarios
    if (request.email === "test@error.com") {
      console.error("❌ [ProfileMock] Email already exists");
      throw new Error("Email already exists");
    }

    if (request.email === "test@server.com") {
      console.error("❌ [ProfileMock] Server error occurred");
      throw new Error("Server error occurred");
    }

    // Success case
    console.log("✅ [ProfileMock] Profile created successfully");
    return Promise.resolve();
  }

  static setDelay(delay: number) {
    this.delay = delay;
    console.log(`⏱️ [ProfileMock] Delay set to ${delay}ms`);
  }
}

import { ProfileMockService, profileMockData } from "./profileMocks";
import { ProfileDomainService } from "../domain/profile/services/ProfileDomainService";

/**
 * Example usage of profile mock data for testing
 * This file demonstrates how to use separate mock files for different scenarios
 */

// Example: Testing successful profile creation
export const testSuccessfulProfileCreation = async () => {
  const { input } = profileMockData.testScenarios.success;

  try {
    // Domain validation (client-side)
    const domainService = new ProfileDomainService();
    domainService.validate(input); // Should not throw

    // API call (mocked)
    await ProfileMockService.createProfile(input);

    console.log("✅ Test passed: Profile created successfully");
    return { success: true, message: "Profile created successfully" };
  } catch (error: any) {
    console.error("❌ Test failed:", error.message);
    return { success: false, message: error.message };
  }
};

// Example: Testing email already exists error
export const testEmailExistsError = async () => {
  const { input, expected } = profileMockData.testScenarios.emailExists;

  try {
    // Domain validation should pass
    const domainService = new ProfileDomainService();
    domainService.validate(input); // Should not throw

    // API call should fail
    await ProfileMockService.createProfile(input);

    console.error("❌ Test failed: Expected error but got success");
    return { success: false, message: "Expected error but got success" };
  } catch (error: any) {
    if (error.message === expected) {
      console.log("✅ Test passed: Correct error message");
      return { success: true, message: error.message };
    } else {
      console.error("❌ Test failed: Wrong error message");
      return { success: false, message: `Expected "${expected}" but got "${error.message}"` };
    }
  }
};

// Example: Testing domain validation (email format)
export const testEmailValidation = () => {
  const { input } = profileMockData.testScenarios.invalidEmail;

  try {
    const domainService = new ProfileDomainService();
    domainService.validate(input); // Should throw

    console.error("❌ Test failed: Expected validation error");
    return { success: false, message: "Expected validation error" };
  } catch (error: any) {
    if (error.message === "Invalid email") {
      console.log("✅ Test passed: Email validation working");
      return { success: true, message: "Email validation working" };
    } else {
      console.error("❌ Test failed: Wrong validation error");
      return { success: false, message: `Expected "Invalid email" but got "${error.message}"` };
    }
  }
};

// Example: Testing domain validation (password length)
export const testPasswordValidation = () => {
  const { input } = profileMockData.testScenarios.shortPassword;

  try {
    const domainService = new ProfileDomainService();
    domainService.validate(input); // Should throw

    console.error("❌ Test failed: Expected validation error");
    return { success: false, message: "Expected validation error" };
  } catch (error: any) {
    if (error.message === "Password must be at least 6 characters") {
      console.log("✅ Test passed: Password validation working");
      return { success: true, message: "Password validation working" };
    } else {
      console.error("❌ Test failed: Wrong validation error");
      return { success: false, message: `Expected password error but got "${error.message}"` };
    }
  }
};

// Example: Running all tests
export const runAllProfileTests = async () => {
  console.log("🧪 Running Profile Mock Tests...");

  // Set faster delay for testing
  ProfileMockService.setDelay(500);

  const results = await Promise.all([
    testSuccessfulProfileCreation(),
    testEmailExistsError(),
    testEmailValidation(),
    testPasswordValidation()
  ]);

  const passed = results.filter(r => r.success).length;
  const total = results.length;

  console.log(`\n📊 Test Results: ${passed}/${total} passed`);

  if (passed === total) {
    console.log("🎉 All tests passed!");
  } else {
    console.log("⚠️ Some tests failed");
  }

  return results;
};

// Example: Manual testing in browser console
export const manualTestExamples = {
  // Copy these to browser console for manual testing
  successTest: `
// Test successful profile creation
import { testSuccessfulProfileCreation } from './src/mocks/profileMocks.example.ts';
testSuccessfulProfileCreation().then(console.log);
`,

  errorTest: `
// Test error scenarios
import { testEmailExistsError, testEmailValidation } from './src/mocks/profileMocks.example.ts';
testEmailExistsError().then(console.log);
testEmailValidation();
`,

  runAllTests: `
// Run all tests
import { runAllProfileTests } from './src/mocks/profileMocks.example.ts';
runAllProfileTests().then(results => console.table(results));
`
};

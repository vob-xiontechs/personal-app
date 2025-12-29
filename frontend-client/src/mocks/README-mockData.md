# Mock Data Documentation

This document explains how to use the mock data system for testing the profile creation feature.

## Overview

The mock data system is located in `src/shared/mockData.ts` and provides:
- Fake API responses for testing
- Pre-defined test scenarios
- Utility functions for managing mock data
- Configurable network delays

## Usage

### Basic Testing

The ProfileApiRepository automatically uses mock data by default. Simply use the form as normal and it will simulate API calls.

### Test Scenarios

Use these special email addresses to test different scenarios:

| Email | Expected Behavior |
|-------|------------------|
| `test@error.com` | Triggers "Email already exists" error |
| `test@server.com` | Triggers "Server error occurred" error |
| `test@invalid.com` | Triggers "Invalid data provided" error |
| Any other valid email | Success response |

### Console Logging

Mock API calls are logged to the browser console with emojis:
- 🎭 Mock API Call - Request details
- ✅ Mock API Success - Success confirmation
- ❌ Mock API Error - Error details

### Utility Functions

Import and use these utilities for advanced testing:

```typescript
import { mockUtils, MockProfileApiService, mockProfileData } from '../shared/mockData';

// Reset mock data to initial state
mockUtils.reset();

// Add custom profile for testing
mockUtils.addProfile({
  id: "3",
  name: "Custom User",
  email: "custom@example.com",
  password: "password789"
});

// Clear all profiles
mockUtils.clearProfiles();

// Set custom API delay (in milliseconds)
mockUtils.setDelay(500); // Faster for quick testing
mockUtils.setDelay(3000); // Slower for loading states
```

### Mock Data Structure

```typescript
// Sample profiles
mockProfileData.profiles

// API response definitions
mockProfileData.responses

// Pre-defined test scenarios
mockProfileData.testScenarios
```

### Switching Between Mock and Real API

To use real API calls instead of mock data:

```typescript
// In your dependency injection or factory
const repository = new ProfileApiRepository(httpClient, false); // false = use real API
```

## Test Scenarios

### Success Case
- **Input**: Any valid data with non-special email
- **Expected**: Success message after 1.5s delay

### Error Cases
- **Domain Validation**: Invalid email format or short password
- **API Errors**: Special email addresses trigger different error types

### Loading States
- Form shows loading spinner during API call
- Submit button disabled during request
- Success/error messages displayed after completion

## Adding New Test Cases

1. Add new response to `mockProfileData.responses`
2. Add new scenario to `mockProfileData.testScenarios`
3. Update the condition in `MockProfileApiService.createProfile()`

## Best Practices

1. **Use descriptive email addresses** for different error scenarios
2. **Check browser console** for API call logs during testing
3. **Test both success and error paths** thoroughly
4. **Use mockUtils** for complex test setups
5. **Keep mock data realistic** to match real API responses

## File Structure

```
src/shared/
├── mockData.ts           # Main mock data and services
├── README-mockData.md    # This documentation
└── ...
```

## Related Files

- `src/infrastructure/http/ProfileApiRepository.ts` - Uses mock service
- `src/domain/profile/services/ProfileDomainService.ts` - Domain validation
- `src/presentation/profile/ProfileContainer.tsx` - Form state management

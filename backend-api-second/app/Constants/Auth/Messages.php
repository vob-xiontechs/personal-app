<?php

namespace App\Constants\Auth;

/**
 * Authentication Response Messages Constants
 */
class Messages
{
    // Success Messages
    public const USER_REGISTERED_SUCCESSFULLY = 'User registered successfully';
    public const LOGIN_SUCCESSFUL = 'Login successful';
    public const LOGOUT_SUCCESSFUL = 'Logout successful';
    public const TOKEN_REFRESHED_SUCCESSFULLY = 'Token refreshed successfully';

    // Error Messages
    public const VALIDATION_FAILED = 'Validation failed';
    public const EMAIL_ALREADY_EXISTS = 'Email already exists.';
    public const INVALID_LOGIN_CREDENTIALS = 'Invalid email or password.';
    public const LOGIN_FAILED_GENERIC = 'Login failed. Please try again.';
    public const REGISTRATION_FAILED = 'Registration failed. Please try again.';
    public const LOGIN_FAILED = 'Login failed. Please try again.';
    public const LOGOUT_FAILED = 'Logout failed';
    public const TOKEN_REFRESH_FAILED = 'Token refresh failed.';
    public const TOKEN_REFRESH_FAILED_GENERIC = 'Token refresh failed.';
}

<?php

namespace App\Providers;

use Tymon\JWTAuth\Contracts\Providers\Auth;
use Illuminate\Contracts\Auth\UserProvider;
use App\Models\User;

class CustomJWTAuthProvider implements Auth
{
    /**
     * The user provider instance.
     */
    protected UserProvider $userProvider;

    /**
     * Create a new custom JWT auth provider.
     */
    public function __construct(UserProvider $userProvider)
    {
        $this->userProvider = $userProvider;
    }

    /**
     * Check a user's credentials.
     */
    public function check($credentials = null): bool
    {
        return !is_null($this->userProvider->retrieveByCredentials($credentials));
    }

    /**
     * Get a user by credentials.
     */
    public function byCredentials($credentials = null)
    {
        return $this->userProvider->retrieveByCredentials($credentials);
    }

    /**
     * Get the currently authenticated user.
     */
    public function user()
    {
        // This method is typically not used in JWT context
        // Return null as it's not applicable for stateless JWT
        return null;
    }

    /**
     * Attempt to authenticate a user.
     */
    public function attempt($credentials = null)
    {
        $user = $this->userProvider->retrieveByCredentials($credentials);

        if ($user && $this->userProvider->validateCredentials($user, $credentials)) {
            return $user;
        }

        return false;
    }

    /**
     * Find a user by ID.
     */
    public function byId($id)
    {
        return $this->userProvider->retrieveById($id);
    }

    /**
     * Find a user by token.
     */
    public function byToken($token, $identifier = null)
    {
        if (!$identifier) {
            return null;
        }

        return $this->userProvider->retrieveByToken($identifier, $token);
    }

    /**
     * Update a user's remember me token.
     */
    public function updateRememberToken($user, $token): void
    {
        $this->userProvider->updateRememberToken($user, $token);
    }

    /**
     * Set the user provider.
     */
    public function setProvider(UserProvider $userProvider): void
    {
        $this->userProvider = $userProvider;
    }

    /**
     * Get the user provider.
     */
    public function getProvider(): UserProvider
    {
        return $this->userProvider;
    }
}

<?php

namespace App\Providers;

use Illuminate\Contracts\Auth\Authenticatable;
use Illuminate\Contracts\Auth\UserProvider;
use App\Models\User;

class CustomJWTUserProvider implements UserProvider
{
    /**
     * The MongoDB user model.
     */
    protected User $model;

    /**
     * Create a new custom JWT user provider.
     */
    public function __construct(User $model)
    {
        $this->model = $model;
    }

    /**
     * Retrieve a user by their unique identifier.
     * Prioritizes user_id over MongoDB _id for better query performance.
     */
    public function retrieveById($identifier): ?Authenticatable
    {
        // Try to find by user_id first (custom ID field)
        $user = $this->model->where('user_id', $identifier)->first();

        // If not found by user_id, try MongoDB _id
        if (!$user) {
            $user = $this->model->find($identifier);
        }

        return $user;
    }

    /**
     * Retrieve a user by their unique identifier and "remember me" token.
     */
    public function retrieveByToken($identifier, $token): ?Authenticatable
    {
        $user = $this->retrieveById($identifier);

        return $user && $user->getRememberToken() === $token ? $user : null;
    }

    /**
     * Update the "remember me" token for the given user in storage.
     */
    public function updateRememberToken(Authenticatable $user, $token): void
    {
        $user->setRememberToken($token);
        $user->save();
    }

    /**
     * Retrieve a user by the given credentials.
     */
    public function retrieveByCredentials(array $credentials): ?Authenticatable
    {
        if (empty($credentials) || !isset($credentials['email'])) {
            return null;
        }

        return $this->model->where('email', $credentials['email'])->first();
    }

    /**
     * Validate a user against the given credentials.
     */
    public function validateCredentials(Authenticatable $user, array $credentials): bool
    {
        return isset($credentials['password']) &&
               password_verify($credentials['password'], $user->getAuthPassword());
    }

    /**
     * Rehash the user's password if required and supported.
     */
    public function rehashPasswordIfRequired(Authenticatable $user, array $credentials, bool $force = false): void
    {
        // MongoDB implementation doesn't require password rehashing for now
    }
}

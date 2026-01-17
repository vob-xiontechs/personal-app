<?php

namespace App\Repositories\Auth;

use App\Models\User;
use Illuminate\Contracts\Pagination\LengthAwarePaginator;
use Illuminate\Support\Facades\Log;

class UserRepository implements UserRepositoryInterface
{
    public function __construct(
        private User $model
    ) {}

    /**
     * Find user by email
     */
    public function findByEmail(string $email): ?User
    {
        try {
            return $this->model->where('email', $email)->first();
        } catch (\Exception $e) {
            Log::error('Error finding user by email', [
                'email' => $email,
                'error' => $e->getMessage()
            ]);
            return null;
        }
    }

    /**
     * Find user by ID
     */
    public function findById(string $id): ?User
    {
        try {
            return $this->model->find($id);
        } catch (\Exception $e) {
            Log::error('Error finding user by ID', [
                'user_id' => $id,
                'error' => $e->getMessage()
            ]);
            return null;
        }
    }

    /**
     * Create a new user
     */
    public function create(array $data): User
    {
        try {
            $user = $this->model->create($data);
            Log::info('User created successfully', ['user_id' => $user->_id]);
            return $user;
        } catch (\Exception $e) {
            Log::error('Error creating user', [
                'data' => $data,
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }

    /**
     * Update user
     */
    public function update(string $id, array $data): bool
    {
        try {
            $updated = $this->model->where('_id', $id)->update($data);
            if ($updated) {
                Log::info('User updated successfully', ['user_id' => $id]);
            }
            return $updated > 0;
        } catch (\Exception $e) {
            Log::error('Error updating user', [
                'user_id' => $id,
                'data' => $data,
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }

    /**
     * Delete user
     */
    public function delete(string $id): bool
    {
        try {
            $deleted = $this->model->where('_id', $id)->delete();
            if ($deleted) {
                Log::info('User deleted successfully', ['user_id' => $id]);
            }
            return $deleted > 0;
        } catch (\Exception $e) {
            Log::error('Error deleting user', [
                'user_id' => $id,
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }

    /**
     * Get all users with pagination
     */
    public function getAllPaginated(int $perPage = 15): LengthAwarePaginator
    {
        try {
            return $this->model->paginate($perPage);
        } catch (\Exception $e) {
            Log::error('Error getting paginated users', [
                'per_page' => $perPage,
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }
}

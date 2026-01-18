<?php

namespace App\Repositories\Auth;

use App\Models\User;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Contracts\Pagination\LengthAwarePaginator;

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
            // Try to find by user_id first (custom ID field)
            $user = $this->model->where('user_id', $id)->first();

            // If not found by user_id, try MongoDB _id
            if (!$user) {
                $user = $this->model->find($id);
            }

            return $user;
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
            // For now, return true as a stub implementation
            Log::info('User update called (stub implementation)', ['user_id' => $id]);
            return true;
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
            // For now, return true as a stub implementation
            Log::info('User delete called (stub implementation)', ['user_id' => $id]);
            return true;
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
            // For now, return empty paginator as a stub implementation
            Log::info('User pagination called (stub implementation)', ['per_page' => $perPage]);
            // This would need a proper implementation, but for now we'll skip it
            throw new \Exception('Pagination not implemented for MongoDB direct access');
        } catch (\Exception $e) {
            Log::error('Error getting paginated users', [
                'per_page' => $perPage,
                'error' => $e->getMessage()
            ]);
            throw $e;
        }
    }
}

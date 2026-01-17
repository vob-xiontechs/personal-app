<?php

namespace App\Repositories\Auth;

use App\Models\TblUserSd;

interface UserRepositoryInterface
{
    /**
     * Find user by email
     */
    public function findByEmail(string $email): ?TblUserSd;

    /**
     * Find user by ID
     */
    public function findById(string $id): ?TblUserSd;

    /**
     * Create a new user
     */
    public function create(array $data): TblUserSd;

    /**
     * Update user
     */
    public function update(string $id, array $data): bool;

    /**
     * Delete user
     */
    public function delete(string $id): bool;

    /**
     * Get all users with pagination
     */
    public function getAllPaginated(int $perPage = 15): \Illuminate\Contracts\Pagination\LengthAwarePaginator;
}

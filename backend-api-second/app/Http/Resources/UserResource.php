<?php

namespace App\Http\Resources;

use App\Utils\MongoDateUtil;
use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class UserResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->user_id ?? $this->_id, // Use user_id if available, fallback to _id
            'user_id' => $this->user_id, // Include both for backward compatibility
            'name' => $this->name,
            'email' => $this->email,
            'email_verified_at' => $this->email_verified_at,
            'created_at' => MongoDateUtil::formatUTCDateTime($this->created_at),
            'updated_at' => MongoDateUtil::formatUTCDateTime($this->updated_at),
        ];
    }

    /**
     * Get additional data that should be returned with the resource array.
     */
    public function with(Request $request): array
    {
        return [
            'meta' => [
                'api_version' => '1.0',
                'resource_type' => 'user',
            ],
        ];
    }
}

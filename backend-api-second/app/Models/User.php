<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use MongoDB\Laravel\Eloquent\Model as Eloquent;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Tymon\JWTAuth\Contracts\JWTSubject;

/**
 * @OA\Schema(
 *     schema="User",
 *     title="User",
 *     description="User model",
 *     @OA\Property(property="_id", type="string", description="MongoDB Object ID", example="507f1f77bcf86cd799439011"),
 *     @OA\Property(property="user_id", type="string", description="Custom User ID", example="A47F97DE86", maxLength=10),
 *     @OA\Property(property="name", type="string", description="User name", example="John Doe"),
 *     @OA\Property(property="email", type="string", format="email", description="User email", example="john@example.com"),
 *     @OA\Property(property="email_verified_at", type="string", format="date-time", description="Email verification timestamp", nullable=true),
 *     @OA\Property(property="created_at", type="string", format="date-time", description="Creation timestamp"),
 *     @OA\Property(property="updated_at", type="string", format="date-time", description="Update timestamp")
 * )
 */
class User extends Eloquent implements \Illuminate\Contracts\Auth\Authenticatable, JWTSubject
{
    use HasFactory, Notifiable;

    /**
     * The table associated with the model.
     *
     * @var string
     */
    protected $table = 'tbl_users_sd';

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'user_id',
        'name',
        'email',
        'password',
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var array<int, string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }

    /**
     * Get the identifier that will be stored in the subject claim of the JWT.
     * Prioritize user_id over MongoDB _id for better query performance.
     */
    public function getJWTIdentifier()
    {
        return $this->user_id ?? $this->_id ?? $this->getKey();
    }

    /**
     * Return a key value array, containing any custom claims to be added to the JWT.
     */
    public function getJWTCustomClaims()
    {
        return [];
    }

    /**
     * Get the name of the unique identifier for the user.
     */
    public function getAuthIdentifierName()
    {
        return '_id';
    }

    /**
     * Get the unique identifier for the user.
     */
    public function getAuthIdentifier()
    {
        return $this->_id;
    }

    /**
     * Get the password for the user.
     */
    public function getAuthPassword()
    {
        return $this->password;
    }

    /**
     * Get the token value for the "remember me" session.
     */
    public function getRememberToken()
    {
        return $this->remember_token;
    }

    /**
     * Set the token value for the "remember me" session.
     */
    public function setRememberToken($value)
    {
        $this->remember_token = $value;
    }

    /**
     * Get the column name for the "remember me" token.
     */
    public function getRememberTokenName()
    {
        return 'remember_token';
    }

    /**
     * Get the password column name for authentication.
     */
    public function getAuthPasswordName()
    {
        return 'password';
    }

    /**
     * Get the primary key for the model.
     */
    public function getKey()
    {
        return $this->_id;
    }

    /**
     * Dynamically retrieve attributes on the model.
     */
    public function __get($key)
    {
        return $this->attributes[$key] ?? null;
    }

    /**
     * Dynamically set attributes on the model.
     */
    public function __set($key, $value)
    {
        $this->attributes[$key] = $value;
    }

    /**
     * Determine if the given attribute exists.
     */
    public function __isset($key)
    {
        return isset($this->attributes[$key]);
    }

    /**
     * Unset an attribute on the model.
     */
    public function __unset($key)
    {
        unset($this->attributes[$key]);
    }

    /**
     * Convert the model instance to an array, handling MongoDB BSON types.
     */
    public function toArray(): array
    {
        $array = parent::toArray();

        // Convert MongoDB BSON types to PHP types
        foreach ($array as $key => $value) {
            if ($value instanceof \MongoDB\BSON\UTCDateTime) {
                $array[$key] = $value->toDateTime()->format('Y-m-d H:i:s');
            } elseif ($value instanceof \MongoDB\BSON\ObjectId) {
                $array[$key] = (string) $value;
            }
        }

        return $array;
    }

    /**
     * Convert the model to JSON, handling MongoDB BSON types.
     */
    public function toJson($options = 0)
    {
        return json_encode($this->toArray(), $options);
    }
}

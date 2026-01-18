<?php

namespace App\Utils;

/**
 * Utility class for generating custom IDs and identifiers
 */
class IdGeneratorUtil
{
    /**
     * Generate a custom user ID: uppercase letters + numbers, max 10 chars
     *
     * @return string
     */
    public static function generateUserId(): string
    {
        return strtoupper(substr(md5(uniqid(mt_rand(), true)), 0, 10));
    }

    /**
     * Generate a custom ID with prefix
     *
     * @param string $prefix
     * @param int $length
     * @return string
     */
    public static function generateWithPrefix(string $prefix, int $length = 10): string
    {
        $randomPart = strtoupper(substr(md5(uniqid(mt_rand(), true)), 0, $length - strlen($prefix)));
        return $prefix . $randomPart;
    }

    /**
     * Generate a UUID-like identifier
     *
     * @return string
     */
    public static function generateUUID(): string
    {
        return sprintf(
            '%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
            mt_rand(0, 0xffff),
            mt_rand(0, 0xffff),
            mt_rand(0, 0xffff),
            mt_rand(0, 0x0fff) | 0x4000,
            mt_rand(0, 0x3fff) | 0x8000,
            mt_rand(0, 0xffff),
            mt_rand(0, 0xffff),
            mt_rand(0, 0xffff)
        );
    }

    /**
     * Generate a timestamp-based ID
     *
     * @param string $prefix
     * @return string
     */
    public static function generateTimestampId(string $prefix = ''): string
    {
        return $prefix . time() . rand(1000, 9999);
    }

    /**
     * Generate a secure random string
     *
     * @param int $length
     * @return string
     */
    public static function generateSecureRandom(int $length = 10): string
    {
        return strtoupper(bin2hex(random_bytes($length / 2)));
    }

    /**
     * Validate ID format
     *
     * @param string $id
     * @param string $type 'user_id', 'uuid', etc.
     * @return bool
     */
    public static function validateId(string $id, string $type = 'user_id'): bool
    {
        switch ($type) {
            case 'user_id':
                return strlen($id) <= 10 && ctype_upper($id) && ctype_alnum($id);
            case 'uuid':
                return preg_match('/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i', $id);
            default:
                return !empty($id);
        }
    }
}

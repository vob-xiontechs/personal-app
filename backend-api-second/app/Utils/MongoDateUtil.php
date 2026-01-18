<?php

namespace App\Utils;

/**
 * Utility class for handling MongoDB BSON date conversions
 */
class MongoDateUtil
{
    /**
     * Convert MongoDB UTCDateTime to formatted date string
     *
     * @param mixed $dateTime
     * @param string $format
     * @return string|null
     */
    public static function formatUTCDateTime($dateTime, string $format = 'Y-m-d H:i:s'): ?string
    {
        if ($dateTime instanceof \MongoDB\BSON\UTCDateTime) {
            return $dateTime->toDateTime()->format($format);
        }

        return $dateTime;
    }

    /**
     * Convert MongoDB UTCDateTime to ISO 8601 string
     *
     * @param mixed $dateTime
     * @return string|null
     */
    public static function toISOString($dateTime): ?string
    {
        if ($dateTime instanceof \MongoDB\BSON\UTCDateTime) {
            return $dateTime->toDateTime()->format('c'); // ISO 8601 format
        }

        return $dateTime;
    }

    /**
     * Check if value is a MongoDB UTCDateTime
     *
     * @param mixed $value
     * @return bool
     */
    public static function isUTCDateTime($value): bool
    {
        return $value instanceof \MongoDB\BSON\UTCDateTime;
    }

    /**
     * Convert multiple date fields in an array
     *
     * @param array $data
     * @param array $dateFields
     * @param string $format
     * @return array
     */
    public static function formatMultipleDates(array $data, array $dateFields = ['created_at', 'updated_at'], string $format = 'Y-m-d H:i:s'): array
    {
        foreach ($dateFields as $field) {
            if (isset($data[$field])) {
                $data[$field] = self::formatUTCDateTime($data[$field], $format);
            }
        }

        return $data;
    }
}

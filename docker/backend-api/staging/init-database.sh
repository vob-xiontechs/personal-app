#!/bin/sh

echo "=== DATABASE INITIALIZATION START ==="
echo "Checking for existing backup files..."

# List backup directory
ls -la /backup/

# Check if backup directory exists and has files
if [ -d /backup ] && [ "$(ls -A /backup/backup_staging_*.sql 2>/dev/null)" ]; then
    echo "Found SQL files, getting latest backup..."

    # List backup files
    ls -la /backup/backup_staging_*.sql

    # Find the latest backup file
    LATEST_BACKUP=$(ls -t /backup/backup_staging_*.sql 2>/dev/null | head -1)

    if [ -n "$LATEST_BACKUP" ] && [ -f "$LATEST_BACKUP" ]; then
        echo "Starting database restoration..."
        echo "Backup file: $LATEST_BACKUP"

        # Restore the database
        mysql -h db -u staging_user staging_db < "$LATEST_BACKUP"

        if [ $? -eq 0 ]; then
            echo "MySQL restore command succeeded"

            # Check if data was inserted
            USER_COUNT=$(mysql -h db -u staging_user -pstaging_pass staging_db -e "SELECT COUNT(*) as user_count FROM tbl_users;" 2>/dev/null | tail -1)

            echo "Database restored successfully from backup"
            echo "User count: $USER_COUNT"
        else
            echo "ERROR: MySQL restore command failed with exit code $?"
            exit 1
        fi
    else
        echo "ERROR: Latest backup file does not exist or is empty"
        exit 1
    fi
else
    echo "WARNING: No backup directory or backup files found, database remains empty"
fi

echo "=== DATABASE INITIALIZATION END ==="

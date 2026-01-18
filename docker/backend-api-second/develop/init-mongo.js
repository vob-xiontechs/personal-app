// MongoDB initialization script for authentication
db = db.getSiblingDB('admin');

// Create root user with full access including readWrite for application database
db.createUser({
  user: 'admin',
  pwd: 'mypassword123',
  roles: [
    {
      role: 'root',
      db: 'admin'
    },
    {
      role: 'readWrite',
      db: 'backend_db_second'
    }
  ]
});

// Switch to the application database and create a test collection
db = db.getSiblingDB('backend_db_second');
db.createCollection('test_collection');
db.test_collection.insertOne({message: 'Database initialized successfully', timestamp: new Date()});

print('MongoDB authentication setup completed successfully');

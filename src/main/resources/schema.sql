
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL CHECK (role IN ('vehicle_owner', 'job_owner', 'cloud_controller')),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicles (
    vin VARCHAR(17) PRIMARY KEY,
    owner_id SERIAL NOT NULL,
    model VARCHAR(100),
    make VARCHAR(100),
    year INTEGER,
    arrival_time TIMESTAMP,
    departure_time TIMESTAMP,
    priority INTEGER DEFAULT 0,
    FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS jobs (
    job_id VARCHAR(50) PRIMARY KEY,
    job_name VARCHAR(255),
    job_owner_id SERIAL,
    duration VARCHAR(50),
    deadline VARCHAR(50),
    status VARCHAR(50),
    FOREIGN KEY (job_owner_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS compensations (
    compensation_id SERIAL PRIMARY KEY,
    vehicle_vin VARCHAR(17) NOT NULL,
    job_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    compensation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vehicle_vin) REFERENCES vehicles(vin),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id SERIAL NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS allocations (
    allocation_id SERIAL PRIMARY KEY,
    user_id SERIAL NOT NULL,
    job_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id)
);

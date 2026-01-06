-- Schema fuer PostgreSQL
-- Wird von DatabaseCreateTables verwendet, sobald die JDBC-URL nicht "h2" enthaelt.

CREATE TABLE IF NOT EXISTS mt_user (
                                       id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_mt_user_email ON mt_user(email);
CREATE UNIQUE INDEX IF NOT EXISTS uk_mt_user_name ON mt_user(name);

CREATE TABLE IF NOT EXISTS mt_patient (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    date_of_birth DATE,
    allergies TEXT,
    chronic_conditions TEXT
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_mt_patient_user_id ON mt_patient(user_id);

CREATE TABLE IF NOT EXISTS mt_medical_staff (
                                                id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    specialization VARCHAR(255)
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_mt_medical_staff_user_id ON mt_medical_staff(user_id);

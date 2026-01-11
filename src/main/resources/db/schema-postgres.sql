-- PostgreSQL schema for MediTrack (aligned with current JPA entities + JDBC tables)
--
-- Note: The current code uses String IDs (UUID strings), so we store IDs as VARCHAR(36).

CREATE TABLE IF NOT EXISTS mt_user (
  id VARCHAR(36) PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL,
  password_hash TEXT NOT NULL,
  role TEXT NOT NULL DEFAULT 'PATIENT'
);

CREATE TABLE IF NOT EXISTS mt_patient (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL REFERENCES mt_user(id),
  display_name TEXT NOT NULL,
  first_name TEXT NULL,
  last_name TEXT NULL
);

CREATE TABLE IF NOT EXISTS mt_medical_staff (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL REFERENCES mt_user(id),
  display_name TEXT NOT NULL,
  staff_role TEXT NOT NULL DEFAULT 'NURSE'
);

CREATE TABLE IF NOT EXISTS mt_assignment (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL REFERENCES mt_patient(id),
  staff_id VARCHAR(36) NOT NULL REFERENCES mt_medical_staff(id),
  assignment_role TEXT NOT NULL DEFAULT 'SUPPORT',
  status TEXT NOT NULL DEFAULT 'ACTIVE',
  assigned_at TIMESTAMP NOT NULL,
  assigned_by_user_id VARCHAR(36) NOT NULL REFERENCES mt_user(id),
  ended_at TIMESTAMP NULL,
  ended_by_user_id VARCHAR(36) NULL REFERENCES mt_user(id)
);

CREATE INDEX IF NOT EXISTS idx_assignment_patient_status ON mt_assignment (patient_id, status);
CREATE INDEX IF NOT EXISTS idx_assignment_staff_status ON mt_assignment (staff_id, status);

CREATE TABLE IF NOT EXISTS mt_vital_reading (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL REFERENCES mt_patient(id),
  vital_type TEXT NOT NULL,
  value_numeric DOUBLE PRECISION NOT NULL,
  unit TEXT NOT NULL,
  measured_at TIMESTAMP NOT NULL,
  recorded_by_staff_id VARCHAR(36) NULL REFERENCES mt_medical_staff(id)
);

CREATE INDEX IF NOT EXISTS idx_vital_patient_measured ON mt_vital_reading (patient_id, measured_at);

CREATE TABLE IF NOT EXISTS mt_alert (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL REFERENCES mt_patient(id),
  vital_reading_id VARCHAR(36) NULL REFERENCES mt_vital_reading(id),
  severity TEXT NOT NULL,
  message TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMP NOT NULL,
  resolved_at TIMESTAMP NULL
);

CREATE INDEX IF NOT EXISTS idx_alert_patient_status ON mt_alert (patient_id, status);

CREATE TABLE IF NOT EXISTS mt_medication (
  id VARCHAR(36) PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_medication_name ON mt_medication (name);

CREATE TABLE IF NOT EXISTS mt_medication_plan (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL REFERENCES mt_patient(id),
  plan_name TEXT NOT NULL,
  created_by_user_id VARCHAR(36) NOT NULL REFERENCES mt_user(id),
  notes TEXT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_plan_patient ON mt_medication_plan (patient_id);

CREATE TABLE IF NOT EXISTS mt_medication_plan_item (
  id VARCHAR(36) PRIMARY KEY,
  plan_id VARCHAR(36) NOT NULL REFERENCES mt_medication_plan(id) ON DELETE CASCADE,
  medication_name TEXT NOT NULL,
  dosage TEXT NOT NULL,
  frequency TEXT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NULL,
  notes TEXT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_plan_item_plan ON mt_medication_plan_item (plan_id);

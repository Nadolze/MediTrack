-- H2 schema for MediTrack (aligned with current JPA entities + JDBC tables)
--
-- IDs are stored as VARCHAR(36) UUID strings.

CREATE TABLE IF NOT EXISTS mt_user (
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'PATIENT'
);

CREATE TABLE IF NOT EXISTS mt_patient (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES mt_user(id)
);

CREATE TABLE IF NOT EXISTS mt_medical_staff (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  staff_role VARCHAR(30) NOT NULL,
  CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES mt_user(id)
);

CREATE TABLE IF NOT EXISTS mt_assignment (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL,
  staff_id VARCHAR(36) NOT NULL,
  assignment_role VARCHAR(50) NOT NULL,
  status VARCHAR(30) NOT NULL,
  assigned_at TIMESTAMP NOT NULL,
  assigned_by_user_id VARCHAR(36) NOT NULL,
  ended_at TIMESTAMP,
  ended_by_user_id VARCHAR(36),
  CONSTRAINT fk_assignment_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_assignment_staff FOREIGN KEY (staff_id) REFERENCES mt_medical_staff(id),
  CONSTRAINT fk_assignment_assigned_by FOREIGN KEY (assigned_by_user_id) REFERENCES mt_user(id),
  CONSTRAINT fk_assignment_ended_by FOREIGN KEY (ended_by_user_id) REFERENCES mt_user(id)
);

CREATE TABLE IF NOT EXISTS mt_vital_reading (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL,
  vital_type VARCHAR(50) NOT NULL,
  value_numeric DOUBLE NOT NULL,
  unit VARCHAR(20) NOT NULL,
  measured_at TIMESTAMP NOT NULL,
  recorded_by_staff_id VARCHAR(36),
  CONSTRAINT fk_vital_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_vital_recorded_by FOREIGN KEY (recorded_by_staff_id) REFERENCES mt_medical_staff(id)
);

CREATE TABLE IF NOT EXISTS mt_alert (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL,
  vital_reading_id VARCHAR(36),
  severity VARCHAR(20) NOT NULL,
  message VARCHAR(500) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  resolved_at TIMESTAMP,
  CONSTRAINT fk_alert_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_alert_vital FOREIGN KEY (vital_reading_id) REFERENCES mt_vital_reading(id)
);

CREATE TABLE IF NOT EXISTS mt_medication (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(2000),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  created_by_user_id VARCHAR(36),
  CONSTRAINT fk_med_created_by FOREIGN KEY (created_by_user_id) REFERENCES mt_user(id)
);

CREATE TABLE IF NOT EXISTS mt_medication_plan (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL,
  plan_name VARCHAR(200) NOT NULL,
  created_by_user_id VARCHAR(36) NOT NULL,
  notes VARCHAR(2000),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT fk_plan_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_plan_created_by FOREIGN KEY (created_by_user_id) REFERENCES mt_user(id)
);

CREATE TABLE IF NOT EXISTS mt_medication_plan_item (
  id VARCHAR(36) PRIMARY KEY,
  plan_id VARCHAR(36) NOT NULL,
  medication_name VARCHAR(200) NOT NULL,
  dosage VARCHAR(100),
  frequency VARCHAR(100),
  start_date DATE,
  end_date DATE,
  notes VARCHAR(2000),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT fk_plan_item_plan FOREIGN KEY (plan_id) REFERENCES mt_medication_plan(id) ON DELETE CASCADE
);

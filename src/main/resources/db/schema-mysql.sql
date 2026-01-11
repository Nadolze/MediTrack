-- MySQL schema for MediTrack (aligned with current JPA entities + JDBC tables)

-- Note:
-- * IDs are stored as CHAR(36) UUID strings.
-- * We keep some additional nullable patient fields (first/last name) to stay compatible with older dumps,
--   but the app primarily uses mt_user + mt_patient/mt_medical_staff relationships.

CREATE TABLE IF NOT EXISTS mt_user (
  id CHAR(36) PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'PATIENT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_patient (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  -- optional (legacy compatibility)
  first_name VARCHAR(50) NULL,
  last_name VARCHAR(50) NULL,
  CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES mt_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_medical_staff (
  id CHAR(36) PRIMARY KEY,
  user_id CHAR(36) NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  staff_role VARCHAR(30) NOT NULL DEFAULT 'NURSE',
  CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES mt_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_assignment (
  id CHAR(36) PRIMARY KEY,
  patient_id CHAR(36) NOT NULL,
  staff_id CHAR(36) NOT NULL,
  assignment_role VARCHAR(50) NOT NULL DEFAULT 'SUPPORT',
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  assigned_at DATETIME NOT NULL,
  assigned_by_user_id CHAR(36) NOT NULL,
  ended_at DATETIME NULL,
  ended_by_user_id CHAR(36) NULL,
  CONSTRAINT fk_assignment_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_assignment_staff FOREIGN KEY (staff_id) REFERENCES mt_medical_staff(id),
  CONSTRAINT fk_assignment_assigned_by FOREIGN KEY (assigned_by_user_id) REFERENCES mt_user(id),
  CONSTRAINT fk_assignment_ended_by FOREIGN KEY (ended_by_user_id) REFERENCES mt_user(id),
  INDEX idx_assignment_patient_status (patient_id, status),
  INDEX idx_assignment_staff_status (staff_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_vital_reading (
  id CHAR(36) PRIMARY KEY,
  patient_id CHAR(36) NOT NULL,
  vital_type VARCHAR(50) NOT NULL,
  value_numeric DOUBLE NOT NULL,
  unit VARCHAR(20) NOT NULL,
  measured_at DATETIME NOT NULL,
  recorded_by_staff_id CHAR(36) NULL,
  CONSTRAINT fk_vital_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_vital_recorded_by FOREIGN KEY (recorded_by_staff_id) REFERENCES mt_medical_staff(id),
  INDEX idx_vital_patient_measured (patient_id, measured_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_alert (
  id CHAR(36) PRIMARY KEY,
  patient_id CHAR(36) NOT NULL,
  vital_reading_id CHAR(36) NULL,
  severity VARCHAR(20) NOT NULL,
  message TEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  created_at DATETIME NOT NULL,
  resolved_at DATETIME NULL,
  CONSTRAINT fk_alert_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_alert_vital FOREIGN KEY (vital_reading_id) REFERENCES mt_vital_reading(id),
  INDEX idx_alert_patient_status (patient_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_medication (
  id CHAR(36) PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description TEXT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  INDEX idx_medication_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_medication_plan (
  id CHAR(36) PRIMARY KEY,
  patient_id CHAR(36) NOT NULL,
  plan_name VARCHAR(200) NOT NULL,
  created_by_user_id CHAR(36) NOT NULL,
  notes TEXT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT fk_plan_patient FOREIGN KEY (patient_id) REFERENCES mt_patient(id),
  CONSTRAINT fk_plan_created_by FOREIGN KEY (created_by_user_id) REFERENCES mt_user(id),
  INDEX idx_plan_patient (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mt_medication_plan_item (
  id CHAR(36) PRIMARY KEY,
  plan_id CHAR(36) NOT NULL,
  medication_name VARCHAR(200) NOT NULL,
  dosage VARCHAR(100) NOT NULL,
  frequency VARCHAR(100) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NULL,
  notes TEXT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT fk_plan_item_plan FOREIGN KEY (plan_id) REFERENCES mt_medication_plan(id) ON DELETE CASCADE,
  INDEX idx_plan_item_plan (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

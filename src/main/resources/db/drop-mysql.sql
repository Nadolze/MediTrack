-- Drop all MediTrack tables (MySQL)
--
-- Use this to fully reset the schema during development.

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS mt_alert;
DROP TABLE IF EXISTS mt_vital_reading;

DROP TABLE IF EXISTS mt_medication_plan_item;
DROP TABLE IF EXISTS mt_medication_plan;
DROP TABLE IF EXISTS mt_medication;

DROP TABLE IF EXISTS mt_assignment;
DROP TABLE IF EXISTS mt_medical_staff;
DROP TABLE IF EXISTS mt_patient;
DROP TABLE IF EXISTS mt_user;

SET FOREIGN_KEY_CHECKS = 1;

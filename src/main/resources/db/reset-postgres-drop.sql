-- Legacy: Full reset by drop+recreate is now handled by DatabaseCreateTables.
-- Kept for manual use.

DROP TABLE IF EXISTS mt_alert;
DROP TABLE IF EXISTS mt_vital_reading;

DROP TABLE IF EXISTS mt_medication_plan_item;
DROP TABLE IF EXISTS mt_medication_plan;
DROP TABLE IF EXISTS mt_medication;

DROP TABLE IF EXISTS mt_assignment;
DROP TABLE IF EXISTS mt_medical_staff;
DROP TABLE IF EXISTS mt_patient;
DROP TABLE IF EXISTS mt_user;

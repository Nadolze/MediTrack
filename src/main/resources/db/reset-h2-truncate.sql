-- Legacy: Use DatabaseCreateTables reset endpoint if enabled.
-- Manual TRUNCATE helpers for H2.

TRUNCATE TABLE mt_alert;
TRUNCATE TABLE mt_vital_reading;

TRUNCATE TABLE mt_medication_plan_item;
TRUNCATE TABLE mt_medication_plan;
TRUNCATE TABLE mt_medication;

TRUNCATE TABLE mt_assignment;
TRUNCATE TABLE mt_medical_staff;
TRUNCATE TABLE mt_patient;
TRUNCATE TABLE mt_user;

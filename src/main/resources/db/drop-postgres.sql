-- Drop-Skript fuer PostgreSQL
-- Wird von DatabaseCreateTables bei `meditrack.db.init.drop-and-recreate=true` ausgefuehrt.

DROP TABLE IF EXISTS mt_medical_staff CASCADE;
DROP TABLE IF EXISTS mt_patient CASCADE;
DROP TABLE IF EXISTS mt_user CASCADE;

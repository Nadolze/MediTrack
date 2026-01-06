-- Drop-Skript fuer H2 (In-Memory / lokale Entwicklung)
-- Wird von DatabaseCreateTables bei `meditrack.db.init.drop-and-recreate=true` ausgefuehrt.

DROP TABLE IF EXISTS mt_medical_staff;
DROP TABLE IF EXISTS mt_patient;
DROP TABLE IF EXISTS mt_user;

-- Optional: explizit Indizes loeschen (H2 entfernt diese i.d.R. mit der Tabelle)
DROP INDEX IF EXISTS uk_mt_staff_user;
DROP INDEX IF EXISTS uk_mt_patient_user;
DROP INDEX IF EXISTS uk_mt_user_email;

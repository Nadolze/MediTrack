-- Reset-Skript fuer PostgreSQL: Daten leeren, Tabellenstruktur behalten.
-- RESTART IDENTITY ist hier unkritisch (wir nutzen UUID-Strings), bleibt aber konsistent.

TRUNCATE TABLE mt_medical_staff, mt_patient, mt_user RESTART IDENTITY;

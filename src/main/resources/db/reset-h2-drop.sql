-- Reset-Skript fuer H2 ("DROP"-Modus): Daten loeschen ohne die Tabellen zu entfernen.
-- Hintergrund: Ein echtes DROP wuerde die Applikation danach nicht mehr funktionieren lassen.

SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM mt_medical_staff;
DELETE FROM mt_patient;
DELETE FROM mt_user;

SET REFERENTIAL_INTEGRITY TRUE;

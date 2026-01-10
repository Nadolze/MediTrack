-- Reset-Skript fuer H2: Daten leeren, Tabellenstruktur behalten.
-- Hinweis: In H2 kann TRUNCATE bei Constraints/Referenzen scheitern.
-- Wir deaktivieren daher kurz die referentielle Integritaet.

SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE mt_medical_staff;
TRUNCATE TABLE mt_patient;
TRUNCATE TABLE mt_user;

SET REFERENTIAL_INTEGRITY TRUE;

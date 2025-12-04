# 🧩 Bounded Contexts & Domänenmodell – MediTrack

Dieses Dokument beschreibt die fachliche Aufteilung von **MediTrack** in Bounded Contexts (BCs) sowie die wichtigsten Domänen-Entitäten und ihre Zuordnung.

Die technischen Ordnernamen der BCs in `src/main/java/com/meditrack` sind:

- `user`         → Benutzerverwaltung  
- `coredata`     → Stammdatenerfassung  
- `assignment`   → Zuweisung medizinisches Personal  
- `history`      → Krankheitshistorie  
- `vitals`       → Erfassung Werte (Vitaldaten)  
- `alerts`       → Alarmsystem / Notification Center  

---

## 🗺 Übersicht der Bounded Contexts

| Bounded Context (fachlich)              | Technischer Name | Verantwortung                                                                 | Beziehung zu anderen BCs                                       |
| --------------------------------------- | ---------------- | ---------------------------------------------------------------------------- | ---------------------------------------------------------------- |
| Benutzerverwaltung                      | `user`           | Patienten und medizinisches Personal können sich registrieren und anmelden. | Voraussetzung für alle weiteren Bounded Contexts.               |
| Stammdatenerfassung                     | `coredata`       | Patienten und med. Personal können persönliche Daten angeben und verwalten. | Benutzer muss sich registriert/angemeldet haben.                |
| Zuweisung medizinisches Personal        | `assignment`     | Med. Personal kann sich Patienten und Fachgebieten zuweisen.               | Benötigt Daten des Patienten (Stammdatenerfassung).             |
| Krankheitshistorie                      | `history`        | Patient und med. Personal können auf Krankheitsverläufe zugreifen und diese erweitern. | Med. Personal muss dem Patienten zugewiesen sein.               |
| Erfassung Werte (Vitaldaten)            | `vitals`         | Patienten-Vitalwerte werden erfasst, gespeichert und regelmäßig überprüft.  | Patient benötigt zugewiesenes med. Personal.                    |
| Alarmsystem / Notification Center       | `alerts`         | Erkennt kritische Vitalwerte und benachrichtigt das zuständige Personal und den Patienten. | Patient-Vitalwerte müssen erfasst werden.                       |

---

## 🧱 Entitäten je Bounded Context

Im Folgenden sind die wichtigsten **Domänen-Entitäten** (und einige typische Value Objects) pro Bounded Context aufgeführt.  
Die Entitäten liegen jeweils im Ordner `domain/entity`, die Value Objects in `domain/valueobject`.

---

### 1️⃣ Benutzerverwaltung (`user`)

**Verantwortung:**  
Registrierung, Anmeldung und Verwaltung der Benutzerkonten (Patienten und medizinisches Personal).

**Wichtige Entitäten:**

- `User`  
  Repräsentiert einen Benutzer des Systems (unabhängig von seiner Rolle).  
  Felder: `UserId`, `username`, `email`, `passwordHash`, `roles`, `status` …

- `Role` (optional als eigene Entität oder Value Object)  
  Beschreibt die Rolle eines Benutzers, z. B. `PATIENT`, `DOCTOR`, `NURSE`, `ADMIN`.

**Typische Value Objects:**

- `UserId` – eindeutige Kennung eines Benutzers  
- `EmailAddress` – validierte E-Mail-Adresse  
- `HashedPassword` – passwortsicherer Hash  
- `UserStatus` – z. B. `ACTIVE`, `LOCKED`, `PENDING`

---

### 2️⃣ Stammdatenerfassung (`coredata`)

**Verantwortung:**  
Verwaltung der Stammdaten von Patienten und medizinischem Personal.

**Wichtige Entitäten:**

- `Patient`  
  Stammdaten eines Patienten.  
  Felder: `PatientId`, `personalData`, `contactData`, `insuranceData`, `primaryPhysicianId` …

- `MedicalStaff`  
  Stammdaten von medizinischem Personal (Ärzt:innen, Pflegekräfte etc.).  
  Felder: `StaffId`, `personalData`, `contactData`, `specialization`, `department` …

**Typische Value Objects:**

- `PatientId`, `StaffId`  
- `PersonalData` (Name, Geburtsdatum, Geschlecht …)  
- `Address`  
- `PhoneNumber`  
- `InsuranceData`  

Diese Daten werden von anderen BCs (z. B. `assignment`, `history`, `vitals`) referenziert – meist über IDs, nicht als direkte Objekt-Referenzen.

---

### 3️⃣ Zuweisung medizinisches Personal (`assignment`)

**Verantwortung:**  
Abbilden, welches medizinische Personal für welche Patienten zuständig ist.

**Wichtige Entitäten:**

- `Assignment`  
  Verknüpft `PatientId` und `StaffId` plus Informationen zur Rolle / Zuständigkeit.  
  Felder: `AssignmentId`, `patientId`, `staffId`, `role`, `validFrom`, `validTo`, `status` …

- (optional) `CareTeam`  
  Gruppe von `MedicalStaff`, die gemeinsam für einen Patienten zuständig sind.

**Typische Value Objects:**

- `AssignmentId`  
- `AssignmentRole` (z. B. `PRIMARY_PHYSICIAN`, `NURSE`, `THERAPIST`)  
- `AssignmentStatus`  

Dieser BC ist wichtig für Berechtigungen: Nur zugewiesenes Personal darf z. B. Historien einsehen oder Vitalwerte bearbeiten.

---

### 4️⃣ Krankheitshistorie (`history`)

**Verantwortung:**  
Dokumentation der Krankheitsverläufe eines Patienten: Diagnosen, Notizen, Behandlungen.

**Wichtige Entitäten:**

- `HistoryEntry`  
  Allgemeiner Eintrag in die Krankheitsgeschichte (diagnostisch, therapeutisch oder administrativ).  
  Felder: `HistoryEntryId`, `patientId`, `authorStaffId`, `type`, `timestamp`, `content` …

- `Diagnosis`  
  Spezialisierter Eintrag für Diagnosen (ICD-Code, Beschreibung, Schweregrad …).

- `MedicalNote`  
  Freitext-Notizen des medizinischen Personals (z. B. Verlauf, Beobachtungen).

Je nach Detailgrad kann `Diagnosis` und `MedicalNote` auch lediglich als **Typ** von `HistoryEntry` modelliert werden.

**Typische Value Objects:**

- `HistoryEntryId`  
- `DiagnosisCode` (z. B. ICD-10)  
- `NoteType`, `HistoryEntryType`  

---

### 5️⃣ Erfassung Werte – Vitaldaten (`vitals`)

**Verantwortung:**  
Erfassen, Speichern und Bewerten von Vitalwerten eines Patienten (Blutdruck, Puls, Temperatur, etc.).

**Wichtige Entitäten:**

- `VitalReading`  
  Ein einzelner Vitalwert eines Patienten zu einem bestimmten Zeitpunkt.  
  Felder: `VitalReadingId`, `patientId`, `type`, `value`, `unit`, `measuredAt`, `recordedByStaffId` …

- (optional) `VitalType`  
  Definition eines Mess-Typs (z. B. Blutdruck systolisch/diastolisch, Puls, Temperatur).

**Typische Value Objects:**

- `VitalReadingId`  
- `MeasurementValue` – der numerische Messwert + Validierung  
- `Unit` – Einheit des Messwerts (z. B. `mmHg`, `bpm`, `°C`)  
- `Threshold` – Grenzwerte (min/max) für die Beurteilung, ob ein Wert kritisch ist

**Domain Events:**

- `VitalReadingCreatedEvent` – wird ausgelöst, wenn ein neuer Vitalwert erfasst wurde.  
  → wird z. B. vom `alerts`-BC konsumiert.

---

### 6️⃣ Alarmsystem / Notification Center (`alerts`)

**Verantwortung:**  
Überwachung der Vitalwerte und Auslösen von Alarmen, wenn Grenzwerte überschritten werden. Benachrichtigung von Patient und zuständigem Personal.

**Wichtige Entitäten:**

- `Alert`  
  Repräsentiert einen ausgelösten Alarm.  
  Felder: `AlertId`, `patientId`, `vitalReadingId`, `severity`, `message`, `createdAt`, `status` …

- (optional) `AlertRule`  
  Beschreibt die Regeln, ab wann ein Alarm ausgelöst werden soll (z. B. Grenzwerte, Kombinationen von Vitalwerten, Dauer).

- (optional) `Notification`  
  Konkrete Benachrichtigung, die versendet wurde (an wen, wann, über welchen Kanal).

**Typische Value Objects:**

- `AlertId`  
- `Severity` (z. B. `INFO`, `WARNING`, `CRITICAL`)  
- `NotificationChannel` (z. B. `EMAIL`, `SMS`, `APP_PUSH`)  

**Event-Integration:**

- Konsumiert `VitalReadingCreatedEvent` aus dem `vitals`-BC  
- Erzeugt ggf. eigene Events wie `AlertTriggeredEvent`

---

## 🔁 Beziehungen zwischen den Bounded Contexts (kurz)

- `user`  
  Basis für Authentifizierung und Rollen – alle anderen BCs setzen vorhandene Benutzer voraus.

- `coredata`  
  Liefert Stammdaten; andere BCs referenzieren Patienten und Personal über IDs.

- `assignment`  
  Steuert, welches Personal auf welche Patienten-Daten zugreifen darf.

- `history`  
  Nutzt `coredata` (Patient/Personal) und `assignment` (Berechtigung).

- `vitals`  
  Erfasst Messwerte für Patienten; verwendet `assignment` für Berechtigungen.

- `alerts`  
  Reagiert auf `vitals`-Events und informiert die in `assignment` hinterlegten Personen.

---

## 💡 Hinweise für die Implementierung

- **Ordnerzuordnung**  
  Jede genannte Entität liegt in ihrem jeweiligen BC unter `domain/entity`.  
  Value Objects liegen unter `domain/valueobject`.

- **Teststruktur**  
  Unter `src/test/java/com/meditrack/...` wird dieselbe BC-Struktur gespiegelt, damit Tests klar zugeordnet sind.

- **Kommentare im Code**  
  Kommentare im Code sollten auf Deutsch sein, damit alle Teammitglieder (und ggf. Dozenten) sie gut verstehen.

---

Bei Bedarf kann dieses Dokument um UML-Diagramme, Sequenzdiagramme oder eine Context Map erweitert werden.

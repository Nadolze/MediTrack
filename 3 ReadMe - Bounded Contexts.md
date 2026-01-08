# 🧩 Bounded Contexts & Domänenmodell – MediTrack

Dieses Dokument beschreibt die fachliche Aufteilung von **MediTrack** in Bounded Contexts (BCs) sowie die wichtigsten Domänen-Entitäten und ihre Zuordnung.

Die technischen Ordnernamen der BCs in `src/main/java/com/meditrack` sind:

- `user`         → Benutzerverwaltung
- `coredata`     → Stammdatenerfassung
- `assignment`   → Zuweisung medizinisches Personal
- `history`      → Krankheitshistorie
- `vitals`       → Erfassung Werte (Vitaldaten)
- `alerts`       → Alarmsystem / Notification Center
- `medication`   → Medikationsmanagement

---

## 🗺 Übersicht der Bounded Contexts

| Bounded Context (fachlich) | Technischer Name | Verantwortung | Beziehung zu anderen BCs |
| --- | --- | --- | --- |
| Benutzerverwaltung | `user` | Registrierung, Anmeldung und Rollenverwaltung. | Voraussetzung für alle weiteren BCs. |
| Stammdatenerfassung | `coredata` | Verwaltung von Patienten- und Personalstammdaten. | Benutzer muss registriert sein. |
| Zuweisung medizinisches Personal | `assignment` | Zuordnung von Personal zu Patienten. | Benötigt Patienten-Stammdaten. |
| Krankheitshistorie | `history` | Dokumentation von Diagnosen und Verläufen. | Personal muss zugewiesen sein. |
| Vitaldatenerfassung | `vitals` | Erfassen und Bewerten von Vitalwerten. | Nutzt Zuweisungen und Patientendaten. |
| Alarmsystem | `alerts` | Erkennung kritischer Werte und Benachrichtigung. | Konsumiert Events aus `vitals`. |
| Medikationsmanagement | `medication` | Verwaltung von Medikationsplänen und Einnahmen. | Nutzt `user`, optional `assignment` & `coredata`. |

---

## 🧱 Entitäten je Bounded Context

### 1️⃣ Benutzerverwaltung (`user`)
**Entitäten:** `User`, `Role`  
**Value Objects:** `UserId`, `EmailAddress`, `HashedPassword`, `UserStatus`

---

### 2️⃣ Stammdatenerfassung (`coredata`)
**Entitäten:** `Patient`, `MedicalStaff`  
**Value Objects:** `PatientId`, `StaffId`, `PersonalData`, `Address`, `InsuranceData`

---

### 3️⃣ Zuweisung medizinisches Personal (`assignment`)
**Entitäten:** `Assignment`  
**Value Objects:** `AssignmentId`, `AssignmentRole`, `AssignmentStatus`

---

### 4️⃣ Krankheitshistorie (`history`)
**Entitäten:** `HistoryEntry`, `Diagnosis`, `MedicalNote`  
**Value Objects:** `HistoryEntryId`, `DiagnosisCode`, `HistoryEntryType`

---

### 5️⃣ Vitaldaten (`vitals`)
**Entitäten:** `VitalReading`  
**Value Objects:** `VitalReadingId`, `MeasurementValue`, `Unit`, `Threshold`  
**Events:** `VitalReadingCreatedEvent`

---

### 6️⃣ Alarmsystem (`alerts`)
**Entitäten:** `Alert`, `Notification`  
**Value Objects:** `AlertId`, `Severity`, `NotificationChannel`

---

### 7️⃣ Medikationsmanagement (`medication`)
**Verantwortung:**  
Erstellung, Anzeige und Pflege von Medikationsplänen eines Patienten.

**Entitäten:**
- `MedicationPlan`
- `MedicationPlanItem`
- `Medication`

**Value Objects:**
- `PlanId`, `ItemId`, `MedicationId`
- `Dose`, `Frequency`, `TimeOfDay`, `PlanStatus`

**Berechtigung:**
- PATIENT: darf eigene Pläne lesen
- STAFF/ADMIN: darf Pläne für Patienten anlegen

**Technischer Hinweis:**  
Aktuell wird `patientId = userId` verwendet. Beim Start wird ein minimaler `mt_patient`-Datensatz automatisch synchronisiert, um FK-Konsistenz sicherzustellen.

---

## 🔁 Beziehungen (Kurzfassung)

- `user` ist Basis für alle BCs
- `coredata` liefert Stammdaten
- `assignment` regelt Zugriffe
- `vitals` erzeugt Events
- `alerts` reagiert auf Events
- `medication` verwaltet Pläne pro Patient

---

## 💡 Hinweise

- BC-Struktur wird auch in Tests gespiegelt
- Kommentare im Code auf Deutsch
- Dokument erweiterbar um UML / Context Maps

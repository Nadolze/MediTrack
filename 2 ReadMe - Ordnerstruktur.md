
---

# 📚 Bedeutung der Unterordner (Wofür sind sie da?)

| Unterordner     | Bedeutung                                   | Wann benötigt?                             |
| --------------- | --------------------------------------------- | ------------------------------------------ |
| **command**     | Befehl, der einen UseCase startet            | Wenn ein BC *Zustand ändert*               |
| **query**       | Lesende Anfrage                              | Wenn ein BC Abfragen/Reporting erlaubt     |
| **handler**     | Event-Verarbeitung                           | Wenn ein BC *auf Ereignisse reagiert*      |
| **dto**         | Datenobjekte für Transport                   | Fast immer                                 |
| **service**     | Anwendungslogik (UseCases)                   | Immer bei BC mit Logik                     |
| **entity**      | Domain-Entitäten                             | Immer                                       |
| **valueobject** | Fachwerte (immutable, validiert)             | Wenn sinnvoll (z. B. Email, Threshold)     |
| **events**      | Domain-Events                                | Wenn Ereignisse auftreten                  |
| **repository**  | Domain-Schnittstellen für Datenzugriff       | Immer im Domain-Layer                      |

---

# 🧩 Warum diese Struktur?

- **Fachlich getrennt** statt technisch gemischt  
- Skalierbar für große Teams  
- Perfekt geeignet für **Spring Boot + JPA + MySQL**  
- Unterstützt **Test-Driven Development (TDD)**  
- Saubere, wartbare Architektur  
- Vorbereitung auf mögliche Microservices

# 📦 Projektstruktur – MediTrack

```
MediTrack/
├── .github/
│   └── workflows/
│       └── maven-tests.yml
│
├── .gitignore
├── .idea/
├── Jenkinsfile
├── README.md
│
├── Uebungen/
│   ├── README.md
│   ├── Uebung1/
│   │   └── 1-Uebung.pdf
│   ├── Uebung2/
│   │   └── 2-Uebung.pdf
│   ├── Uebung3/
│   │   └── 3-Uebung.pdf
│   ├── Uebung4/
│   │   └── 4-Uebung.pdf
│   ├── Uebung5/
│   │   └── 5-Uebung.pdf
│   ├── src/
│   │   └── images/
│   └── 3-Uebung-MediTrack.pdf
│
├── pom.xml
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── meditrack/
│   │   │           ├── alerts/
│   │   │           │   ├── api/
│   │   │           │   ├── application/
│   │   │           │   │   ├── dto/
│   │   │           │   │   ├── handler/
│   │   │           │   │   └── service/
│   │   │           │   ├── domain/
│   │   │           │   │   ├── entity/
│   │   │           │   │   ├── events/
│   │   │           │   │   ├── repository/
│   │   │           │   │   ├── service/
│   │   │           │   │   └── valueobject/
│   │   │           │   └── infrastructure/
│   │   │           │       ├── adapter/
│   │   │           │       ├── notifications/
│   │   │           │       └── persistence/
│   │   │           │
│   │   │           ├── assignment/
│   │   │           │   ├── api/
│   │   │           │   ├── application/
│   │   │           │   │   ├── command/
│   │   │           │   │   ├── dto/
│   │   │           │   │   └── service/
│   │   │           │   ├── domain/
│   │   │           │   │   ├── entity/
│   │   │           │   │   ├── events/
│   │   │           │   │   ├── repository/
│   │   │           │   │   └── service/
│   │   │           │   └── infrastructure/
│   │   │           │       ├── adapter/
│   │   │           │       └── persistence/
│   │   │           │
│   │   │           ├── coredata/
│   │   │           │   ├── api/
│   │   │           │   ├── application/
│   │   │           │   │   ├── command/
│   │   │           │   │   ├── dto/
│   │   │           │   │   └── service/
│   │   │           │   ├── domain/
│   │   │           │   │   ├── entity/
│   │   │           │   │   ├── repository/
│   │   │           │   │   ├── service/
│   │   │           │   │   └── valueobject/
│   │   │           │   └── infrastructure/
│   │   │           │       ├── config/
│   │   │           │       └── persistence/
│   │   │           │
│   │   │           ├── history/
│   │   │           │   ├── api/
│   │   │           │   ├── application/
│   │   │           │   │   ├── command/
│   │   │           │   │   ├── dto/
│   │   │           │   │   ├── query/
│   │   │           │   │   └── service/
│   │   │           │   ├── domain/
│   │   │           │   │   ├── entity/
│   │   │           │   │   ├── events/
│   │   │           │   │   ├── repository/
│   │   │           │   │   └── service/
│   │   │           │   └── infrastructure/
│   │   │           │       ├── adapter/
│   │   │           │       ├── persistence/
│   │   │           │       └── projections/
│   │   │           │
│   │   │           ├── shared/
│   │   │           │   ├── exception/
│   │   │           │   └── valueobject/
│   │   │           │
│   │   │           ├── user/
│   │   │           │   ├── api/
│   │   │           │   ├── application/
│   │   │           │   │   ├── command/
│   │   │           │   │   ├── dto/
│   │   │           │   │   └── service/
│   │   │           │   ├── domain/
│   │   │           │   │   ├── entity/
│   │   │           │   │   ├── repository/
│   │   │           │   │   ├── service/
│   │   │           │   │   └── valueobject/
│   │   │           │   └── infrastructure/
│   │   │           │       ├── config/
│   │   │           │       └── persistence/
│   │   │           │
│   │   │           └── vitals/
│   │   │               ├── api/
│   │   │               ├── application/
│   │   │               │   ├── command/
│   │   │               │   ├── dto/
│   │   │               │   └── service/
│   │   │               ├── domain/
│   │   │               │   ├── entity/
│   │   │               │   ├── events/
│   │   │               │   ├── repository/
│   │   │               │   ├── service/
│   │   │               │   └── valueobject/
│   │   │               └── infrastructure/
│   │   │                   ├── persistence/
│   │   │                   └── scheduler/
│   │   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/
            └── meditrack/
                ├── alerts/
                ├── assignment/
                ├── coredata/
                ├── history/
                ├── shared/
                ├── user/
                └── vitals/
```

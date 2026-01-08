ARCHITEKTURÜBERSICHT – MediTrack (User-Bounded Context)

Dieses Dokument beschreibt die Architektur des User-Bounded Context in der MediTrack-Anwendung.
Ziel: Schnell verstehen, welche Schichten es gibt, welche Klassen wohin gehören und wie ein Request vom Browser bis zur Datenbank fließt.

--------------------------------------------------
1. High-Level Überblick
--------------------------------------------------

Wir kombinieren:

- Web / MVC (Spring MVC + Thymeleaf)
- Application Layer (Use-Cases)
- Domain Layer (Domänenmodell)
- Infrastructure Layer (Persistenz via JPA / MySQL bzw. H2 im Test)

Vereinfacht:

Browser (HTML, Formulare)
│
▼
[Web / MVC] UserController
│
▼
[Application] UserApplicationService
│
▼
[Domain] User, UserId, ...
│
▼
[Infrastructure] JpaUserRepository, UserEntityJpa, DB


--------------------------------------------------
2. Schichten im Detail
--------------------------------------------------

2.1 Web / MVC-Schicht
----------------------

Paket: com.meditrack.user.api
Wichtigste Klasse: UserController

Aufgaben:
- HTTP-Requests entgegennehmen (@GetMapping, @PostMapping)
- HTML-Formulare anzeigen (Thymeleaf-Templates)
- Formulardaten in DTOs packen
- Fehler in BindingResult eintragen
- Bei Erfolg: Application-Service aufrufen
- View-Namen als String zurückgeben

Beispiele:
- GET /register → zeigt Registrierungsformular (View: user/register)
- POST /register → validiert Formular, ruft userApplicationService.registerUser(...) auf
    - bei Fehler: user/register
    - bei Erfolg: Redirect auf /login
- GET /login → zeigt Login-Formular
- POST /login → ruft userApplicationService.login(...) auf
    - bei Fehler: user/login
    - bei Erfolg: home bzw. Redirect

Wichtig:
Der Controller kennt nur DTOs und den Application-Service, aber keine JPA-Entities und keine Repositorys direkt.


2.2 Application Layer
---------------------

Paket: com.meditrack.user.application
Wichtigste Klassen:
- UserApplicationService
- DTOs:
    - UserRegistrationDto
    - UserLoginDto

Aufgaben:
- Fachliche Use-Cases orchestrieren (z. B. Registrierung, Login)
- Eingaben aus dem Web-Layer entgegennehmen (DTOs)
- Domain-Objekte anlegen / verwenden
- Infrastruktur über Interfaces (Repositorys) nutzen
- Keine Web-spezifischen Dinge (kein Model, keine Views)

Beispiele:
- registerUser(UserRegistrationDto dto):
    - Passwort hashen (PasswordEncoder)
    - Domain-User erstellen
    - UserEntityJpa bauen
    - JpaUserRepository.save(...) aufrufen

- login(String usernameOrEmail, String password):
    - User per E-Mail suchen, sonst per Name
    - Passwort mit PasswordEncoder.matches(...) prüfen
    - true / false zurückgeben


2.3 Domain Layer
----------------

Paket: com.meditrack.user.domain (z. B. entity, value etc.)

Wichtige Klassen (Beispiele, je nach aktuellem Stand):
- User
- UserId

Aufgaben:
- Domänenlogik kapseln (z. B. Invarianten, Regeln)
- Keine Framework-Abhängigkeiten
- Fachlich sprechende API für den Application-Layer

Die Domain-Schicht sollte nichts von JPA, Spring, HTTP usw. wissen.


2.4 Infrastructure Layer (Persistenz)
-------------------------------------

Paket: com.meditrack.user.infrastructure.persistence

Wichtige Klassen:
- UserEntityJpa
- JpaUserRepository

Aufgaben:
- Mapping der Domain-Welt auf die Datenbank-Welt
- JPA-Entity-Definition (@Entity, @Id, Spalten, Tabellenname, etc.)
- Repository-Interface für Spring Data JPA

Beispiel:

public interface JpaUserRepository extends JpaRepository<UserEntityJpa, String> {

    Optional<UserEntityJpa> findByEmail(String email);

    Optional<UserEntityJpa> findByName(String name);
}

Dies ist das Repository-Pattern:
Der Application-Service arbeitet mit einem Repository-Interface, das wie eine Sammlung von UserEntityJpa wirkt.


--------------------------------------------------
3. Request-Flow: Registrierung
--------------------------------------------------

Beispiel: User registriert sich über /register

Browser (POST /register mit Formularfeldern)
│
▼
UserController.handleRegister(...)
│  - Validiert DTO (z. B. leer, @-Zeichen etc.)
│  - Bei Fehler → View "user/register"
│  - Bei Erfolg → userApplicationService.registerUser(dto)
▼
UserApplicationService.registerUser(dto)
│  - Passwort hashen
│  - Domain-User erstellen (z. B. User, UserId)
│  - UserEntityJpa aus Domain-User bauen
│  - jpaUserRepository.save(entity)
▼
JpaUserRepository.save(entity)
│
▼
Datenbank (INSERT INTO users ...)


--------------------------------------------------
4. Request-Flow: Login
--------------------------------------------------

Beispiel: User loggt sich über /login ein

Browser (POST /login mit usernameOrEmail + password)
│
▼
UserController.handleLogin(...)
│  - Ruft userApplicationService.login(usernameOrEmail, password) auf
▼
UserApplicationService.login(...)
│  - jpaUserRepository.findByEmail(...)
│    falls leer:
│       jpaUserRepository.findByName(...)
│  - Wenn User vorhanden:
│       passwordEncoder.matches(rawPassword, storedHash)
│       → true/false
▼
UserController.handleLogin(...)
│  - Bei false: Model.error setzen, View "user/login"
│  - Bei true : "home" / Redirect


--------------------------------------------------
5. Tests
--------------------------------------------------

5.1 Unit-Tests Application Service
----------------------------------

Klasse: UserApplicationServiceTest

- Tools: JUnit 5, Mockito
- Repository (JpaUserRepository) und PasswordEncoder werden gemockt
- Ziel: Verhalten von UserApplicationService isoliert testen

Beispiele:
- registerUser_shouldSaveUserWithHashedPassword()
- login_shouldReturnTrueWhenUserFoundByEmailAndPasswordMatches()
- login_shouldReturnFalseWhenUserNotFound()
- login_shouldReturnFalseWhenPasswordDoesNotMatch()

Diese Tests benötigen keine echte Datenbank.


5.2 Integrationstest JPA-Repository
-----------------------------------

Klasse: JpaUserRepositoryTest

- Annotation: @DataJpaTest
- Nutzt eine In-Memory-H2-Datenbank (wird von Spring Boot konfiguriert)
- Testet Zusammenspiel von:
    - UserEntityJpa
    - JpaUserRepository
    - Hibernate / JPA-Mapping
    - Datenbank (H2)

Beispiele:
- saveAndFindByEmail_shouldReturnUser()
- saveAndFindByName_shouldReturnUser()


--------------------------------------------------
6. Konfiguration
--------------------------------------------------

6.1 Anwendung (Runtime)
-----------------------

Datei: src/main/resources/application.properties

- MySQL-URL
- User / Passwort
- JPA-Settings (DDL, Dialekt, etc.)


6.2 Tests
---------

Datei: src/test/resources/application-test.properties

- eigene DB-Konfiguration für Tests (falls benötigt)
- oft reicht bei @DataJpaTest die Auto-Konfiguration von H2


--------------------------------------------------
7. Zusammenfassung
--------------------------------------------------

- MVC-Controller und Repository-Pattern existieren parallel, aber in unterschiedlichen Schichten:
    - UserController → Web / MVC
    - UserApplicationService → Application Layer
    - Domain-Klassen → Domain Layer
    - JpaUserRepository, UserEntityJpa → Infrastructure Layer (Repository-Pattern)

- Unit-Tests testen fachliches Verhalten ohne echte DB.
- Integrationstests testen JPA + DB mit H2.

Dieses Dokument soll dir helfen, den Aufbau später schnell wieder zu verstehen und neue Features
(z. B. Profile, Rollen, Passwort-Reset) sauber in die passende Schicht einzuhängen.

ToDo: Erstmal nur Randnotizen. Muss auf alle BCs und dann kürzer beschrieben werden.
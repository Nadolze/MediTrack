# 🧭 HTML- / Template-Struktur in MediTrack

Dieses Dokument beschreibt die aktuelle Struktur der HTML-/Thymeleaf-Templates und die dazugehörigen Routen in MediTrack.

---

## 1. Ordnerstruktur der Templates nach BCs

```
src/  
  main/  
    resources/  
      templates/  
        landing.html          # Öffentliche Landing-Page (/)  
        home.html             # Einfache Startseite / Dashboard (/home)  
        user/  
          login.html          # Login-Formular (/login)  
          register.html       # Registrierungsformular (/register)
```

- **`landing.html`**  
  Einstieg in die Anwendung, Links zu Login und Registrierung.

- **`home.html`**  
  Einfache Übersicht / Dummy-Dashboard nach Login (noch ohne echte Security / Rollenlogik).

- **`user/login.html`**  
  Login-Formular (Benutzername oder E-Mail + Passwort).

- **`user/register.html`**  
  Formular zur Registrierung eines neuen Benutzers (Benutzername, E-Mail, Passwort).

---

## 2. Routen & Adressen (mit Server-Platzhalter)

Im Folgenden wird `<BASE_URL>` als Platzhalter für die Basis-Adresse des Servers verwendet, z. B.:

- lokal: `http://localhost:9090`  
- produktiv oder Testsystem: z. B. `https://meditrack.example.com`

| Funktion               | URL                   | Template           |
|------------------------|-----------------------|--------------------|
| Landing-Page           | `<BASE_URL>/`         | `landing.html`     |
| Startseite / Home      | `<BASE_URL>/home`     | `home.html`        |
| Login anzeigen         | `<BASE_URL>/login`    | `user/login.html`  |
| Registrierung anzeigen | `<BASE_URL>/register` | `user/register.html` |

> **Hinweis:**  
> Der Port (`9090`) und die Host-Adresse werden über die Spring-Boot-Konfiguration (z. B. `application.properties`) festgelegt und können je nach Umgebung (lokal, Test, Produktion) variieren.

---

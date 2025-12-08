package com.meditrack.shared.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller für allgemeine Seiten wie Landing-Page und Startseite.
 *
 * Dieser Controller ist bewusst im Bounded Context "shared" abgelegt,
 * da die Seiten nicht exklusiv zu einem bestimmten Fachbereich gehören.
 */
@Controller
public class HomeController {

    /**
     * Landing-Page für die Anwendung.
     *
     * Wird aufgerufen, wenn ein Benutzer die Start-URL "/" aufruft.
     * Bietet Links zu Login und Registrierung.
     */
    @GetMapping("/")
    public String showLanding(Model model) {
        model.addAttribute("title", "MediTrack – Digitale Patientenverwaltung");
        return "landing";
    }

    /**
     * Einfache Startseite nach erfolgreichem Login.
     *
     * Achtung:
     * - Derzeit noch ohne echte Authentifizierungsprüfung.
     * - Dient zunächst als Platzhalter für das Dashboard.
     */
    @GetMapping("/home")
    public String showHome(Model model) {
        model.addAttribute("title", "MediTrack – Übersicht");
        return "home";
    }
}

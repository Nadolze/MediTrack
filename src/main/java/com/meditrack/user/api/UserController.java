package com.meditrack.user.api;

import com.meditrack.user.application.dto.UserLoginDto;
import com.meditrack.user.application.dto.UserRegistrationDto;
import com.meditrack.user.application.service.UserApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * MVC-Controller für Login und Registrierung.
 *
 * Dieser Controller liefert Thymeleaf-Templates zurück
 * und verwendet dafür DTOs aus dem Application-Layer.
 */
@Controller
public class UserController {

    /**
     * Application-Service, der die Anwendungsfälle für Benutzer ausführt.
     */
    private final UserApplicationService userApplicationService;

    /**
     * Konstruktor-Injektion durch Spring.
     */
    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    // ---------------- Registrierung ----------------

    /**
     * Zeigt das Registrierungsformular an.
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registration", new UserRegistrationDto());
        return "user/register";
    }

    /**
     * Verarbeitet das Registrierungsformular.
     */
    @PostMapping("/register")
    public String handleRegister(
            @ModelAttribute("registration") UserRegistrationDto form,
            BindingResult bindingResult,
            Model model
    ) {
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            bindingResult.rejectValue("username", "username.empty", "Benutzername darf nicht leer sein.");
        }
        if (form.getEmail() == null || !form.getEmail().contains("@")) {
            bindingResult.rejectValue("email", "email.invalid", "Bitte eine gültige E-Mail-Adresse eingeben.");
        }
        if (form.getPassword() == null || form.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.short", "Passwort muss mindestens 6 Zeichen haben.");
        }

        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        userApplicationService.registerUser(form);
        model.addAttribute("message", "Registrierung erfolgreich. Bitte melde dich jetzt an.");
        return "redirect:/login";
    }

    // ---------------- Login ----------------

    /**
     * Zeigt das Login-Formular an.
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new UserLoginDto());
        return "user/login";
    }

    /**
     * Verarbeitet das Login-Formular.
     */
    @PostMapping("/login")
    public String handleLogin(
            @ModelAttribute("login") UserLoginDto form,
            Model model
    ) {
        boolean success = userApplicationService.login(
                form.getUsernameOrEmail(),
                form.getPassword()
        );

        if (!success) {
            model.addAttribute("error", "Benutzername/E-Mail oder Passwort ist falsch.");
            return "user/login";
        }

        model.addAttribute("message", "Erfolgreich eingeloggt.");
        return "home"; // oder "redirect:/", je nach deiner Startseite
    }
}

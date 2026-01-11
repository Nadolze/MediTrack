package com.meditrack.user.api;

import com.meditrack.shared.api.SessionKeys;
import com.meditrack.user.application.dto.UserLoginDto;
import com.meditrack.user.application.dto.UserRegistrationDto;
import com.meditrack.user.application.service.UserApplicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * MVC-Controller für Login, Registrierung und Logout.
 * Die Start-/Home-Seiten liegen im HomeController.
 */
@Controller
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new UserLoginDto());
        return "user/login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @ModelAttribute("login") UserLoginDto form,
            Model model,
            HttpSession session
    ) {
        return userApplicationService.authenticate(form.getUsernameOrEmail(), form.getPassword())
                .map(sessionUser -> {
                    session.setAttribute(SessionKeys.LOGGED_IN_USER, sessionUser);
                    return "redirect:/home";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Benutzername/E-Mail oder Passwort ist falsch.");
                    return "user/login";
                });
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registration", new UserRegistrationDto());
        return "user/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @ModelAttribute("registration") UserRegistrationDto form,
            BindingResult bindingResult,
            Model model
    ) {
        // Controller-level Checks (UI-Feedback, TDD-freundlich)
        if (form.getUsername() == null || form.getUsername().isBlank()) {
            bindingResult.rejectValue("username", "username.empty", "Benutzername darf nicht leer sein.");
        }
        if (form.getEmail() == null || !form.getEmail().contains("@")) {
            bindingResult.rejectValue("email", "email.invalid", "Bitte eine gültige E-Mail-Adresse eingeben.");
        }
        if (form.getPassword() == null || form.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.short", "Passwort muss mindestens 6 Zeichen haben.");
        }
        if (form.getConfirmPassword() == null || !form.getConfirmPassword().equals(form.getPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwörter stimmen nicht überein.");
        }

        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            userApplicationService.registerUser(form);
            model.addAttribute("message", "Registrierung erfolgreich. Bitte melde dich jetzt an.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "user/register";
        }
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}

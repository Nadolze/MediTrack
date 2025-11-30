package de.meditrack.alteWelt.controller;

import de.meditrack.backend.User;
import de.meditrack.alteWelt.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Thymeleaf Template
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "E-Mail bereits registriert.");
            return "register";
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username bereits vergeben. Bitte wählen Sie einen anderen.");
            return "register";
        }

        if (!isValidPassword(user.getPassword())) {
            model.addAttribute("error", "Passwort muss Groß-/Kleinbuchstaben, Zahl, Sonderzeichen und min. 8 Zeichen enthalten.");
            return "register";
        }

        userRepository.save(user);

        // Erfolgsmeldung
        model.addAttribute("success", "Registrierung erfolgreich! Sie können sich nun einloggen.");
        return "register"; // wir bleiben auf der gleichen Seite und zeigen Erfolg
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Thymeleaf Template
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        var existing = userRepository.findByEmail(email);
        if (existing.isPresent() && existing.get().getPassword().equals(password)) {
            session.setAttribute("loggedUser", existing.get()); // <-- Session speichern
            return "redirect:/home";
        }
        model.addAttribute("error", "Ungültige E-Mail oder Passwort.");
        return "login";
    }


    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";
        return Pattern.matches(regex, password);
    }
}

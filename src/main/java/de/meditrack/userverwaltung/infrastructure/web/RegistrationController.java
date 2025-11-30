package de.meditrack.userverwaltung.infrastructure.web;

import de.meditrack.userverwaltung.api.dto.RegisterUserCommand;
import de.meditrack.userverwaltung.application.RegisterUserService;
import de.meditrack.userverwaltung.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final RegisterUserService registerUserService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        RegisterUserCommand cmd = new RegisterUserCommand();
        cmd.setRole(UserRole.PATIENT); // DEFAULT
        model.addAttribute("user", cmd);
        return "register";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterUserCommand cmd, Model model) {

        try {
            registerUserService.register(cmd);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        // SUCCESS → Loginseite mit Hinweis anzeigen
        return "redirect:/login?registered";
    }

}

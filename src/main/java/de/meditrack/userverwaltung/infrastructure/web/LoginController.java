package de.meditrack.userverwaltung.infrastructure.web;

import de.meditrack.userverwaltung.application.LoginUserService;
import de.meditrack.userverwaltung.api.dto.LoginUserCommand;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final LoginUserService loginUserService;

    public LoginController(LoginUserService loginUserService) {
        this.loginUserService = loginUserService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new LoginUserCommand());
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute("login") LoginUserCommand cmd,
                          HttpSession session,
                          Model model) {

        var user = loginUserService.authenticate(cmd.getUsername(), cmd.getPassword());

        if (user == null) {
            model.addAttribute("error", "Benutzername oder Passwort falsch.");
            return "login";
        }

        // Benutzer in HTTP-Session speichern
        session.setAttribute("user", user);

        return "redirect:/";
    }


}

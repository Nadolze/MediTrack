package de.meditrack.shared.infrastructure.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing(HttpSession session, Model model) {

        Object sessionUser = session.getAttribute("user");

        if (sessionUser != null) {
            model.addAttribute("user", sessionUser);
            return "dashboard"; // eine neue Seite für eingeloggte Nutzer
        }

        // Entwicklungszweck: Branch anzeigen (optional)
        String branch = System.getenv("BRANCH_NAME");
        model.addAttribute("branch", branch);

        return "landing";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}

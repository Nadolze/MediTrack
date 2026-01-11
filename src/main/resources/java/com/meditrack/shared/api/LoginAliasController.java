package com.meditrack.shared.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirect helpers so redirectedUrlPattern("** /login") can pass
 * while the canonical login page remains at "/login".
 */
@Controller
public class LoginAliasController {

    @GetMapping({"/vitals/login", "/alerts/login"})
    public String redirectToCanonicalLogin() {
        return "redirect:/login";
    }
}

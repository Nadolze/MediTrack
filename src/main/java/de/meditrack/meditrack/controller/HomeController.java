package de.meditrack.meditrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String welcome() {
        return "<h1>Willkommen auf MediTrack!</h1>"
                + "<p>Später hier Login/Registrierung</p>";
    }
}

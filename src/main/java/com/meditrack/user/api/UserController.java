package com.meditrack.user.api;

import com.meditrack.user.api.dto.CreateUserRequest;
import com.meditrack.user.api.dto.UserResponse;
import com.meditrack.user.application.service.UserApplicationService;
import com.meditrack.user.domain.entity.User;
import org.springframework.web.bind.annotation.*;

/**
 * REST-Controller für alle Benutzer-Endpunkte.
 *
 * Diese Klasse stellt HTTP-Endpunkte bereit, über die Clients
 * Benutzer erstellen, lesen oder löschen können.
 *
 * Die eigentliche Logik liegt NICHT im Controller,
 * sondern ausschließlich im ApplicationService.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserApplicationService userService;

    public UserController(UserApplicationService userService) {
        this.userService = userService;
    }

    /**
     * POST /users
     *
     * Erstellt einen neuen Benutzer.
     *
     * @param request Daten aus dem HTTP-Request (Name + Email)
     * @return Benutzer-Daten als UserResponse
     */
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request.name, request.email);
        return new UserResponse(user);
    }
}

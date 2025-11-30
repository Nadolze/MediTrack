package de.meditrack.userverwaltung.application;

import de.meditrack.userverwaltung.api.dto.RegisterUserCommand;
import de.meditrack.userverwaltung.domain.model.*;
import de.meditrack.userverwaltung.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserService {

    private final UserRepository userRepository;

    public User register(RegisterUserCommand cmd) {

        if (userRepository.existsByEmail(cmd.getEmail())) {
            throw new IllegalArgumentException("E-Mail bereits vergeben");
        }

        if (userRepository.existsByUsername(cmd.getUsername())) {
            throw new IllegalArgumentException("Benutzername bereits vergeben");
        }

        User user;

        if (cmd.getRole() == UserRole.ARZT) {
            user = new Arzt();
        } else {
            Patient p = new Patient();
            p.setGeburtsdatum(cmd.getGeburtsdatum());
            user = p;
        }

        user.setRole(cmd.getRole());
        user.setEmail(cmd.getEmail());
        user.setUsername(cmd.getUsername());
        user.setVorname(cmd.getVorname());
        user.setNachname(cmd.getNachname());

        // ❗ Passwort im Klartext speichern, ohne Hashing
        user.setPassword(cmd.getPassword());

        return userRepository.save(user);
    }
}

package com.meditrack.user.application.service;

import com.meditrack.shared.valueobject.UserSession;
import com.meditrack.user.application.dto.UserRegistrationDto;
import com.meditrack.user.domain.entity.User;
import com.meditrack.user.domain.valueobject.UserId;
import com.meditrack.user.domain.valueobject.UserRole;
import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application Service für Benutzerfunktionen.
 */
@Service
public class UserApplicationService {

    private final JpaUserRepository jpaUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public UserApplicationService(JpaUserRepository jpaUserRepository,
                                  PasswordEncoder passwordEncoder) {
        this.jpaUserRepository = jpaUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Registrierungsdaten fehlen.");
        }

        String username = dto.getUsername();
        String email = dto.getEmail();
        String password = dto.getPassword();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Benutzername darf nicht leer sein.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Bitte eine gültige E-Mail-Adresse eingeben.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Passwort muss mindestens 6 Zeichen haben.");
        }

        if (jpaUserRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-Mail ist bereits vergeben.");
        }
        if (jpaUserRepository.findByName(username).isPresent()) {
            throw new IllegalArgumentException("Benutzername ist bereits vergeben.");
        }

        User user = new User(UserId.generate(), username, email, UserRole.PATIENT);
        String hash = passwordEncoder.encode(password);

        UserEntityJpa entity = new UserEntityJpa(
                user.getId().getValue(),
                user.getName(),
                user.getEmail(),
                hash,
                user.getRole().name()
        );

        jpaUserRepository.save(entity);

        // WICHTIG: mt_patient ist FK-Target für viele Tabellen (u.a. mt_medication_plan).
        // Wir halten patientId = userId konsistent, indem wir mt_patient.id = userId setzen.
        ensurePatientRowExists(user.getId().getValue(), username);

        return user;
    }

    public boolean login(String usernameOrEmail, String password) {
        return authenticate(usernameOrEmail, password).isPresent();
    }

    public Optional<UserSession> authenticate(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            return Optional.empty();
        }
        if (password == null || password.isBlank()) {
            return Optional.empty();
        }

        Optional<UserEntityJpa> userOpt = jpaUserRepository.findByEmail(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = jpaUserRepository.findByName(usernameOrEmail);
        }
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        UserEntityJpa entity = userOpt.get();

        String storedHash = entity.getPasswordHash();
        if (storedHash == null || storedHash.isBlank()) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, storedHash)) {
            return Optional.empty();
        }

        String role = (entity.getRole() == null || entity.getRole().isBlank())
                ? UserRole.PATIENT.name()
                : entity.getRole();

        return Optional.of(new UserSession(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                role
        ));
    }

    private void ensurePatientRowExists(String userId, String username) {
        if (jdbcTemplate == null || userId == null || userId.isBlank()) {
            return;
        }
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM mt_patient WHERE id = ?",
                    Integer.class,
                    userId
            );
            if (count != null && count > 0) {
                return;
            }

            String firstName = (username == null || username.isBlank()) ? userId : username;

            jdbcTemplate.update(
                    "INSERT INTO mt_patient (id, user_id, first_name, last_name) VALUES (?, ?, ?, ?)",
                    userId,
                    userId,
                    firstName,
                    ""
            );
        } catch (Exception ignored) {
            // bewusst still
        }
    }
}

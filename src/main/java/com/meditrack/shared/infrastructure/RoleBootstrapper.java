package com.meditrack.shared.infrastructure;

import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// Infrastruktur-Komponente zum Bootstrap von Benutzerrollen.
@Component
public class RoleBootstrapper {

    private static final Logger log = LoggerFactory.getLogger(RoleBootstrapper.class);

    private final JpaUserRepository userRepository;
    private final Environment env;

    public RoleBootstrapper(JpaUserRepository userRepository, Environment env) {
        this.userRepository = userRepository;
        this.env = env;
    }

    /**
     * Setzt (falls vorhanden) definierte Bootstrap-User auf ADMIN/STAFF.
     * Wird in Tests direkt aufgerufen; kann in der App z.B. über einen Runner getriggert werden.
     */
    public void run() {
        applyRoleForEmail("meditrack.bootstrap.admin-email", "ADMIN");
        applyRoleForEmail("meditrack.bootstrap.staff-email", "STAFF");
    }

    private void applyRoleForEmail(String propertyKey, String targetRole) {
        String email = env.getProperty(propertyKey);
        if (email == null || email.isBlank()) {
            return;
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            String before = user.getRole() == null ? "" : user.getRole();
            if (targetRole.equalsIgnoreCase(before)) {
                return;
            }

            user.setRole(targetRole);
            userRepository.save(user);

            log.info("✅ RoleBootstrapper: User '{}' wurde auf Rolle {} gesetzt (vorher: {}).", email, targetRole, before);
        });
    }
}

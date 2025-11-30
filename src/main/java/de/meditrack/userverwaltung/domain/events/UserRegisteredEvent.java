package de.meditrack.userverwaltung.domain.events;

import de.meditrack.userverwaltung.domain.model.UserRole;
import lombok.Value;

@Value
public class UserRegisteredEvent {
    Long userId;
    UserRole role;
}

package de.meditrack.userverwaltung.application;

import de.meditrack.userverwaltung.domain.model.User;
import de.meditrack.userverwaltung.domain.model.UserRole;
import de.meditrack.userverwaltung.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterUserServiceTest {

    @Test
    void shouldCreatePatientWhenRoleIsPatient() {
        UserRepository repo = mock(UserRepository.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegisterUserService service = new RegisterUserService(repo, publisher);

        RegisterUserCommand cmd = new RegisterUserCommand();
        cmd.setEmail("patient@example.com");
        cmd.setPassword("secret");
        cmd.setUsername("patientUser");
        cmd.setVorname("Paul");
        cmd.setNachname("Patient");
        cmd.setRole(UserRole.PATIENT);
        cmd.setGeburtsdatum(LocalDate.of(2000, 1, 1));

        User u = service.register(cmd);

        assertEquals(UserRole.PATIENT, u.getRole());
        assertEquals("patientUser", u.getUsername());

        verify(repo, times(1)).save(any());
        verify(publisher, times(1)).publishEvent(any());
    }
}

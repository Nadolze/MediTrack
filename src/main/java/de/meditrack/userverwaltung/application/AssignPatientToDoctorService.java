package de.meditrack.userverwaltung.application;

import de.meditrack.userverwaltung.domain.events.PatientAssignedToDoctorEvent;
import de.meditrack.userverwaltung.domain.model.Arzt;
import de.meditrack.userverwaltung.domain.model.Patient;
import de.meditrack.userverwaltung.domain.model.User;
import de.meditrack.userverwaltung.domain.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignPatientToDoctorService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AssignPatientToDoctorService(UserRepository userRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void assign(Long patientId, Long arztId) {
        User patUser = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient nicht gefunden"));

        User arztUser = userRepository.findById(arztId)
                .orElseThrow(() -> new IllegalArgumentException("Arzt nicht gefunden"));

        if (!(patUser instanceof Patient patient)) {
            throw new IllegalArgumentException("User " + patientId + " ist kein Patient");
        }
        if (!(arztUser instanceof Arzt arzt)) {
            throw new IllegalArgumentException("User " + arztId + " ist kein Arzt");
        }

        patient.assignToDoctor(arzt);
        userRepository.save(patient);

        eventPublisher.publishEvent(
                new PatientAssignedToDoctorEvent(patientId, arztId)
        );
    }
}

package de.meditrack.userverwaltung.domain.events;

import lombok.Value;

@Value
public class PatientAssignedToDoctorEvent {
    Long patientId;
    Long arztId;
}

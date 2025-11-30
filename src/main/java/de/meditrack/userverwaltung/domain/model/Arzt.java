package de.meditrack.userverwaltung.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
public class Arzt extends User {
    public void addPatient(Patient patient) {
        if (patient == null) throw new IllegalArgumentException("Patient darf nicht null sein");
        patient.assignToDoctor(this);
    }

}

package de.meditrack.userverwaltung.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "arzt")
@Getter
@Setter
@NoArgsConstructor
public class Arzt extends User {

    public Arzt(String email,
                String password,
                String username,
                String vorname,
                String nachname)
    {
        super(email, password, username, vorname, nachname, UserRole.ARZT);
    }
}

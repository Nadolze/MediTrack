package de.meditrack.userverwaltung.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor  // wichtig für JPA
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String vorname;

    private String nachname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    // ✔ der Konstruktor, den Patient & Arzt verwenden
    protected User(String email,
                   String password,
                   String username,
                   String vorname,
                   String nachname,
                   UserRole role)
    {
        this.email = email;
        this.password = password;
        this.username = username;
        this.vorname = vorname;
        this.nachname = nachname;
        this.role = role;
    }
}

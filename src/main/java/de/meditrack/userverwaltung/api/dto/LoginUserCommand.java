package de.meditrack.userverwaltung.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserCommand {

    private String username;
    private String password;

    public LoginUserCommand() {
        // leere Form für Thymeleaf
    }

    public LoginUserCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

package com.meditrack.user.application.dto;

/**
 * Simple DTO: Tests erwarten Default-Konstruktor + Getter/Setter.
 */
public class UserLoginDto {

    private String usernameOrEmail;
    private String password;

    public UserLoginDto() {
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

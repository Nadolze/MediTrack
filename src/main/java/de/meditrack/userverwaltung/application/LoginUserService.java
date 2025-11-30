package de.meditrack.userverwaltung.application;

import de.meditrack.userverwaltung.domain.model.User;
import de.meditrack.userverwaltung.domain.repositories.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class LoginUserService {

    private final UserRepository repo;

    public LoginUserService(UserRepository repo) {
        this.repo = repo;
    }
    public User authenticate(String username, String password) {
        return repo.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    public boolean login(String username, String password) {
        return repo.findByUsername(username)
                .map(u -> u.getPassword().equals(password))
                .orElse(false);
    }
}

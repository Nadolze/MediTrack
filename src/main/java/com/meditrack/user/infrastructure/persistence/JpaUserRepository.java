package com.meditrack.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring-Data-JPA-Repository für UserEntityJpa.
 */
public interface JpaUserRepository extends JpaRepository<UserEntityJpa, String> {

    Optional<UserEntityJpa> findByEmail(String email);

    Optional<UserEntityJpa> findByName(String name);
}

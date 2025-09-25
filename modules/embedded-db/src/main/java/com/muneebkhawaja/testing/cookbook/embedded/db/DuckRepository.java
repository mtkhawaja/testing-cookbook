package com.muneebkhawaja.testing.cookbook.embedded.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DuckRepository extends JpaRepository<Duck, Long> {
    Optional<Duck> findDuckBySpecies(final String species);
}

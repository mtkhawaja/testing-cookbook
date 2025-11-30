package com.muneebkhawaja.testing.cookbook.embedded.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("h2-test")
class DuckRepositoryTest {
    @Autowired
    private DuckRepository repository;

    @Test
    @DisplayName("Should find duck by species when duck exists")
    void shouldFindDuckBySpeciesWhenDuckExists() {
        final var duck = new Duck();
        duck.setSpecies("Mallard");
        repository.save(duck);
        assertThat(repository.findDuckBySpecies("Mallard"))
                .isNotNull()
                .isPresent()
                .get()
                .extracting(Duck::getSpecies)
                .isEqualTo("Mallard");
    }
}
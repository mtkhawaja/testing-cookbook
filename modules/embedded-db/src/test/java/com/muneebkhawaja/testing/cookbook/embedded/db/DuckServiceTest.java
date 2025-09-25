package com.muneebkhawaja.testing.cookbook.embedded.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
abstract class DuckServiceTest {
    @Autowired
    private DuckRepository repository;
    @Autowired
    private DuckService service;


    @DisplayName("Should create a duck with specified species When adding a new duck")
    @Test
    void shouldCreateADuckWithSpecifiedSpeciesWhenAddingANewDuck() {
        final var response = service.createDuck("Mallard");
        assertThat(response).isNotNull()
                .extracting(DuckDTO::species)
                .isEqualTo("Mallard");
    }

    @DisplayName("Should find a duck by species When the duck species exists")
    @Test
    void shouldFindADuckBySpeciesWhenTheDuckSpeciesExists() {
        final var duck = new Duck();
        duck.setSpecies("Wood Duck");
        this.repository.save(duck);
        assertThat(service.findDuckBySpecies(duck.getSpecies()))
                .isNotNull()
                .isPresent()
                .get()
                .extracting(DuckDTO::species)
                .isEqualTo(duck.getSpecies());
    }

    @DisplayName("Should throw IllegalArgumentException When species is null or empty")
    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowIllegalArgumentExceptionWhenSpeciesIsNullOrEmpty(final String species) {
        assertThatThrownBy(() -> service.createDuck(species))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Should return empty Optional When species is not found")
    @Test
    void shouldReturnEmptyOptionalWhenSpeciesIsNotFound() {
        assertThat(service.findDuckBySpecies(UUID.randomUUID().toString()))
                .isNotNull()
                .isEmpty();
    }


}

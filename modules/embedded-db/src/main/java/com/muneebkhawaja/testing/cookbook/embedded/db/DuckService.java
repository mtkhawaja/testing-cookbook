package com.muneebkhawaja.testing.cookbook.embedded.db;

import ch.qos.logback.core.util.StringUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DuckService {
    private final DuckRepository repository;
    private final DuckConverter converter;

    @Autowired
    public DuckService(final DuckRepository repository,
                       final DuckConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Transactional
    public DuckDTO createDuck(final String species) {
        if (StringUtil.isNullOrEmpty(species)) {
            throw new IllegalArgumentException("Duck 'species' cannot be null or empty");
        }
        final var duck = new Duck();
        duck.setSpecies(species);
        return this.converter.convert(repository.save(duck));
    }

    public Optional<DuckDTO> findDuckBySpecies(final String species) {
        return repository.findDuckBySpecies(species)
                .map(this.converter::convert);
    }


}

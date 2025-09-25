package com.muneebkhawaja.testing.cookbook.embedded.db;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DuckConverter implements Converter<Duck, DuckDTO> {
    @Override
    public DuckDTO convert(final Duck duck) {
        Objects.requireNonNull(duck, "Duck cannot be null");
        return new DuckDTO(duck.getSpecies());
    }

}

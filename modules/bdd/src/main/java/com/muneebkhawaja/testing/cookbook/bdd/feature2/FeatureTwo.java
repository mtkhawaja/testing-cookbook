package com.muneebkhawaja.testing.cookbook.bdd.feature2;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeatureTwo {

    public long sumEntries(List<ValueEntry> entries) {
        return entries.stream()
                .mapToLong(ValueEntry::value)
                .sum();
    }


}

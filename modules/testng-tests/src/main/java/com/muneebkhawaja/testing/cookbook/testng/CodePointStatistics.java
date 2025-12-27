package com.muneebkhawaja.testing.cookbook.testng;

import java.util.Map;

public record CodePointStatistics(
        long totalCodePoints,
        int numberOfUniqueCodePoints,
        int leastFrequentCodePoint,
        long leastFrequentCodePointFrequency,
        int mostFrequentCodePoint,
        long mostFrequentCodePointFrequency,
        Map<Integer, Long> codePointFrequency
) {
}

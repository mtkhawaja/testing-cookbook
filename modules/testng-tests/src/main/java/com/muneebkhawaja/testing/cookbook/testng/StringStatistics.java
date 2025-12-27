package com.muneebkhawaja.testing.cookbook.testng;


public record StringStatistics(
        String text,
        CodePointStatistics codePointStatistics,
        EncodingStatisticCollection encodingStatisticsCollection,
        long numberOfUserPerceivedCharacters) {
}

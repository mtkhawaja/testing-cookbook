package com.muneebkhawaja.testing.cookbook.testng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

///
/// See [Spring TestNG Support](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/support-classes.html#testcontext-support-classes-testng) for additional information
///
@ContextConfiguration(classes = {TestApplication.class})
@Test(groups = {"unit"})
public class StringStatisticsTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StringStatisticsService service;

    @Test(groups = {"ascii"})
    void shouldComputeTotalCodePointsForAsciiOnlyString() {
        final StringStatistics stats = service.calculate("Mississippi");
        assertThat(stats.codePointStatistics().totalCodePoints()).isEqualTo(11L);
    }

    @Test(groups = {"ascii", "minmax"})
    void shouldChooseDeterministicMostFrequentCodePointWhenAsciiMaxFrequencyTies() {
        final StringStatistics stats = service.calculate("Mississippi");
        final CodePointStatistics cps = stats.codePointStatistics();
        // mississippi => i=4, s=4; tie-breaker chooses the lowest code point => 'i'
        assertThat(cps.mostFrequentCodePoint()).isEqualTo((int) 'i');
        assertThat(cps.mostFrequentCodePointFrequency()).isEqualTo(4L);
    }

    @Test(groups = {"bmp", "minmax"})
    void shouldComputeLeastAndMostFrequentForBmpMixedString() {
        final StringStatistics stats = service.calculate("aß你你你a");
        final CodePointStatistics cps = stats.codePointStatistics();
        assertThat(cps.numberOfUniqueCodePoints()).isEqualTo(3);
        assertThat(cps.leastFrequentCodePoint()).isEqualTo((int) 'ß');
        assertThat(cps.leastFrequentCodePointFrequency()).isEqualTo(1L);
        assertThat(cps.mostFrequentCodePoint()).isEqualTo((int) '你');
        assertThat(cps.mostFrequentCodePointFrequency()).isEqualTo(3L);
        assertThat(cps.codePointFrequency()).isEqualTo(Map.of(
                (int) 'a', 2L,
                (int) 'ß', 1L,
                (int) '你', 3L
        ));
    }

    @Test(groups = {"combining", "perceived"})
    void shouldCountCombiningSequenceAsOnePerceivedCharacter() {
        final StringStatistics stats = service.calculate("e\u0300è");
        assertThat(stats.codePointStatistics().totalCodePoints()).isEqualTo(3L);
        assertThat(stats.numberOfUserPerceivedCharacters()).isEqualTo(2L);
    }

    @Test(groups = {"supplementary", "zwj", "perceived"})
    void shouldComputeTotalsForSupplementaryAndZWJCluster() {
        final StringStatistics stats = service.calculate("😀😀👨‍👩‍👧");
        final CodePointStatistics cps = stats.codePointStatistics();
        assertThat(cps.totalCodePoints()).isEqualTo(7L);
        assertThat(cps.numberOfUniqueCodePoints()).isEqualTo(5);
        assertThat(stats.numberOfUserPerceivedCharacters()).isEqualTo(3L);
    }

    @Test(groups = {"supplementary", "zwj", "minmax"})
    void shouldComputeLeastAndMostFrequentForSupplementaryAndZWJCluster() {
        final CodePointStatistics cps = service.calculate("😀😀👨‍👩‍👧").codePointStatistics();
        // least frequency = 1, tie among 👧(1F467), 👨(1F468), 👩(1F469) => choose lowest => 👧
        assertThat(cps.leastFrequentCodePoint()).isEqualTo(0x1F467);
        assertThat(cps.leastFrequentCodePointFrequency()).isEqualTo(1L);
        // most frequency = 2, tie between ZWJ(200D) and 😀(1F600) => choose lowest => ZWJ
        assertThat(cps.mostFrequentCodePoint()).isEqualTo(0x200D);
        assertThat(cps.mostFrequentCodePointFrequency()).isEqualTo(2L);
    }

    @Test(groups = {"supplementary", "zwj", "encoding"})
    void shouldComputeEncodingSizesForSupplementaryAndZwjsCluster() {
        final StringStatistics stats = service.calculate("😀😀👨‍👩‍👧");
        assertEncoding(stats, StandardCharsets.UTF_8, 26, 26);
        assertEncoding(stats, StandardCharsets.UTF_16BE, 24, 12);
        assertEncoding(stats, StandardCharsets.UTF_32BE, 28, 7);
    }

    @Test(groups = {"regression", "perceived"})
    void shouldComputeKnownTotalsForMixedRealWorldString() {
        final StringStatistics stats = service.calculate("👧abc😀👨‍👩‍👧124😀😀😀1235");
        final CodePointStatistics cps = stats.codePointStatistics();
        assertThat(stats.numberOfUserPerceivedCharacters()).isEqualTo(16L);
        assertThat(cps.totalCodePoints()).isEqualTo(20L);
        assertThat(cps.numberOfUniqueCodePoints()).isEqualTo(13);
    }

    @Test(groups = {"regression", "minmax"})
    void shouldComputeKnownLeastAndMostFrequentForMixedRealWorldString() {
        final CodePointStatistics cps = service.calculate("👧abc😀👨‍👩‍👧124😀😀😀1235").codePointStatistics();
        assertThat(cps.leastFrequentCodePoint()).isEqualTo((int) '3');
        assertThat(cps.leastFrequentCodePointFrequency()).isEqualTo(1L);
        assertThat(cps.mostFrequentCodePoint()).isEqualTo(0x1F600);
        assertThat(cps.mostFrequentCodePointFrequency()).isEqualTo(4L);
    }

    @Test(groups = {"regression", "encoding"})
    void shouldComputeKnownEncodingSizesForMixedRealWorldString() {
        final StringStatistics stats = service.calculate("👧abc😀👨‍👩‍👧124😀😀😀1235");
        assertEncoding(stats, StandardCharsets.UTF_8, 48, 48);
        assertEncoding(stats, StandardCharsets.UTF_16BE, 56, 28);
        assertEncoding(stats, StandardCharsets.UTF_32BE, 80, 20);
    }

    private static void assertEncoding(
            final StringStatistics stats,
            final Charset charset,
            final int expectedBytes,
            final int expectedCodeUnits
    ) {
        final EncodingStatisticCollection esc = stats.encodingStatisticsCollection();

        final Map<Charset, EncodingStatistic> byCharset = esc.statistics().stream()
                .collect(Collectors.toMap(EncodingStatistic::encoding, Function.identity()));

        final EncodingStatistic es = byCharset.get(charset);
        assertThat(es).isNotNull();
        assertThat(es.bytes()).isEqualTo(expectedBytes);
        assertThat(es.codeUnits()).isEqualTo(expectedCodeUnits);
    }
}
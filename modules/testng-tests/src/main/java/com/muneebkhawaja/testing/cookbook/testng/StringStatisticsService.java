package com.muneebkhawaja.testing.cookbook.testng;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StringStatisticsService {
    private static final int UTF_8_CODE_UNIT_BYTES = 1;
    private static final int UTF_16_CODE_UNIT_BYTES = 2;
    private static final int UTF_32_CODE_UNIT_BYTES = 4;

    public StringStatistics calculate(@NonNull final String text) {
        final var encodingStatistics = calculateEncodingStatistics(text);
        final var codePointStatistics = calculateCodePointStatistics(text);
        final int perceivedLength = calculatePerceivedLength(text);
        return new StringStatistics(
                text,
                codePointStatistics,
                encodingStatistics,
                perceivedLength
        );
    }

    private CodePointStatistics calculateCodePointStatistics(@NonNull final String text) {
        final Map<Integer, Long> codePointFrequency = text
                .codePoints()
                .boxed()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
        final int numberOfUniqueCodePoints = codePointFrequency.size();
        long minFrequency = Long.MAX_VALUE;
        long maxFrequency = Long.MIN_VALUE;
        int leastFrequentCodePoint = Integer.MAX_VALUE;
        int mostFrequentCodePoint = Integer.MAX_VALUE;
        long numberOfTotalCodePoints = 0;
        for (final var frequencyEntry : codePointFrequency.entrySet()) {
            final int codePoint = frequencyEntry.getKey();
            final long frequency = frequencyEntry.getValue();
            // For ties, the lowest code point is chosen.
            if (frequency < minFrequency
                || (frequency == minFrequency && codePoint < leastFrequentCodePoint)) {
                minFrequency = frequencyEntry.getValue();
                leastFrequentCodePoint = codePoint;
            }
            if (frequency > maxFrequency
                || (frequency == maxFrequency && codePoint < mostFrequentCodePoint)) {
                maxFrequency = frequency;
                mostFrequentCodePoint = codePoint;
            }
            numberOfTotalCodePoints += frequencyEntry.getValue();
        }
        return new CodePointStatistics(
                numberOfTotalCodePoints,
                numberOfUniqueCodePoints,
                leastFrequentCodePoint,
                minFrequency,
                mostFrequentCodePoint,
                maxFrequency,
                Collections.unmodifiableMap(codePointFrequency)
        );
    }

    private static @NonNull EncodingStatisticCollection calculateEncodingStatistics(final @NonNull String text) {
        final List<EncodingStatistic> encodingStatistics = new ArrayList<>();
        final byte[] utf8Bytes = text.getBytes(StandardCharsets.UTF_8);
        final byte[] utf16Bytes = text.getBytes(StandardCharsets.UTF_16BE);
        final byte[] utf32Bytes = text.getBytes(StandardCharsets.UTF_32BE);
        final int utf8CodeUnits = utf8Bytes.length / UTF_8_CODE_UNIT_BYTES;
        final int utf16CodeUnits = utf16Bytes.length / UTF_16_CODE_UNIT_BYTES;
        final int utf32CodeUnits = utf32Bytes.length / UTF_32_CODE_UNIT_BYTES;
        final double utf8To16ByteSavings = calculateSavings(utf8Bytes, utf16Bytes);
        final double utf8To32ByteSavings = calculateSavings(utf8Bytes, utf32Bytes);
        final double utf16To32ByteSavings = calculateSavings(utf16Bytes, utf32Bytes);
        encodingStatistics.add(new EncodingStatistic(
                StandardCharsets.UTF_8,
                utf8Bytes.length,
                utf8CodeUnits
        ));
        encodingStatistics.add(new EncodingStatistic(
                StandardCharsets.UTF_16BE,
                utf16Bytes.length,
                utf16CodeUnits
        ));
        encodingStatistics.add(new EncodingStatistic(
                StandardCharsets.UTF_32BE,
                utf32Bytes.length,
                utf32CodeUnits
        ));
        return new EncodingStatisticCollection(
                Collections.unmodifiableList(encodingStatistics),
                utf8To16ByteSavings,
                utf8To32ByteSavings,
                utf16To32ByteSavings
        );
    }

    private static double calculateSavings(final byte[] of, final byte[] over) {
        int candidateSize = of.length;
        int comparisonTargetSize = over.length;
        if (comparisonTargetSize == 0 || comparisonTargetSize <= candidateSize) {
            return 0.0;
        }
        return (comparisonTargetSize - candidateSize) * 100.0 / comparisonTargetSize;
    }

    /// For example, consider the sequence 👨‍👩‍👧‍, which is composed of the following
    /// Unicode code points (note the trailing ZERO WIDTH JOINER):
    ///
    /// - 👨 [U+1F468](https://unicodeplus.com/U+1F468)
    ///   = 0xD83D 0xDC68 (UTF-16 surrogate pair)
    /// - ZERO WIDTH JOINER [U+200D](https://unicodeplus.com/U+200D)
    ///   = 0x200D (UTF-16)
    /// - 👩 [U+1F469](https://unicodeplus.com/U+1F469)
    ///   = 0xD83D 0xDC69 (UTF-16 surrogate pair)
    /// - ZERO WIDTH JOINER [U+200D](https://unicodeplus.com/U+200D)
    ///   = 0x200D (UTF-16)
    /// - 👧 [U+1F467](https://unicodeplus.com/U+1F467)
    ///   = 0xD83D 0xDC67 (UTF-16 surrogate pair)
    /// - ZERO WIDTH JOINER [U+200D](https://unicodeplus.com/U+200D)
    ///   = 0x200D (UTF-16)
    ///
    /// Combined sequence:
    /// U+1F468 → U+200D → U+1F469 → U+200D → U+1F467 → U+200D
    ///
    /// This sequence consists of 6 Unicode code points but is rendered as a single
    /// user-perceived character (a grapheme cluster). The final ZERO WIDTH JOINER
    /// is a dangling joiner and does not contribute to the rendered glyph.
    ///
    /// Note: This is an example of a *grapheme cluster* composed of multiple Unicode
    /// code points joined by ZERO WIDTH JOINER (U+200D). See:
    /// https://unicode.org/reports/tr29/#Grapheme_Cluster_Boundaries
    ///
    /// Also consider the following related example:
    /// The lowercase letter e with grave accent ('è') can be represented either as:
    /// - A single code point: [U+00E8](https://unicodeplus.com/U+00E8)
    /// - Or as a decomposed sequence:
    ///   [U+0065](https://unicodeplus.com/U+0065) (LATIN SMALL LETTER E)
    ///   followed by
    ///   [U+0300](https://unicodeplus.com/U+0300) (COMBINING GRAVE ACCENT),
    ///   i.e. the string "e\u0300".
    private static int calculatePerceivedLength(final String text) {
        if (text.isEmpty()) {
            return 0;
        }
        final var iterator = BreakIterator.getCharacterInstance(Locale.ROOT);
        iterator.setText(text);
        int count = 0;
        iterator.first();
        while (iterator.next() != BreakIterator.DONE) {
            count++;
        }
        return count;
    }
}

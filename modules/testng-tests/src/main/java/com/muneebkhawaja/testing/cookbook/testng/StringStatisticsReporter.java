package com.muneebkhawaja.testing.cookbook.testng;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Formatter;

@Component
public class StringStatisticsReporter {

    public @NonNull String generateReport(@NonNull final StringStatistics statistics) {
        final var cps = statistics.codePointStatistics();
        final var enc = statistics.encodingStatisticsCollection();
        final StringBuilder sb = new StringBuilder(1024);
        try (final Formatter fmt = new Formatter(sb)) {
            buildReport(statistics, fmt, cps, enc);
        }
        return sb.toString();
    }

    private static void buildReport(final StringStatistics statistics,
                                    final Formatter fmt, CodePointStatistics cps,
                                    final EncodingStatisticCollection enc) {
        fmt.format("String Statistics Report for '%s'%n", statistics.text());
        fmt.format("========================%n%n");
        fmt.format("Summary%n");
        fmt.format("-------%n");
        fmt.format("User-perceived characters     : %d%n", statistics.numberOfUserPerceivedCharacters());
        fmt.format("Total code points             : %d%n", cps.totalCodePoints());
        fmt.format("Unique code points            : %d%n", cps.numberOfUniqueCodePoints());
        fmt.format("Min frequency                 : %s -> %d%n", formatCodePoint(cps.leastFrequentCodePoint()), cps.leastFrequentCodePointFrequency());
        fmt.format("Max frequency                 : %s -> %d%n%n", formatCodePoint(cps.mostFrequentCodePoint()), cps.mostFrequentCodePointFrequency());
        fmt.format("Encoding sizes%n");
        fmt.format("--------------%n");
        fmt.format("%-12s %10s %10s %14s%n", "Encoding", "Bytes", "CodeUnits", "Bytes/Unit");
        fmt.format("%-12s %10s %10s %14s%n", "------------", "----------", "----------", "--------------");
        for (EncodingStatistic es : enc.statistics()) {
            final double bytesPerUnit = es.codeUnits() == 0 ? Double.NaN : es.bytes() / (double) es.codeUnits();
            fmt.format("%-12s %10d %10d %14.2f%n",
                    es.encoding().name(),
                    es.bytes(),
                    es.codeUnits(),
                    bytesPerUnit);
        }
        fmt.format("%nSavings%n");
        fmt.format("------%n");
        fmt.format("UTF-8  Byte Savings over UTF-16 bytes: %.2f %%%n", enc.utf8ByteSavingsOverUTF16());
        fmt.format("UTF-8  Byte Savings over UTF-32 bytes: %.2f %%%n", enc.utf8ByteSavingsOverUTF32());
        fmt.format("UTF-16 Byte Savings over UTF-32 bytes: %.2f %%%n", enc.utf16ByteSavingsOverUTF32());
    }

    private static String formatCodePoint(final int cp) {
        if (cp < 0) {
            return "<unknown>";
        }
        final String hex = String.format("U+%04X", cp);
        if (Character.isValidCodePoint(cp) && !Character.isISOControl(cp)) {
            return "[" + hex + ", '" + new String(Character.toChars(cp)) + "']";
        }
        return "[" + hex + "]";
    }
}
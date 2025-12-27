package com.muneebkhawaja.testing.cookbook.testng;

import java.util.List;

public record EncodingStatisticCollection(
        List<EncodingStatistic> statistics,
        double utf8ByteSavingsOverUTF16,
        double utf8ByteSavingsOverUTF32,
        double utf16ByteSavingsOverUTF32) {
}

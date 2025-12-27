package com.muneebkhawaja.testing.cookbook.testng;

import java.nio.charset.Charset;

/// ## Example
///
/// Consider the 'code point' 😀 [U+1F600](https://unicodeplus.com/U+1F600)
/// - Unicode Code Point: U+1F600 (hex) -> 128_512 (decimal)
/// - Outside the Basic Multilingual Plane (BMP)
/// - It is encoded in the Emoticons block, which belongs to the Supplementary Multilingual Plane (SMP)
/// - Note for the UTF16/UTF32 examples below:
///     * BE = Big Endian
///     * LE = Little Endian
///     * UTF16 (without a LE/BE suffix) has 2 bytes used to store the BOM (Byte Order Mark)
///
/// ### UTF-8
/// - Code Unit Size = 8 bits = 1 Byte
/// - Variable Length Encoding;
///     * 1 byte: U+0000 to U+007F (ASCII)
///     * 2 bytes: U+0080 to .U+07FF (Some BMP ranges)
///     * 3 bytes: U+0800 to U+FFFF (rest of BMP, excluding surrogates)
/// 	* 4 bytes: U+10000 to U+10FFFF (supplementary planes, including SMP)
/// - Code units combine to form code points
/// - "😀".getBytes(StandardCharsets.UTF_8) = byte\[4\] { -16, -97, -104, -128 }
/// - 4 (bytes) / 1 (utf8 code unit bytes) = 4 Code Units
///
/// ### UTF-16 (Java Default)
///
/// - Code Unit Size = 16 bits = 2 Bytes
/// - Variable Length Encoding;
///     * Encodes a Code Point in 2 bytes or 1 Code Unit (the best case, ASCII, BMP)
///     * Encodes a Code Point in 4 bytes or 2 Code Units (surrogate pair) (the worst case, SMP)
/// - Code units combine to form code points
/// - "😀".getBytes(StandardCharsets.UTF_16) = byte\[6\] { -2, -1, -40, 61, -34, 0 }
/// - "😀".getBytes(StandardCharsets.UTF_16BE) = byte\[4\] { -40, 61, -34, 0 }
/// - "😀".getBytes(StandardCharsets.UTF_16LE) = byte\[4\] { 61, -40, 0, -34 }
/// - 4 (bytes) / 2 (utf16 code unit bytes) = 2 Code Units
///
/// ### UTF-32
/// - Code Unit Size: 32 bits = 4 Bytes
/// - Every code point will take 4 bytes (in every case)
/// - Code unit equals code point
/// - Fixed Length; Everything takes 4 bytes
/// - Can Represent the entire Unicode Range (up to 0x10FFFF) in one 'Code Unit'
/// - "😀".getBytes(StandardCharsets.UTF_32) = byte\[4\] { 0, 1, -10, 0 }
/// - "😀".getBytes(StandardCharsets.UTF_32BE) = byte\[4\] { 0, 1, -10, 0 }
/// - "😀".getBytes(StandardCharsets.UTF_32LE) = byte\[4\] { 0, -10, 1, 0 }
/// - 4 (bytes) / 4 (utf32 code unit bytes) = 1 Code Units
public record EncodingStatistic(Charset encoding,
                                int bytes,
                                int codeUnits) {
}

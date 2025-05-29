package dev.mathops.text.fips;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.HexEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code FIPS180SHA1} class.
 */
public class TestFIPS180SHA1 {

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-1 hash of 'abc'")
    void test001() {

        final String text = "abc";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA1.INSTANCE.hash(message);

        final String expect = "a9993e364706816aba3e25717850c26c9cd0d89d";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-1 hash of 'abc'.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-1 hash of ''")
    void test002() {

        final String text = CoreConstants.EMPTY;
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA1.INSTANCE.hash(message);

        final String expect = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-1 hash of ''.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-1 hash of 448-bit")
    void test003() {

        final String text = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA1.INSTANCE.hash(message);

        final String expect = "84983e441c3bd26ebaae4aa1f95129e5e54670f1";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-1 hash of 448-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-1 hash of 896-bit")
    void test004() {

        final String text = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopq"
                            + "klmnopqrlmnopqrsmnopqrstnopqrstu";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA1.INSTANCE.hash(message);

        final String expect = "a49b2446a02c645bf419f995b67091253a04a259";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-1 hash of 896-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-1 hash of 8000000-bit")
    void test005() {

        final byte[] message = new byte[1000000];
        Arrays.fill(message, (byte)'a');
        final byte[] hash = FIPS180SHA1.INSTANCE.hash(message);

        final String expect = "34aa973cd4c4daa4f61eeb2bdbad27316534016f";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-1 hash of 896-bit.");
    }
}

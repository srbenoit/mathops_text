package dev.mathops.text.fips;

import dev.mathops.commons.HexEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code FIPS180SHA512256} class.
 */
public class TestFIPS180SHA512256 {

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-512/256 hash of 'abc'")
    void test001() {

        final String text = "abc";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA512256.INSTANCE.hash(message);

        final String expect = "53048E2681941EF99B2E29B76B4C7DABE4C2D0C634FC6D46E0E2F13107E7AF23".toLowerCase();
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-512/256 hash of 'abc'.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-512/256 hash of 896-bit")
    void test004() {

        final String text = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA512256.INSTANCE.hash(message);

        final String expect = "3928E184FB8690F840DA3988121D31BE65CB9D3EF83EE6146FEAC861E19B563A".toLowerCase();
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-512/256 hash of 896-bit.");
    }
}

package dev.mathops.text.fips;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.HexEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code FIPS180SHA512} class.
 */
public class TestFIPS180SHA384 {

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-384 hash of 'abc'")
    void test001() {

        final String text = "abc";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA384.INSTANCE.hash(message);

        final String expect = "cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-384 hash of 'abc'.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-384 hash of ''")
    void test002() {

        final String text = CoreConstants.EMPTY;
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA384.INSTANCE.hash(message);

        final String expect = "38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-384 hash of ''.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-384 hash of 448-bit")
    void test003() {

        final String text = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA384.INSTANCE.hash(message);

        final String expect = "3391fdddfc8dc7393707a65b1b4709397cf8b1d162af05abfe8f450de5f36bc6b0455a8520bc4e6f5fe95b1fe3c8452b";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-384 hash of 448-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-384 hash of 896-bit")
    void test004() {

        final String text = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopq"
                            + "klmnopqrlmnopqrsmnopqrstnopqrstu";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA384.INSTANCE.hash(message);

        final String expect = "09330c33f71147e83d192fc782cd1b4753111b173b3b05d22fa08086e3b0f712fcc7c71a557e2db966c3e9fa91746039";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-384 hash of 896-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-384 hash of 8000000-bit")
    void test005() {

        final byte[] message = new byte[1000000];
        Arrays.fill(message, (byte) 'a');
        final byte[] hash = FIPS180SHA384.INSTANCE.hash(message);

        final String expect = "9d0e1809716474cb086e834e310a4a1ced149e9c00f248527972cec5704c2a5b07b8b3dc38ecc4ebae97ddd87f3d8985";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-384 hash of 896-bit.");
    }
}

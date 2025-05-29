package dev.mathops.text.fips;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.HexEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code FIPS180SHA224} class.
 */
public class TestFIPS180SHA224 {

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-224 hash of 'abc'")
    void test001() {

        final String text = "abc";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA224.INSTANCE.hash(message);

        final String expect = "23097d223405d8228642a477bda255b32aadbce4bda0b3f7e36c9da7";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-224 hash of 'abc'.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-224 hash of ''")
    void test002() {

        final String text = CoreConstants.EMPTY;
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA224.INSTANCE.hash(message);

        final String expect = "d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-224 hash of ''.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-224 hash of 448-bit")
    void test003() {

        final String text = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA224.INSTANCE.hash(message);

        final String expect = "75388b16512776cc5dba5da1fd890150b0c6455cb4f58b1952522525";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-224 hash of 448-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-224 hash of 896-bit")
    void test004() {

        final String text = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopq"
                            + "klmnopqrlmnopqrsmnopqrstnopqrstu";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA224.INSTANCE.hash(message);

        final String expect = "c97ca9a559850ce97a04a96def6d99a9e0e0e2ab14e6b8df265fc0b3";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-224 hash of 896-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-224 hash of 8000000-bit")
    void test005() {

        final byte[] message = new byte[1000000];
        Arrays.fill(message, (byte) 'a');
        final byte[] hash = FIPS180SHA224.INSTANCE.hash(message);

        final String expect = "20794655980c91d8bbb4c1ea97618a4bf03f42581948b2ee4ee7ad67";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-224 hash of 896-bit.");
    }
}

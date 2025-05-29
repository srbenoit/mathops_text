package dev.mathops.text.fips;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.HexEncoder;
import dev.mathops.commons.log.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code FIPS180SHA256} class.
 */
public class TestFIPS180SHA256 {

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-256 hash of 'abc'")
    void test001() {

        final String text = "abc";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA256.INSTANCE.hash(message);

        final String expect = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-256 hash of 'abc'.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-256 hash of ''")
    void test002() {

        final String text = CoreConstants.EMPTY;
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA256.INSTANCE.hash(message);

        final String expect = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-256 hash of ''.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-256 hash of 448-bit")
    void test003() {

        final String text = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA256.INSTANCE.hash(message);

        final String expect = "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-256 hash of 448-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-256 hash of 896-bit")
    void test004() {

        final String text = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopq"
                            + "klmnopqrlmnopqrsmnopqrstnopqrstu";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA256.INSTANCE.hash(message);

        final String expect = "cf5b16a778af8380036ce59e7b0492370b249b11e8f07a51afac45037afee9d1";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-256 hash of 896-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-256 hash of 896-bit")
    void test005() {

        final byte[] message = new byte[1000000];
        Arrays.fill(message, (byte) 'a');
        final byte[] hash = FIPS180SHA256.INSTANCE.hash(message);

        final String expect = "cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-256 hash of 896-bit.");
    }
}

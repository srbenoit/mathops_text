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
public class TestFIPS180SHA512 {

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-512 hash of 'abc'")
    void test001() {

        final String text = "abc";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA512.INSTANCE.hash(message);

        final String expect = "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-512 hash of 'abc'.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-512 hash of ''")
    void test002() {

        final String text = CoreConstants.EMPTY;
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA512.INSTANCE.hash(message);

        final String expect = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-512 hash of ''.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-512 hash of 448-bit")
    void test003() {

        final String text = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA512.INSTANCE.hash(message);

        final String expect = "204a8fc6dda82f0a0ced7beb8e08a41657c16ef468b228a8279be331a703c33596fd15c13b1b07f9aa1d3bea57789ca031ad85c7a71dd70354ec631238ca3445";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-512 hash of 448-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-512 hash of 896-bit")
    void test004() {

        final String text = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopq"
                            + "klmnopqrlmnopqrsmnopqrstnopqrstu";
        final byte[] message = text.getBytes(StandardCharsets.UTF_8);
        final byte[] hash = FIPS180SHA512.INSTANCE.hash(message);

        final String expect = "8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-512 hash of 896-bit.");
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("SHA-512 hash of 8000000-bit")
    void test005() {

        final byte[] message = new byte[1000000];
        Arrays.fill(message, (byte) 'a');
        final byte[] hash = FIPS180SHA512.INSTANCE.hash(message);

        final String expect = "e718483d0ce769644e2e42c7bc15b4638e1f98b13b2044285632a803afa973ebde0ff244877ea60a4cb0432ce577c31beb009c5c2c49aa2e4eadb217ad8cc09b";
        final String actual = HexEncoder.encodeLowercase(hash);

        assertEquals(expect, actual, "Mismatch of SHA-512 hash of 896-bit.");
    }
}

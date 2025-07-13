package dev.mathops.text.internet;

import dev.mathops.commons.HexEncoder;
import dev.mathops.commons.log.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * An implementation of <a href='https://www.rfc-editor.org/rfc/rfc2104'>RFC 2104 (HMAC: Keyed-Hashing for Message
 * Authentication)</a>.
 */
public enum RFC2104 {
    ;

    /** The block size for MD5. */
    private static final int MD5_BLOCK_SIZE = 64;

    /** The block size for SHA. */
    private static final int SHA_BLOCK_SIZE = 64;

    /** The block size for SHA-224. */
    private static final int SHA_224_BLOCK_SIZE = 64;

    /** The block size for SHA-256. */
    private static final int SHA_256_BLOCK_SIZE = 64;

    /** The block size for SHA-384. */
    private static final int SHA_384_BLOCK_SIZE = 128;

    /** The block size for SHA-512. */
    private static final int SHA_512_BLOCK_SIZE = 128;

    /** Fill bytes for the inner pad. */
    private static final byte IPAD_FILL = (byte) 0x36;

    /** Fill bytes for the outer pad. */
    private static final byte OPAD_FILL = (byte) 0x5C;

    /**
     * Computes the HMAC of a block of text using a supplied key and the MD5 hash.
     *
     * @param text the test
     * @param key  the key
     * @return the resulting digest
     */
    public static byte[] HMAC_MD5(final byte[] text, final byte[] key) {

        byte[] result = null;

        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            result = hmac(text, key, digest, MD5_BLOCK_SIZE);
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to instantiate a MessageDigest object with the 'MD5' algorithm.", ex);
        }

        return result;
    }

    /**
     * Computes the HMAC of a block of text using a supplied key and the SHA hash.
     *
     * @param text the test
     * @param key  the key
     * @return the resulting digest
     */
    public static byte[] HMAC_SHA(final byte[] text, final byte[] key) {

        byte[] result = null;

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA");
            result = hmac(text, key, digest, SHA_BLOCK_SIZE);
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to instantiate a MessageDigest object with the 'SHA' algorithm.", ex);
        }

        return result;
    }

    /**
     * Computes the HMAC of a block of text using a supplied key and the SHA-224 hash.
     *
     * @param text the test
     * @param key  the key
     * @return the resulting digest
     */
    public static byte[] HMAC_SHA224(final byte[] text, final byte[] key) {

        byte[] result = null;

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-224");
            result = hmac(text, key, digest, SHA_224_BLOCK_SIZE);
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to instantiate a MessageDigest object with the 'SHA-224' algorithm.", ex);
        }

        return result;
    }

    /**
     * Computes the HMAC of a block of text using a supplied key and the SHA-256 hash.
     *
     * @param text the test
     * @param key  the key
     * @return the resulting digest
     */
    public static byte[] HMAC_SHA256(final byte[] text, final byte[] key) {

        byte[] result = null;

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            result = hmac(text, key, digest, SHA_256_BLOCK_SIZE);
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to instantiate a MessageDigest object with the 'SHA-256' algorithm.", ex);
        }

        return result;
    }

    /**
     * Computes the HMAC of a block of text using a supplied key and the SHA-384 hash.
     *
     * @param text the test
     * @param key  the key
     * @return the resulting digest
     */
    public static byte[] HMAC_SHA384(final byte[] text, final byte[] key) {

        byte[] result = null;

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-384");
            result = hmac(text, key, digest, SHA_384_BLOCK_SIZE);
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to instantiate a MessageDigest object with the 'SHA-384' algorithm.", ex);
        }

        return result;
    }

    /**
     * Computes the HMAC of a block of text using a supplied key and the SHA-512 hash.
     *
     * @param text the test
     * @param key  the key
     * @return the resulting digest
     */
    public static byte[] HMAC_SHA512(final byte[] text, final byte[] key) {

        byte[] result = null;

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-512");
            result = hmac(text, key, digest, SHA_512_BLOCK_SIZE);
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to instantiate a MessageDigest object with the 'SHA-512' algorithm.", ex);
        }

        return result;
    }

    /**
     * Computes the HMAC of a block of text using a supplied key and digest method.
     *
     * @param text      the test
     * @param key       the key
     * @param digest    the digest function
     * @param blockSize the block size for the digest
     * @return the resulting digest
     */
    private static byte[] hmac(final byte[] text, final byte[] key, final MessageDigest digest, final int blockSize) {

        final byte[] actualKey = key.length > blockSize ? digest.digest(key) : key;

        final byte[] ipad = new byte[blockSize];
        final byte[] opad = new byte[blockSize];
        final byte[] paddedKey = new byte[blockSize];
        Arrays.fill(ipad, IPAD_FILL);
        Arrays.fill(opad, OPAD_FILL);
        System.arraycopy(actualKey, 0, paddedKey, 0, actualKey.length);

        for (int i = 0; i < blockSize; ++i) {
            ipad[i] = (byte) ((int) ipad[i] ^ (int) paddedKey[i]);
            opad[i] = (byte) ((int) opad[i] ^ (int) paddedKey[i]);
        }

        final byte[] data1 = new byte[text.length + blockSize];
        System.arraycopy(ipad, 0, data1, 0, blockSize);
        System.arraycopy(text, 0, data1, blockSize, text.length);
        final byte[] digest1 = digest.digest(data1);

        final byte[] data2 = new byte[digest1.length + blockSize];
        System.arraycopy(opad, 0, data2, 0, blockSize);
        System.arraycopy(digest1, 0, data2, blockSize, digest1.length);

        return digest.digest(data2);
    }

    /**
     * Tests the implementation.
     *
     * @param args
     */
    public static void main(final String... args) {

        final byte[] text1 = "Hi There".getBytes(StandardCharsets.UTF_8);
        final byte[] key1 = {(byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b,
                (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b,
                (byte) 0x0b, (byte) 0x0b, (byte) 0x0b};
        final byte[] hmac1 = HMAC_MD5(text1, key1);
        final String hex1 = HexEncoder.encodeLowercase(hmac1);
        if ("9294727a3638bb1c13f48ef8158bfc9d".equals(hex1)) {
            Log.info("Test vector 1 successful.");
        } else {
            Log.warning("Test vector 1 generated '", hex1, "' rather than '9294727a3638bb1c13f48ef8158bfc9d'");
        }

        final byte[] text2 = "what do ya want for nothing?".getBytes(StandardCharsets.UTF_8);
        final byte[] key2 = {(byte) 'J', (byte) 'e', (byte) 'f', (byte) 'e'};
        final byte[] hmac2 = HMAC_MD5(text2, key2);
        final String hex2 = HexEncoder.encodeLowercase(hmac2);
        if ("750c783e6ab0b503eaa86e310a5db738".equals(hex2)) {
            Log.info("Test vector 2 successful.");
        } else {
            Log.warning("Test vector 2 generated '", hex2, "' rather than '750c783e6ab0b503eaa86e310a5db738'");
        }

        final byte[] text3 = {(byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
                (byte) 0xDD, (byte) 0xDD};
        final byte[] key3 = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
                (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
                (byte) 0xAA, (byte) 0xAA, (byte) 0xAA};
        final byte[] hmac3 = HMAC_MD5(text3, key3);
        final String hex3 = HexEncoder.encodeLowercase(hmac3);
        if ("56be34521d144c88dbb8c733f0e8b3f6".equals(hex3)) {
            Log.info("Test vector 3 successful.");
        } else {
            Log.warning("Test vector 3 generated '", hex3, "' rather than '56be34521d144c88dbb8c733f0e8b3f6'");
        }
    }
}

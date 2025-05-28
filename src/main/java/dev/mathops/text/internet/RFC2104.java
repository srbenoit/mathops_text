package dev.mathops.text.internet;

import dev.mathops.commons.log.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * An implementation of <a href='https://www.rfc-editor.org/rfc/rfc2104'>RFC 2104 (HMAC: Keyed-Hashing for Message
 * Authentication)</a>.
 */
public enum RFC2104 {
    ;

    /**
     * Computes the HMAC of a block of text using a supplied key and digest method.
     *
     * @param text   the test
     * @param key    the key
     * @param method the digest method ("MD5", "SHA", "SHA-224", "SHA-256", "SHA-384", "SHA-512")
     * @return the resulting digest
     */
    public static byte[] hmac(final byte[] text, final byte[] key, final String method) {

        byte[] result = null;

        try {
            final MessageDigest digest = MessageDigest.getInstance(method);
            final int blockSize = ("SHA-512".equals(method) || "SHA-384".equals(method)) ? 128 : 64;

            final byte[] actualKey = key.length > blockSize ? digest.digest(key) : key;

            final byte[] ipad = new byte[blockSize];
            final byte[] opad = new byte[blockSize];
            final byte[] paddedKey = new byte[blockSize];
            Arrays.fill(ipad, (byte) 0x36);
            Arrays.fill(opad, (byte) 0x5C);
            System.arraycopy(actualKey, 0, paddedKey, 0, actualKey.length);

            for (int i = 0; i < blockSize; ++i) {
                ipad[i] = (byte) (ipad[i] ^ paddedKey[i]);
                opad[i] = (byte) (opad[i] ^ paddedKey[i]);
            }

            final byte[] data1 = new byte[text.length + blockSize];
            System.arraycopy(ipad, 0, data1, 0, blockSize);
            System.arraycopy(text, 0, data1, blockSize, text.length);
            final byte[] digest1 = digest.digest(data1);

            final byte[] data2 = new byte[digest1.length + blockSize];
            System.arraycopy(opad, 0, data2, 0, blockSize);
            System.arraycopy(digest1, 0, data2, blockSize, digest1.length);
            result = digest.digest(data2);
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to instantiate a MessageDigest object with the '", method, "' algorithm.", ex);
        }

        return result;
    }

//    /**
//     * Tests the implementation.
//     *
//     * @param args
//     */
//    public static void main(final String... args) {
//
//        final byte[] text1 = "Hi There".getBytes(StandardCharsets.UTF_8);
//        final byte[] key1 = {(byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte)
//        0x0b,
//                (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte) 0x0b, (byte)
//                0x0b,
//                (byte) 0x0b};
//        final byte[] hmac1 = hmac(text1, key1, "MD5");
//        final String hex1 = HexEncoder.encodeLowercase(hmac1);
//        if ("9294727a3638bb1c13f48ef8158bfc9d".equals(hex1)) {
//            Log.info("Test vector 1 successful.");
//        } else {
//            Log.warning("Test vector 1 generated '", hex1, "' rather than '9294727a3638bb1c13f48ef8158bfc9d'");
//        }
//
//        final byte[] text2 = "what do ya want for nothing?".getBytes(StandardCharsets.UTF_8);
//        final byte[] key2 = {(byte) 'J', (byte) 'e', (byte) 'f', (byte) 'e'};
//        final byte[] hmac2 = hmac(text2, key2, "MD5");
//        final String hex2 = HexEncoder.encodeLowercase(hmac2);
//        if ("750c783e6ab0b503eaa86e310a5db738".equals(hex2)) {
//            Log.info("Test vector 2 successful.");
//        } else {
//            Log.warning("Test vector 2 generated '", hex2, "' rather than '750c783e6ab0b503eaa86e310a5db738'");
//        }
//
//        final byte[] text3 = {(byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte)
//        0xDD,
//                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte)
//                0xDD,
//                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte)
//                0xDD,
//                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte)
//                0xDD,
//                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte)
//                0xDD,
//                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte)
//                0xDD,
//                (byte) 0xDD, (byte) 0xDD, (byte) 0xDD};
//        final byte[] key3 = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte)
//        0xAA,
//                (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte)
//                0xAA,
//                (byte) 0xAA};
//        final byte[] hmac3 = hmac(text3, key3, "MD5");
//        final String hex3 = HexEncoder.encodeLowercase(hmac3);
//        if ("56be34521d144c88dbb8c733f0e8b3f6".equals(hex3)) {
//            Log.info("Test vector 3 successful.");
//        } else {
//            Log.warning("Test vector 3 generated '", hex3, "' rather than '56be34521d144c88dbb8c733f0e8b3f6'");
//        }
//    }
}

package dev.mathops.text.internet;

import dev.mathops.commons.log.Log;

/**
 * An implementation of <a href='https://www.rfc-editor.org/rfc/rfc7518.html'>RFC 7518 (JSON Web Algorithms (JWA))</a>.
 */
public enum RFC7518 {
    ;

    /**
     * An implementation of the "HS256" (HMAC using SHA-256) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algHS256(final byte[] signingInput, final byte[] key) {

        byte[] result = null;

        if (key.length >= 32) {
            result = RFC2104.hmac(signingInput, key, "SHA-256");
        } else {
            Log.warning("HS256 algorithm requires a key length of 32 or greater.");
        }

        return result;
    }

    /**
     * Verifies a signature using the "HS256" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgHS256(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algHS256(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "HS384" (HMAC using SHA-384) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algHS384(final byte[] signingInput, final byte[] key) {

        byte[] result = null;

        if (key.length >= 48) {
            result = RFC2104.hmac(signingInput, key, "SHA-384");
        } else {
            Log.warning("HS384 algorithm requires a key length of 48 or greater.");
        }

        return result;
    }

    /**
     * Verifies a signature using the "HS384" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgHS384(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algHS384(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "HS512" (HMAC using SHA-512) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algHS512(final byte[] signingInput, final byte[] key) {

        byte[] result = null;

        if (key.length >= 64) {
            result = RFC2104.hmac(signingInput, key, "SHA-512");
        } else {
            Log.warning("HS512 algorithm requires a key length of 64 or greater.");
        }

        return result;
    }

    /**
     * Verifies a signature using the "HS512" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgHS512(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algHS512(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "RS256" (RSASSA-PKCS1-v1_5 using SHA-256) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algRS256(final byte[] signingInput, final byte[] key) {

        byte[] result = null;

        if (key.length >= 256) {

        } else {
            Log.warning("RS256 algorithm requires a key length of 256 or greater.");
        }

        return result;
    }

    /**
     * Verifies a signature using the "RS256" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgRS256(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algRS256(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "RS384" (RSASSA-PKCS1-v1_5 using SHA-384) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algRS384(final byte[] signingInput, final byte[] key) {

        byte[] result = null;

        if (key.length >= 256) {

        } else {
            Log.warning("RS384 algorithm requires a key length of 256 or greater.");
        }

        return result;
    }

    /**
     * Verifies a signature using the "RS384" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgRS384(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algRS384(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "RS512" (RSASSA-PKCS1-v1_5 using SHA-512) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algRS512(final byte[] signingInput, final byte[] key) {

        byte[] result = null;

        if (key.length >= 256) {

        } else {
            Log.warning("RS512 algorithm requires a key length of 256 or greater.");
        }

        return result;
    }

    /**
     * Verifies a signature using the "RS512" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgRS512(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algRS512(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "ES256" (ECDSA using P-256 and SHA-256) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algES256(final byte[] signingInput, final byte[] key) {

    }

    /**
     * Verifies a signature using the "ES256" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgES256(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algES256(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "ES384" (ECDSA using P-384 and SHA-384) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algES384(final byte[] signingInput, final byte[] key) {

    }

    /**
     * Verifies a signature using the "ES384" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgES384(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algES384(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "ES512" (ECDSA using P-512 and SHA-512) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algES512(final byte[] signingInput, final byte[] key) {

    }

    /**
     * Verifies a signature using the "ES512" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgES512(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algES512(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "PS256" (RSASSA-PSS using SHA-256 and MGF1 with SHA-256) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algPS256(final byte[] signingInput, final byte[] key) {

    }

    /**
     * Verifies a signature using the "PS256" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgPS256(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algPS256(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "PS384" (RSASSA-PSS using SHA-384 and MGF1 with SHA-384) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algPS384(final byte[] signingInput, final byte[] key) {

    }

    /**
     * Verifies a signature using the "PS384" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgPS384(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algPS384(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * An implementation of the "PS512" (RSASSA-PSS using SHA-512 and MGF1 with SHA-512) algorithm.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @return the JWS signature; null if an error occurred
     */
    public static byte[] algPS512(final byte[] signingInput, final byte[] key) {

    }

    /**
     * Verifies a signature using the "PS512" algorithm in constant-time fashion as required by RFC 1518.
     *
     * @param signingInput the block of text to be hashed
     * @param key          the key
     * @param signature    the signature to validate
     * @return true if the signature was valid; false if not
     */
    public static boolean validateAlgPS512(final byte[] signingInput, final byte[] key, final byte[] signature) {

        final byte[] expected = algPS512(signingInput, key);

        return constantTimeCompare(expected, signature);
    }

    /**
     * Performs a constant-time comparison of two byte arrays, where the time spent will be based on the length of the
     * first provided array.
     *
     * @param array1 the first array to compare
     * @param array1 the second array to compare
     * @return true if the arrays match; false if not
     */
    private static boolean constantTimeCompare(final byte[] array1, final byte[] array2) {

        final int len1 = array1.length;
        final int len2 = array2.length;

        boolean match = len2 == len1;

        // Loop always runs the same number of iterations for constant time
        for (int i = 0; i < len1; ++i) {
            final int value1 = (int) array1[i];
            final int value2 = len2 > i ? (int) array2[i] : 999;
            if (value1 != value2) {
                match = false; // No break - need constant time.
            }
        }

        return match;
    }
}

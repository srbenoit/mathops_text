package dev.mathops.text.internet;

import dev.mathops.text.parser.json.JSONObject;

import java.util.Base64;

/**
 * An implementation of <a href='https://www.rfc-editor.org/rfc/rfc7517.html'>RFC 7518 (JSON Web Key (JWK))</a>.
 */
public enum RFC7517 {
    ;

    /**
     * Generates a JWKS key set object containing a list of keys.
     *
     * @param keys the list of keys
     * @return the key set
     */
    public static JSONObject generateKeySet(final JSONObject... keys) {

        final JSONObject result = new JSONObject();

        final JSONObject[] array = keys.clone();
        result.setProperty("keys", array);

        return result;
    }

    /**
     * Generates a JSON Web Key with an RSA public key whose use is "sig".
     *
     * @param publicKey the public key
     * @param kid       the key ID
     * @return the key object
     */
    public static JSONObject generateSigningJWK(final RFC8017PublicKey publicKey, final String kid) {

        final JSONObject result = new JSONObject();

        final Base64.Encoder enc = Base64.getUrlEncoder();

        final byte[] exponentBytes = publicKey.e.toByteArray();
        final String exponentStr = enc.encodeToString(exponentBytes);

        final byte[] modulusBytes = publicKey.n.toByteArray();
        final String modulusStr = enc.encodeToString(modulusBytes);

        result.setProperty("kty", "RSA");
        result.setProperty("use", "sig");
        result.setProperty("kid", kid);
        result.setProperty("alg", "RS256");
        result.setProperty("e", exponentStr);
        result.setProperty("n", modulusStr);

        return result;
    }

    /**
     * Generates a JSON Web Key with an RSA public key whose use is "enc".
     *
     * @param publicKey the public key
     * @param kid       the key ID
     * @return the key object
     */
    public static JSONObject generateEncryptingJWK(final RFC8017PublicKey publicKey, final String kid) {

        final JSONObject result = new JSONObject();

        final Base64.Encoder enc = Base64.getUrlEncoder();

        final byte[] exponentBytes = publicKey.e.toByteArray();
        final String exponentStr = enc.encodeToString(exponentBytes);

        final byte[] modulusBytes = publicKey.n.toByteArray();
        final String modulusStr = enc.encodeToString(modulusBytes);

        result.setProperty("kty", "RSA");
        result.setProperty("use", "enc");
        result.setProperty("kid", kid);
        result.setProperty("alg", "RS256");
        result.setProperty("e", exponentStr);
        result.setProperty("n", modulusStr);

        return result;
    }

    /**
     * Generates a JSON container in which to store a private key (for secure storage).
     *
     * @param privateKey the private key
     * @param kid        the key ID
     * @return the key object
     */
    public static JSONObject generatePrivate(final RFC8017PrivateKey1 privateKey, final String kid) {

        final JSONObject result = new JSONObject();

        final Base64.Encoder enc = Base64.getUrlEncoder();

        final byte[] exponentBytes = privateKey.d.toByteArray();
        final String exponentStr = enc.encodeToString(exponentBytes);

        final byte[] modulusBytes = privateKey.n.toByteArray();
        final String modulusStr = enc.encodeToString(modulusBytes);

        result.setProperty("kty", "RSA");
        result.setProperty("kid", kid);
        result.setProperty("d", exponentStr);
        result.setProperty("n", modulusStr);

        return result;
    }
}

package dev.mathops.text.internet;

import dev.mathops.commons.log.Log;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * A generator that creates valid RSA key pairs.
 */
public enum RFC8017KeyPairGenerator {
    ;

    /**
     * Generates a key pair.
     *
     * @return the key pair (with a type-1 private key).
     */
    public static KeyPair1 generateKeyPair() {

        KeyPair1 result = null;

        try {
            final KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            final KeyPair pair = gen.generateKeyPair();

            final PublicKey pub = pair.getPublic();
            final PrivateKey priv = pair.getPrivate();

            if (pub instanceof RSAPublicKey rsaPub && priv instanceof RSAPrivateKey rsaPriv) {
                final BigInteger modulus = rsaPub.getModulus();
                final BigInteger publicExponent = rsaPub.getPublicExponent();
                final BigInteger privateExponent = rsaPriv.getPrivateExponent();

                final RFC8017PublicKey rfcPub = new RFC8017PublicKey(modulus, publicExponent);
                final RFC8017PrivateKey1 rfcPiv = new RFC8017PrivateKey1(modulus, privateExponent);
                result = new KeyPair1(rfcPub, rfcPiv);
            } else {
                Log.warning("Generate keys do not implement expected interfaces.");
            }
        } catch (final NoSuchAlgorithmException ex) {
            Log.warning("Failed to create key pair generator.", ex);
        }

        return result;
    }

    public static record KeyPair1(RFC8017PublicKey pub, RFC8017PrivateKey1 priv) {
    }

    public static record KeyPair2(RFC8017PublicKey pub, RFC8017PrivateKey2 priv) {
    }
}

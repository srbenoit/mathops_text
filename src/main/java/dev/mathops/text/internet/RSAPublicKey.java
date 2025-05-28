package dev.mathops.text.internet;

import java.math.BigInteger;

/**
 * An RSA public key as defined in <a href='https://www.rfc-editor.org/rfc/rfc8017'>RFC 8017 (PKCS #1: RSA Cryptography
 * * Specifications Version 2.2)</a>.
 */
public class RSAPublicKey {

    /** The modulus (a positive integer, the product of two or more distinct odd primes). */
    public final BigInteger n;

    /** The public exponent (a positive integer between 3 and n- 1). */
    public final BigInteger e;

    /**
     * Constructs a new {@code RSAPublicKey}.
     *
     * @param theN the modulus (a positive integer, the product of two or more distinct odd primes)
     * @param theE the public exponent (a positive integer between 3 and n- 1)
     */
    public RSAPublicKey(final BigInteger theN, final BigInteger theE) {

        this.n = theN;
        this.e = theE;
    }
}

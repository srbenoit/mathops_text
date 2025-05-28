package dev.mathops.text.internet;

import java.math.BigInteger;

/**
 * An RSA private key (first representation) as defined in <a href='https://www.rfc-editor.org/rfc/rfc8017'>RFC 8017
 * (PKCS #1: RSA Cryptography * Specifications Version 2.2)</a>.
 */
public class RSAPrivateKey1 {

    /** The modulus. */
    public final BigInteger n;

    /** The private exponent. */
    public final BigInteger d;

    /**
     * Constructs a new {@code RSAPrivateKey1}.
     *
     * @param theN the modulus
     * @param theD the private exponent
     */
    public RSAPrivateKey1(final BigInteger theN, final BigInteger theD) {

        this.n = theN;
        this.d = theD;
    }
}

package dev.mathops.text.internet;

import java.math.BigInteger;

/**
 * An RSA private key (second representation) as defined in <a href='https://www.rfc-editor.org/rfc/rfc8017'>RFC 8017
 * (PKCS #1: RSA Cryptography * Specifications Version 2.2)</a>.
 */
public class RSAPrivateKey2 {

    /** The first factor (a positive integer). */
    public final BigInteger p;

    /** The second factor (a positive integer). */
    public final BigInteger q;

    /** The first factor's CRT exponent (a positive integer). */
    public final BigInteger dP;

    /** The second factor's CRT exponent (a positive integer). */
    public final BigInteger dQ;

    /** The (first) CRT coefficient (a positive integer). */
    public final BigInteger qInv;

    /** The triplets. */
    private final Triplet[] triplets;

    /**
     * Constructs a new {@code RSAPrivateKey2}.
     *
     * @param theP        the first factor (a positive integer)
     * @param theQ        the second factor (a positive integer)
     * @param theDP       the first factor's CRT exponent (a positive integer)
     * @param theDQ       the second factor's CRT exponent (a positive integer)
     * @param theQInv     the (first) CRT coefficient (a positive integer)
     * @param theTriplets a (possible empty) sequence of triplets
     */
    public RSAPrivateKey2(final BigInteger theP, final BigInteger theQ, final BigInteger theDP, final BigInteger theDQ,
                          final BigInteger theQInv, final Triplet... theTriplets) {

        this.p = theP;
        this.q = theQ;
        this.dP = theDP;
        this.dQ = theDQ;
        this.qInv = theQInv;
        this.triplets = theTriplets == null ? new Triplet[0] : theTriplets.clone();
    }

    /**
     * Gets the number of triplets.
     *
     * @return the number of triplets.
     */
    public int getNumTriplets() {

        return this.triplets.length;
    }

    /**
     * Gets a triplet.
     *
     * @param index the index
     * @return the triplet
     */
    public Triplet getTriplet(final int index) {

        return this.triplets[index];
    }

    /**
     * A container for a triplet of a factor and its CRT exponent and coefficient.
     *
     * @param r the i-th factor (a positive integer)
     * @param d the i-th factor's CRT exponent (a positive integer)
     * @param t the i-th factor's CRT coefficient (a positive integer)
     */
    public record Triplet(BigInteger r, BigInteger d, BigInteger t) {
    }
}

package dev.mathops.text.internet;

import dev.mathops.commons.CoreConstants;
import dev.mathops.text.fips.FIPS180;

import java.math.BigInteger;

/**
 * An implementation of <a href='https://www.rfc-editor.org/rfc/rfc8017'>RFC 8017 (PKCS #1: RSA Cryptography
 * Specifications Version 2.2)</a>.
 * <p>
 * This specification obsoletes RFC 3447.
 */
public enum RFC8017_3447 {
    ;

    /**
     * Converts an integer to an octet string.
     *
     * @param x         a non-negative integer to convert
     * @param nbrOctets the number of octets into which to encode the integer
     * @return the octet string (the most significant byte is first)
     * @throws IllegalArgumentException if the integer is too large to fit in the specified number of octets
     */
    public static byte[] I2OSP(final BigInteger x, final int nbrOctets) {

        byte[] result;

        byte[] bigEndian = x.toByteArray(); // Most significant byte in position [0]
        if ((int) bigEndian[0] == 0) {
            // The most-significant byte only carries the sign bit.  We only consider positive integers in this
            // method, so we can discard this top byte
            final int len = bigEndian.length - 1;
            final byte[] copy = new byte[len];
            System.arraycopy(bigEndian, 1, copy, 0, len);
            bigEndian = copy;
        }

        final int needLen = bigEndian.length;

        if (needLen > nbrOctets) {
            throw new IllegalArgumentException("integer too large");
        }

        if (needLen == nbrOctets) {
            result = bigEndian;
        } else {
            result = new byte[nbrOctets];
            final int start = nbrOctets - needLen;
            System.arraycopy(bigEndian, 0, result, start, needLen);
        }

        return result;
    }

    /**
     * Converts an octet string into a non-negative integer.
     *
     * @param octets the octet string (the most significant byte is first)
     * @return the corresponding integer
     */
    public static BigInteger OS2IP(final byte[] octets) {

        byte[] actual;

        if ((int) octets[0] < 0) {
            // Need a leading '0' byte to carry sign
            final int len = octets.length;
            actual = new byte[len + 1];
            System.arraycopy(octets, 0, actual, 1, len);
        } else {
            actual = octets;
        }

        return new BigInteger(actual);
    }

    /**
     * RSA Encryption primitive.
     *
     * @param publicKey the RSA public key to use to encrypt
     * @param m         the message representative
     * @return the encrypted (ciphertext) integer message representative
     * @throws IllegalArgumentException if {@code m} is negative or greater than or equal to the modulus in the public
     *                                  key
     */
    public static BigInteger RSAEP(final RSAPublicKey publicKey, final BigInteger m) {

        if (m.signum() == -1 || m.compareTo(publicKey.n) >= 0) {
            throw new IllegalArgumentException("message representative out of range.");
        }

        return m.modPow(publicKey.e, publicKey.n);
    }

    /**
     * RSA Decryption primitive.
     *
     * @param privateKey the RSA private key to use to decrypt
     * @param c          the encrypted (ciphertext) integer representative
     * @return the decrypted message representative
     * @throws IllegalArgumentException if {@code m} is negative or greater than or equal to the modulus in the public
     *                                  key
     */
    public static BigInteger RSADP(final RSAPrivateKey1 privateKey, final BigInteger c) {

        return c.modPow(privateKey.d, privateKey.n);
    }

    /**
     * RSA Decryption primitive.
     *
     * @param privateKey the RSA private key to use to decrypt
     * @param c          the encrypted (ciphertext) integer representative
     * @return the decrypted message representative
     */
    public static BigInteger RSADP(final RSAPrivateKey2 privateKey, final BigInteger c) {

        final int u = 2 + privateKey.getNumTriplets();
        final BigInteger[] m = new BigInteger[u];
        m[0] = c.modPow(privateKey.dP, privateKey.p);
        m[1] = c.modPow(privateKey.dQ, privateKey.q);

        BigInteger h = m[0].subtract(m[1]).multiply(privateKey.qInv).mod(privateKey.p);
        BigInteger result = m[1].add(privateKey.q.multiply(h));

        BigInteger r = privateKey.p;
        for (int i = 2; i < u; ++i) {
            final RSAPrivateKey2.Triplet triplet = privateKey.getTriplet(i - 2);
            m[i] = c.modPow(triplet.d(), triplet.r());
            r = r.multiply(triplet.r());
            h = m[i].subtract(result).multiply(triplet.t()).mod(triplet.r());
            result = result.add(r.multiply(h));
        }

        return result;
    }

    /**
     * RSA Signature primitive.
     *
     * @param privateKey the RSA private key to use to sign
     * @param m          the message representative to sign (an integer between 0 and n-1)
     * @return the signature representative (an integer between 0 and n-1)
     */
    public static BigInteger RSASP1(final RSAPrivateKey1 privateKey, final BigInteger m) {

        return m.modPow(privateKey.d, privateKey.n);
    }

    /**
     * RSA Signature primitive.
     *
     * @param privateKey the RSA private key to use to sign
     * @param m          the message representative to sign (an integer between 0 and n-1)
     * @return the signature representative (an integer between 0 and n-1)
     */
    public static BigInteger RSASP1(final RSAPrivateKey2 privateKey, final BigInteger m) {

        final int u = 2 + privateKey.getNumTriplets();
        final BigInteger[] s = new BigInteger[u];
        s[0] = m.modPow(privateKey.dP, privateKey.p);
        s[1] = m.modPow(privateKey.dQ, privateKey.q);

        BigInteger h = s[0].subtract(s[1]).multiply(privateKey.qInv).mod(privateKey.p);
        BigInteger result = s[1].add(privateKey.q.multiply(h));

        BigInteger r = privateKey.p;
        for (int i = 2; i < u; ++i) {
            final RSAPrivateKey2.Triplet triplet = privateKey.getTriplet(i - 2);
            s[i] = m.modPow(triplet.d(), triplet.r());
            r = r.multiply(triplet.r());
            h = s[i].subtract(result).multiply(triplet.t()).mod(triplet.r());
            result = result.add(r.multiply(h));
        }

        return result;
    }

    /**
     * RSA Signature Verification primitive.
     *
     * @param publicKey the RSA public key to use to verify
     * @param s         the signature representative to verify (an integer between 0 and n-1)
     * @return the message representative (an integer between 0 and n-1)
     */
    public static BigInteger RSAVP1(final RSAPublicKey publicKey, final BigInteger s) {

        if (s.signum() == -1 || s.compareTo(publicKey.n) >= 0) {
            throw new IllegalArgumentException("signature representative out of range.");
        }

        return s.modPow(publicKey.e, publicKey.n);
    }

    /**
     * The MFG1 mask generation function.
     *
     * @param seed         the seed from which mask is generated, an octet string
     * @param maskLen      intended length in octets of the mask, at most 2^32 hLen
     * @param hashFunction a hash function
     * @return the mask, an octet string of length maskLen
     */
    public static byte[] MGF1(final byte[] seed, final int maskLen, final FIPS180 hashFunction) {

        final int hLen = hashFunction.hashSize();
        final int count = ((maskLen + hLen - 1) / hLen);

        final int seedLen = seed.length;
        final byte[] toHash = new byte[seedLen + 4];
        System.arraycopy(seed, 0, toHash, 0, seedLen);

        byte[] t = new byte[count * hLen];

        int pos = 0;
        for (int i = 0; i < count; ++i) {
            toHash[seedLen] = (byte) (i >> 24);
            toHash[seedLen + 1] = (byte) (i >> 16);
            toHash[seedLen + 2] = (byte) (i >> 8);
            toHash[seedLen + 3] = (byte) (i);
            final byte[] hash = hashFunction.hash(toHash);
            System.arraycopy(hash, 0, t, pos, hLen);
            pos += hLen;
        }

        byte[] result;

        if (maskLen == t.length) {
            result = t;
        } else {
            result = new byte[maskLen];
            System.arraycopy(t, 0, result, 0, maskLen);
        }

        return result;
    }

    /**
     * Encrypts a message using RSAES-OAEP and the SHA256 hash.
     *
     * @param publicKey the recipient's public key
     * @param message   the message to encrypt
     * @param label     an optional label
     * @return the encrypted (ciphertext) message
     * @throws IllegalArgumentException if the message is too long
     */
    public static byte[] RSAES_OAEP_SHA256_ENCRYPT(final RSAPublicKey publicKey, final byte[] message,
                                                   final String label) {

        final byte[] modulusBytes = publicKey.n.toByteArray();
        final int k = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        final int hLen = 64; // number of bytes in hash function output, 512 / 8
        final int mLen = message.length;

        final int maxMessage = k - 2 * hLen - 2;
        if (mLen > maxMessage) {
            throw new IllegalArgumentException("message too long");
        }

        final String actualLabel = label == null ? CoreConstants.EMPTY : label;

    }
}

package dev.mathops.text.internet;

import dev.mathops.commons.installation.PathList;
import dev.mathops.commons.log.Log;
import dev.mathops.text.fips.FIPS180;
import dev.mathops.text.fips.FIPS180SHA1;
import dev.mathops.text.fips.FIPS180SHA224;
import dev.mathops.text.fips.FIPS180SHA256;
import dev.mathops.text.fips.FIPS180SHA384;
import dev.mathops.text.fips.FIPS180SHA512;
import dev.mathops.text.fips.FIPS180SHA512224;
import dev.mathops.text.fips.FIPS180SHA512256;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

/**
 * An implementation of <a href='https://www.rfc-editor.org/rfc/rfc8017'>RFC 8017 (PKCS #1: RSA Cryptography
 * Specifications Version 2.2)</a>.
 * <p>
 * This specification obsoletes RFC 3447.
 */
public enum RFC8017 {
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
    public static BigInteger RSAEP(final RFC8017PublicKey publicKey, final BigInteger m) {

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
    public static BigInteger RSADP(final RFC8017PrivateKey1 privateKey, final BigInteger c) {

        return c.modPow(privateKey.d, privateKey.n);
    }

    /**
     * RSA Decryption primitive.
     *
     * @param privateKey the RSA private key to use to decrypt
     * @param c          the encrypted (ciphertext) integer representative
     * @return the decrypted message representative
     */
    public static BigInteger RSADP(final RFC8017PrivateKey2 privateKey, final BigInteger c) {

        final int u = 2 + privateKey.getNumTriplets();
        final BigInteger[] m = new BigInteger[u];
        m[0] = c.modPow(privateKey.dP, privateKey.p);
        m[1] = c.modPow(privateKey.dQ, privateKey.q);

        BigInteger h = m[0].subtract(m[1]).multiply(privateKey.qInv).mod(privateKey.p);
        BigInteger result = m[1].add(privateKey.q.multiply(h));

        BigInteger r = privateKey.p;
        for (int i = 2; i < u; ++i) {
            final RFC8017PrivateKey2.Triplet triplet = privateKey.getTriplet(i - 2);
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
    public static BigInteger RSASP1(final RFC8017PrivateKey1 privateKey, final BigInteger m) {

        return m.modPow(privateKey.d, privateKey.n);
    }

    /**
     * RSA Signature primitive.
     *
     * @param privateKey the RSA private key to use to sign
     * @param m          the message representative to sign (an integer between 0 and n-1)
     * @return the signature representative (an integer between 0 and n-1)
     */
    public static BigInteger RSASP1(final RFC8017PrivateKey2 privateKey, final BigInteger m) {

        final int u = 2 + privateKey.getNumTriplets();
        final BigInteger[] s = new BigInteger[u];
        s[0] = m.modPow(privateKey.dP, privateKey.p);
        s[1] = m.modPow(privateKey.dQ, privateKey.q);

        BigInteger h = s[0].subtract(s[1]).multiply(privateKey.qInv).mod(privateKey.p);
        BigInteger result = s[1].add(privateKey.q.multiply(h));

        BigInteger r = privateKey.p;
        for (int i = 2; i < u; ++i) {
            final RFC8017PrivateKey2.Triplet triplet = privateKey.getTriplet(i - 2);
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
    public static BigInteger RSAVP1(final RFC8017PublicKey publicKey, final BigInteger s) {

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
     * Encrypts a message using RSAES-OAEP.
     *
     * @param publicKey    the recipient's public key
     * @param message      the message to encrypt
     * @param label        an optional label
     * @param hashFunction the hash function
     * @param rnd          a cryptographic random number generator
     * @return the encrypted (ciphertext) message
     * @throws IllegalArgumentException if the message is too long
     */
    public static byte[] RSAES_OAEP_ENCRYPT(final RFC8017PublicKey publicKey, final byte[] message,
                                            final byte[] label, final FIPS180 hashFunction, final Random rnd) {

        final byte[] modulusBytes = publicKey.n.toByteArray();
        final int k = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        final int hLen = hashFunction.hashSize();
        final int mLen = message.length;

        final int maxMessage = k - 2 * hLen - 2;
        if (mLen > maxMessage) {
            throw new IllegalArgumentException("message too long");
        }

        final byte[] labelBytes = label == null ? new byte[0] : label;
        final byte[] lHash = hashFunction.hash(labelBytes);

        final int padLen = k - mLen - 2 * hLen - 2;
        final byte[] ps = new byte[padLen];

        final int dbLen = hLen + padLen + 1 + mLen;
        final byte[] db = new byte[dbLen];
        System.arraycopy(lHash, 0, db, 0, hLen);
        System.arraycopy(ps, 0, db, hLen, padLen);
        db[hLen + padLen] = (byte) 0x01;
        System.arraycopy(message, 0, db, hLen + padLen + 1, mLen);

        final byte[] seed = new byte[hLen];
        rnd.nextBytes(seed);

        final byte[] dbMask = MGF1(seed, dbLen, hashFunction);
        final byte[] maskedDb = new byte[dbLen];
        for (int i = 0; i < dbLen; ++i) {
            maskedDb[i] = (byte) ((int) db[i] ^ (int) dbMask[i]);
        }

        final byte[] seedMask = MGF1(maskedDb, hLen, hashFunction);
        final byte[] maskedSeed = new byte[hLen];
        for (int i = 0; i < hLen; ++i) {
            maskedSeed[i] = (byte) ((int) seed[i] ^ (int) seedMask[i]);
        }

        final byte[] em = new byte[1 + hLen + dbLen];
        System.arraycopy(maskedSeed, 0, em, 1, hLen);
        System.arraycopy(maskedDb, 0, em, 1 + hLen, dbLen);

        final BigInteger m = OS2IP(em);
        final BigInteger c = RSAEP(publicKey, m);

        return I2OSP(c, k);
    }

    /**
     * Decrypts a message using RSAES-OAEP.
     *
     * @param privateKey   the recipient's private key
     * @param ciphertext   the ciphertext to be decrypted, an octet string of length k
     * @param label        optional label whose association with the message is to be verified; the default value for L,
     *                     if L is not provided, is the empty string
     * @param hashFunction the hash function
     * @return the decrypted message
     * @throws IllegalArgumentException if decryption fails
     */
    public static byte[] RSAES_OAEP_DECRYPT(final RFC8017PrivateKey1 privateKey, final byte[] ciphertext,
                                            final byte[] label, final FIPS180 hashFunction) {

        final byte[] modulusBytes = privateKey.n.toByteArray();
        final int k = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        if (ciphertext.length != k) {
            Log.warning("Ciphertext was length " + ciphertext.length + ", key modulus length is " + k);
            throw new IllegalArgumentException("decryption error");
        }

        final int hLen = hashFunction.hashSize();

        if (k < 2 * hLen + 2) {
            Log.warning("Invalid relationship between hash size and key modulus size.");
            throw new IllegalArgumentException("decryption error");
        }

        final BigInteger c = OS2IP(ciphertext);
        final BigInteger m = RSADP(privateKey, c);
        final byte[] em = I2OSP(m, k);

        final byte[] labelBytes = label == null ? new byte[0] : label;
        final byte[] lHash = hashFunction.hash(labelBytes);

        final int dbLen = k - hLen - 1;

        final byte y = em[0];
        final byte[] maskedSeed = new byte[hLen];
        final byte[] maskedDb = new byte[dbLen];
        System.arraycopy(em, 1, maskedSeed, 0, hLen);
        System.arraycopy(em, 1 + hLen, maskedDb, 0, dbLen);

        final byte[] seedMask = MGF1(maskedDb, hLen, hashFunction);
        final byte[] seed = new byte[hLen];
        for (int i = 0; i < hLen; ++i) {
            seed[i] = (byte) ((int) maskedSeed[i] ^ (int) seedMask[i]);
        }

        final byte[] dbMask = MGF1(seed, dbLen, hashFunction);
        final byte[] db = new byte[dbLen];
        for (int i = 0; i < dbLen; ++i) {
            db[i] = (byte) ((int) maskedDb[i] ^ (int) dbMask[i]);
        }

        final byte[] lHashPrime = new byte[hLen];
        System.arraycopy(db, 0, lHashPrime, 0, hLen);

        int pos = hLen;
        while (pos < db.length && db[pos] == 0) {
            ++pos;
        }
        if (db[pos] != 0x01) {
            throw new IllegalArgumentException("decryption error");
        }
        ++pos;
        final int mLen = dbLen - pos;
        final byte[] message = new byte[mLen];
        System.arraycopy(db, pos, message, 0, mLen);

        if (labelBytes.length > 0) {
            for (int i = 0; i < hLen; ++i) {
                if ((int) lHashPrime[i] != (int) lHash[i]) {
                    throw new IllegalArgumentException("decryption error");
                }
            }
        }

        return message;
    }

    /**
     * Encrypts a message using RSAES-PKCS1-V1_5.
     *
     * @param publicKey the recipient's public key
     * @param message   the message to encrypt
     * @param rnd       a cryptographic random number generator
     * @return the encrypted (ciphertext) message
     * @throws IllegalArgumentException if the message is too long
     */
    public static byte[] RSAES_PKCS1_V15_ENCRYPT(final RFC8017PublicKey publicKey, final byte[] message,
                                                 final Random rnd) {

        final byte[] modulusBytes = publicKey.n.toByteArray();
        final int k = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        final int mLen = message.length;

        final int maxMessage = k - 11;
        if (mLen > maxMessage) {
            throw new IllegalArgumentException("message too long");
        }

        final int padLen = k - mLen - 3;
        final byte[] ps = new byte[padLen];
        rnd.nextBytes(ps);
        for (int i = 0; i < padLen; ++i) {
            while ((int) ps[i] == 0) {
                ps[i] = (byte) rnd.nextInt();
            }
        }

        final byte[] em = new byte[3 + padLen + mLen];
        em[1] = 0x02;
        System.arraycopy(ps, 0, em, 2, padLen);
        System.arraycopy(message, 0, em, 3 + padLen, mLen);

        final BigInteger m = OS2IP(em);
        final BigInteger c = RSAEP(publicKey, m);

        return I2OSP(c, k);
    }

    /**
     * Decrypts a message using RSAES-PKCS1-V1_5.
     *
     * @param privateKey the recipient's private key
     * @param ciphertext the ciphertext to be decrypted, an octet string of length k
     * @return the decrypted message
     * @throws IllegalArgumentException if decryption fails
     */
    public static byte[] RSAES_PKCS1_V15_DECRYPT(final RFC8017PrivateKey1 privateKey, final byte[] ciphertext) {

        final byte[] modulusBytes = privateKey.n.toByteArray();
        final int k = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        if (k < 11) {
            Log.warning("key modulus length is too short");
            throw new IllegalArgumentException("decryption error");
        }
        if (ciphertext.length != k) {
            Log.warning("Ciphertext was length " + ciphertext.length + ", key modulus length is " + k);
            throw new IllegalArgumentException("decryption error");
        }

        final BigInteger c = OS2IP(ciphertext);
        final BigInteger m = RSADP(privateKey, c);
        final byte[] em = I2OSP(m, k);

        if (em[0] != 0x00 || em[1] != 0x02) {
            Log.warning("Invalid leading bytes in decrypted data");
            throw new IllegalArgumentException("decryption error");
        }
        int pos = 2;
        while (pos < em.length && em[pos] != 0x00) {
            ++pos;
        }
        if (pos == em.length) {
            Log.warning("Can't find trailing 0x00 after padding");
            throw new IllegalArgumentException("decryption error");
        }
        ++pos;

        final int mLen = em.length - pos;
        final byte[] message = new byte[mLen];
        System.arraycopy(em, pos, message, 0, mLen);

        return message;
    }

    /**
     * Performs EMSA-PSS encoding.
     *
     * @param message      the message to be encoded
     * @param emLen        the number bytes in the encoded message
     * @param saltLength   the length of salt, in bytes
     * @param hashFunction the hash function
     * @param rnd          a random number generator used to create salt value
     * @return the encoded message
     */
    public static byte[] EMSA_PSS_ENCODE(final byte[] message, final int emLen, final int saltLength,
                                         final FIPS180 hashFunction, final Random rnd) {

        final int hLen = hashFunction.hashSize();
        final byte[] mHash = hashFunction.hash(message);
        if (emLen < (hLen + saltLength + 2)) {
            throw new IllegalArgumentException("encoding error");
        }

        final byte[] salt = new byte[saltLength];
        rnd.nextBytes(salt);

        final int mPrimeLen = 8 + hLen + saltLength;
        final byte[] mPrime = new byte[mPrimeLen];
        System.arraycopy(mHash, 0, mPrime, 8, hLen);
        System.arraycopy(salt, 0, mPrime, 8 + hLen, saltLength);

        final byte[] h = hashFunction.hash(mPrime);

        final int psLen = emLen - saltLength - hLen - 2;
        final int dbLen = psLen + saltLength + 1; // dbLen = emLen - hLen - 1

        final byte[] db = new byte[dbLen];
        db[psLen] = 0x01;
        System.arraycopy(salt, 0, db, psLen + 1, saltLength);
        final byte[] dbMask = MGF1(h, dbLen, hashFunction);

        final byte[] maskedDb = new byte[dbLen];
        for (int i = 0; i < dbLen; ++i) {
            maskedDb[i] = (byte) ((int) db[i] ^ (int) dbMask[i]);
        }

        final byte[] em = new byte[emLen];
        System.arraycopy(maskedDb, 0, em, 0, dbLen);
        System.arraycopy(h, 0, em, dbLen, hLen);
        em[dbLen + hLen] = (byte) 0xbc;

        return em;
    }

    /**
     * Performs EMSA-PSS verification.
     *
     * @param message      the message to be encoded
     * @param encoded      the encoded message to verify
     * @param saltLength   the length of salt, in bytes
     * @param hashFunction the hash function
     * @return true if results are "consistent"; false if "inconsistent"
     */
    public static boolean EMSA_PSS_VERIFY(final byte[] message, final byte[] encoded,
                                          final int saltLength, final FIPS180 hashFunction) {

        final int hLen = hashFunction.hashSize();
        final int emLen = encoded.length;

        final byte[] mHash = hashFunction.hash(message);

        boolean result = true;
        if (emLen < hLen + saltLength + 2) {
            Log.warning("Encoded message length is not hash length + salt length + 2");
            result = false;
        } else if ((int) encoded[emLen - 1] != (int) ((byte) 0xbc)) {
            Log.warning("Last byte in encoded message is not correct");
            result = false;
        } else {
            final int dbLen = emLen - hLen - 1;
            final byte[] maskedDb = new byte[dbLen];
            final byte[] h = new byte[hLen];
            System.arraycopy(encoded, 0, maskedDb, 0, dbLen);
            System.arraycopy(encoded, dbLen, h, 0, hLen);

            final byte[] dbMask = MGF1(h, dbLen, hashFunction);
            final byte[] db = new byte[dbLen];
            for (int i = 0; i < dbLen; ++i) {
                db[i] = (byte) ((int) maskedDb[i] ^ (int) dbMask[i]);
            }

            final int numZeros = emLen - hLen - saltLength - 2;
            for (int i = 0; i < numZeros; ++i) {
                if (db[i] != 0) {
                    Log.warning("Padding contained nonzero byte");
                    result = false;
                    break;
                }
            }
            if (db[numZeros] != 0x01) {
                Log.warning("Padding not terminated by 0x01 byte");
                result = false;
            } else if (result) {
                final byte[] salt = new byte[saltLength];
                final int start = dbLen - saltLength;
                System.arraycopy(db, start, salt, 0, saltLength);

                final byte[] mPrime = new byte[8 + hLen + saltLength];
                System.arraycopy(mHash, 0, mPrime, 8, hLen);
                System.arraycopy(salt, 0, mPrime, 8 + hLen, saltLength);

                final byte[] hPrime = hashFunction.hash(mPrime);
                for (int i = 0; i < hLen; ++i) {
                    if (hPrime[i] != h[i]) {
                        Log.warning("Hash mismatch");
                        result = false;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Performs EMSA-PKCS1-V1_5 encoding.
     *
     * @param message      the message to be encoded
     * @param emLen        the number bytes in the encoded message
     * @param hashFunction the hash function
     * @return the encoded message
     */
    public static byte[] EMSA_PKCS1_V15_ENCODE(final byte[] message, final int emLen, final FIPS180 hashFunction) {

        final int hLen = hashFunction.hashSize();
        final byte[] mHash = hashFunction.hash(message);

        final byte[] t;
        if (hashFunction instanceof FIPS180SHA1) {
            t = new byte[]{0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2b, 0x0e, 0x03, 0x02, 0x1a, 0x05, 0x00, 0x04, 0x14};
        } else if (hashFunction instanceof FIPS180SHA224) {
            t = new byte[]{0x30, 0x2d, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
                    0x04, 0x05, 0x00, 0x04, 0x1c};
        } else if (hashFunction instanceof FIPS180SHA256) {
            t = new byte[]{0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
                    0x01, 0x05, 0x00, 0x04, 0x20};
        } else if (hashFunction instanceof FIPS180SHA384) {
            t = new byte[]{0x30, 0x41, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
                    0x02, 0x05, 0x00, 0x04, 0x30};
        } else if (hashFunction instanceof FIPS180SHA512) {
            t = new byte[]{0x30, 0x51, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
                    0x03, 0x05, 0x00, 0x04, 0x40};
        } else if (hashFunction instanceof FIPS180SHA512224) {
            t = new byte[]{0x30, 0x2d, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
                    0x05, 0x05, 0x00, 0x04, 0x1c};
        } else if (hashFunction instanceof FIPS180SHA512256) {
            t = new byte[]{0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02,
                    0x06, 0x05, 0x00, 0x04, 0x20};
        } else {
            Log.warning("Unsupported hash function.");
            throw new IllegalArgumentException("encoding error");
        }
        final int tLen = t.length + hLen;

        if (emLen < tLen + 11) {
            throw new IllegalArgumentException("intended encoded message length too short");
        }

        final int psLen = emLen - tLen - 3;
        final byte[] ps = new byte[psLen];
        Arrays.fill(ps, (byte) 0xFF);

        final byte[] em = new byte[emLen];
        em[1] = 0x01;
        System.arraycopy(ps, 0, em, 2, psLen);
        System.arraycopy(t, 0, em, 3 + psLen, t.length);
        System.arraycopy(mHash, 0, em, 3 + psLen + t.length, hLen);

        return em;
    }

    /**
     * Computes a digital signature of a message using RSASSA-PSS.
     *
     * @param privateKey   the signer's private key
     * @param message      the message to be signed
     * @param hashFunction the hash function
     * @param rnd          a random number generator used to create salt value
     * @return the signature
     */
    public static byte[] RSASSA_PSS_SIGN(final RFC8017PrivateKey1 privateKey, final byte[] message,
                                         final FIPS180 hashFunction, final Random rnd) {

        final byte[] modulusBytes = privateKey.n.toByteArray();
        final int emLen = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        final byte[] encoded = EMSA_PSS_ENCODE(message, emLen, 20, hashFunction, rnd);
        final BigInteger m = OS2IP(encoded);
        final BigInteger s = RSASP1(privateKey, m);

        return I2OSP(s, emLen);
    }

    /**
     * Verifies a digital signature of a message using RSASSA-PSS.
     *
     * @param publicKey    the signer's public key
     * @param message      the message to be signed
     * @param signature    the signature to be verified
     * @param hashFunction the hash function
     * @return true if results are "consistent"; false if "inconsistent"
     */
    public static boolean RSASSA_PSS_VERIFY(final RFC8017PublicKey publicKey, final byte[] message,
                                            final byte[] signature, final FIPS180 hashFunction) {

        final byte[] modulusBytes = publicKey.n.toByteArray();
        final int emLen = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        final BigInteger s = OS2IP(signature);
        final BigInteger m = RSAVP1(publicKey, s);
        final byte[] em = I2OSP(m, emLen);

        return EMSA_PSS_VERIFY(message, em, 20, hashFunction);
    }

    /**
     * Computes a digital signature of a message using RSASSA-PKCS1-V1_5.
     *
     * @param privateKey   the signer's private key
     * @param message      the message to be signed
     * @param hashFunction the hash function
     * @param rnd          a random number generator used to create salt value
     * @return the signature
     */
    public static byte[] RSASSA_PKCS1_V15_SIGN(final RFC8017PrivateKey1 privateKey, final byte[] message,
                                               final FIPS180 hashFunction, final Random rnd) {

        final byte[] modulusBytes = privateKey.n.toByteArray();
        final int emLen = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        final byte[] encoded = EMSA_PKCS1_V15_ENCODE(message, emLen, hashFunction);
        final BigInteger m = OS2IP(encoded);
        final BigInteger s = RSASP1(privateKey, m);

        return I2OSP(s, emLen);
    }

    /**
     * Verifies a digital signature of a message using RSASSA-PKCS1-V1_5.
     *
     * @param publicKey    the signer's public key
     * @param message      the message to be signed
     * @param signature    the signature to be verified
     * @param hashFunction the hash function
     * @return true if results are "consistent"; false if "inconsistent"
     */
    public static boolean RSASSA_PKCS1_V15_VERIFY(final RFC8017PublicKey publicKey, final byte[] message,
                                                  final byte[] signature, final FIPS180 hashFunction) {

        final byte[] modulusBytes = publicKey.n.toByteArray();
        final int emLen = (int) modulusBytes[0] == 0 ? modulusBytes.length - 1 : modulusBytes.length;

        boolean result;
        if (signature.length == emLen) {
            final BigInteger s = OS2IP(signature);
            final BigInteger m = RSAVP1(publicKey, s);
            final byte[] em = I2OSP(m, emLen);
            final byte[] emPrime = EMSA_PKCS1_V15_ENCODE(message, emLen, hashFunction);

            return Arrays.equals(em, emPrime);
        } else {
            result = false;
        }

        return result;
    }

    /**
     * Main method to test.
     *
     * @param args command-line arguments.
     */
    public static void main(final String... args) {

        final RFC8017KeyPairGenerator.KeyPair1 pair = RFC8017KeyPairGenerator.generateKeyPair();
        final FIPS180 hash = FIPS180SHA256.INSTANCE;

        try {
            final SecureRandom rnd = SecureRandom.getInstanceStrong();

            final byte[] message = "Hello World".getBytes(StandardCharsets.UTF_8);
            final byte[] label = "The label".getBytes(StandardCharsets.UTF_8);

            {
                final byte[] ciphertext1 = RSAES_OAEP_ENCRYPT(pair.pub(), message, label, hash, rnd);
                final byte[] recovered1 = RSAES_OAEP_DECRYPT(pair.priv(), ciphertext1, label, hash);
                final String recovered1String = new String(recovered1, StandardCharsets.UTF_8);
                Log.info("Recovered 1: ", recovered1String);
            }
            {
                final byte[] ciphertext2 = RSAES_PKCS1_V15_ENCRYPT(pair.pub(), message, rnd);
                final byte[] recovered2 = RSAES_PKCS1_V15_DECRYPT(pair.priv(), ciphertext2);
                final String recovered2String = new String(recovered2, StandardCharsets.UTF_8);
                Log.info("Recovered 2: ", recovered2String);
            }
            {
                final byte[] encoded = EMSA_PSS_ENCODE(message, 512, 20, hash, rnd);
                final boolean valid = EMSA_PSS_VERIFY(message, encoded, 20, hash);
                Log.info("Encoding verified: ", valid);
            }
            {
                final byte[] encoded = EMSA_PKCS1_V15_ENCODE(message, 512, hash);
                ;
                Log.info("Encoding complete");
            }
            {
                final byte[] signature = RSASSA_PSS_SIGN(pair.priv(), message, hash, rnd);
                final boolean valid = RSASSA_PSS_VERIFY(pair.pub(), message, signature, hash);
                Log.info("RSASSA-PSS Signature verified: ", valid);
            }
            {
                final byte[] signature = RSASSA_PKCS1_V15_SIGN(pair.priv(), message, hash, rnd);
                final boolean valid = RSASSA_PKCS1_V15_VERIFY(pair.pub(), message, signature, hash);
                Log.info("RSASSA-PKCS1_V15 Signature verified: ", valid);
            }

        } catch (NoSuchAlgorithmException e) {
            Log.warning("Failed to create secure random.");
        }
    }
}

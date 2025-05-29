package dev.mathops.text.fips;

/**
 * An implementation of the SHA-512/256 algorithm defined in
 * <a href='https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf'>FIPS 180-4 (Secure Hash Standard)</a>.
 *
 * <p>
 * This implementation assumes messages to be hashed will consist of a number of bits that is evenly divisible by 8. It
 * cannot handle messages larger than 2^31 bytes (2^34 bits).
 */
public final class FIPS180SHA512256 extends FIPS180 {

    /** The single instance of SHA-1 hash. */
    public static final FIPS180SHA512256 INSTANCE = new FIPS180SHA512256();

    /**
     * Constructs a new {@code FIPS180SHA512256}.
     */
    private FIPS180SHA512256() {

        super();
    }

    /**
     * Gets the block size, in bytes.
     *
     * @return the block size, in bytes
     */
    public int blockSize() {

        return 128;
    }

    /**
     * Gets the word size, in bytes.
     *
     * @return the word size, in bytes
     */
    public int wordSize() {

        return 8;
    }

    /**
     * Gets the digest size, in bytes.
     *
     * @return the digest size, in bytes
     */
    public int hashSize() {

        return 32;
    }

    /**
     * Computes a hash of a message.
     *
     * @param message the message
     * @return the hash
     */
    public byte[] hash(final byte[] message) {

        final byte[] padded = padToMultipleOf128(message);
        final int len = padded.length;
        final int numBlocks = len >> 7;

        long h0 = 0x22312194FC2BF72CL;
        long h1 = 0x9F555FA3C84C64C2L;
        long h2 = 0x2393B86B6F53B151L;
        long h3 = 0x963877195940EABDL;
        long h4 = 0x96283EE2A88EFFE3L;
        long h5 = 0xBE5E1E2553863992L;
        long h6 = 0x2B0199FC2C85B8AAL;
        long h7 = 0x0EB72DDC81C52CA2L;

        final long[] w = new long[80];

        int blockStart = 0;
        for (int i = 0; i < numBlocks; ++i) {

            // Prepare the message schedule
            int pos = blockStart;
            for (int t = 0; t < 16; ++t) {
                w[t] = eightBytesToWord64(padded, pos);
                pos += 8;
            }
            for (int t = 16; t < 80; ++t) {
                w[t] = lcSigma_1_512(w[t - 2]) + w[t - 7] + lcSigma_0_512(w[t - 15]) + w[t - 16];
            }

            long a = h0;
            long b = h1;
            long c = h2;
            long d = h3;
            long e = h4;
            long f = h5;
            long g = h6;
            long h = h7;

            for (int t = 0; t < 80; ++t) {
                final long t1 = h + ucSigma_1_512(e) + ch64(e, f, g) + FIPS180SHA512.K_SHA512[t] + w[t];
                final long t2 = ucSigma_0_512(a) + maj64(a, b, c);
                h = g;
                g = f;
                f = e;
                e = d + t1;
                d = c;
                c = b;
                b = a;
                a = t1 + t2;
            }

            h0 += a;
            h1 += b;
            h2 += c;
            h3 += d;
            h4 += e;
            h5 += f;
            h6 += g;
            h7 += h;

            blockStart += 128;
        }

        return new byte[]{
                (byte) (h0 >>> 56), (byte) (h0 >>> 48), (byte) (h0 >>> 40), (byte) (h0 >> 32),
                (byte) (h0 >>> 24), (byte) (h0 >>> 16), (byte) (h0 >>> 8), (byte) h0,
                (byte) (h1 >>> 56), (byte) (h1 >>> 48), (byte) (h1 >>> 40), (byte) (h1 >> 32),
                (byte) (h1 >>> 24), (byte) (h1 >>> 16), (byte) (h1 >>> 8), (byte) h1,
                (byte) (h2 >>> 56), (byte) (h2 >>> 48), (byte) (h2 >>> 40), (byte) (h2 >> 32),
                (byte) (h2 >>> 24), (byte) (h2 >>> 16), (byte) (h2 >>> 8), (byte) h2,
                (byte) (h3 >>> 56), (byte) (h3 >>> 48), (byte) (h3 >>> 40), (byte) (h3 >> 32),
                (byte) (h3 >>> 24), (byte) (h3 >>> 16), (byte) (h3 >>> 8), (byte) h3,};
    }
}


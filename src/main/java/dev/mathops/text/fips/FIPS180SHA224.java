package dev.mathops.text.fips;

/**
 * An implementation of the SHA-224 algorithm defined in
 * <a href='https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf'>FIPS 180-4 (Secure Hash Standard)</a>.
 *
 * <p>
 * This implementation assumes messages to be hashed will consist of a number of bits that is evenly divisible by 8. It
 * cannot handle messages larger than 2^31 bytes (2^34 bits).
 */
public final class FIPS180SHA224 extends FIPS180 {

    /** The single instance of SHA-1 hash. */
    public static final FIPS180SHA224 INSTANCE = new FIPS180SHA224();

    /**
     * Constructs a new {@code FIPS180SHA224}.
     */
    private FIPS180SHA224() {

        super();
    }

    /**
     * Gets the block size, in bytes.
     *
     * @return the block size, in bytes
     */
    public int blockSize() {

        return 64;
    }

    /**
     * Gets the word size, in bytes.
     *
     * @return the word size, in bytes
     */
    public int wordSize() {

        return 4;
    }

    /**
     * Gets the digest size, in bytes.
     *
     * @return the digest size, in bytes
     */
    public int digestSize() {

        return 28;
    }

    /**
     * Computes a hash of a message.
     *
     * @param message the message
     * @return the hash
     */
    public byte[] hash(final byte[] message) {

        final byte[] padded = padToMultipleOf64(message);
        final int len = padded.length;
        final int numBlocks = len >> 6;

        int h0 = 0xc1059ed8;
        int h1 = 0x367cd507;
        int h2 = 0x3070dd17;
        int h3 = 0xf70e5939;
        int h4 = 0xffc00b31;
        int h5 = 0x68581511;
        int h6 = 0x64f98fa7;
        int h7 = 0xbefa4fa4;

        final int[] w = new int[64];

        int blockStart = 0;
        for (int i = 0; i < numBlocks; ++i) {

            // Prepare the message schedule
            int pos = blockStart;
            for (int t = 0; t < 16; ++t) {
                w[t] = fourBytesToWord32(padded, pos);
                pos += 4;
            }
            for (int t = 16; t < 64; ++t) {
                w[t] = lcSigma_1_256(w[t - 2]) + w[t - 7] + lcSigma_0_256(w[t - 15]) + w[t - 16];
            }

            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;
            int f = h5;
            int g = h6;
            int h = h7;

            for (int t = 0; t < 64; ++t) {
                final int t1 = h + ucSigma_1_256(e) + ch32(e, f, g) + FIPS180SHA256.K_SHA256[t] + w[t];
                final int t2 = ucSigma_0_256(a) + maj32(a, b, c);
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

            blockStart += 64;
        }

        return new byte[]{
                (byte) (h0 >>> 24), (byte) (h0 >>> 16), (byte) (h0 >>> 8), (byte) h0,
                (byte) (h1 >>> 24), (byte) (h1 >>> 16), (byte) (h1 >>> 8), (byte) h1,
                (byte) (h2 >>> 24), (byte) (h2 >>> 16), (byte) (h2 >>> 8), (byte) h2,
                (byte) (h3 >>> 24), (byte) (h3 >>> 16), (byte) (h3 >>> 8), (byte) h3,
                (byte) (h4 >>> 24), (byte) (h4 >>> 16), (byte) (h4 >>> 8), (byte) h4,
                (byte) (h5 >>> 24), (byte) (h5 >>> 16), (byte) (h5 >>> 8), (byte) h5,
                (byte) (h6 >>> 24), (byte) (h6 >>> 16), (byte) (h6 >>> 8), (byte) h6};
    }
}


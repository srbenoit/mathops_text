package dev.mathops.text.fips;

/**
 * An implementation of the SHA-1 algorithm defined in
 * <a href='https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf'>FIPS 180-4 (Secure Hash Standard)</a>.
 *
 * <p>
 * This implementation assumes messages to be hashed will consist of a number of bits that is evenly divisible by 8. It
 * cannot handle messages larger than 2^31 bytes (2^34 bits).
 */
public final class FIPS180SHA1 extends FIPS180 {

    /** Constants used for SHA-1. */
    private static final int[] K_SHA1 = {0x5a827999, 0x6ed9eba1, 0x8f1bbcdc, 0xca62c1d6};

    /** The single instance of SHA-1 hash. */
    public static final FIPS180SHA1 INSTANCE = new FIPS180SHA1();

    /**
     * Constructs a new {@code FIPS180SHA1}.
     */
    private FIPS180SHA1() {

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
    public int hashSize() {

        return 20;
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

        int h0 = 0x67452301;
        int h1 = 0xefcdab89;
        int h2 = 0x98badcfe;
        int h3 = 0x10325476;
        int h4 = 0xc3d2e1f0;

        final int[] w = new int[80];

        int blockStart = 0;
        for (int i = 0; i < numBlocks; ++i) {

            // Prepare the message schedule
            int pos = blockStart;
            for (int t = 0; t < 16; ++t) {
                w[t] = fourBytesToWord32(padded, pos);
                pos += 4;
            }
            for (int t = 16; t < 80; ++t) {
                w[t] = Integer.rotateLeft(w[t - 3] ^ w[t - 8] ^ w[t - 14] ^ w[t - 16], 1);
            }

            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;

            for (int j = 0; j < 20; ++j) {
                final int t = Integer.rotateLeft(a, 5) + ch32(b, c, d) + e + K_SHA1[0] + w[j];
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = t;
            }
            for (int j = 20; j < 40; ++j) {
                final int t = Integer.rotateLeft(a, 5) + parity32(b, c, d) + e + K_SHA1[1] + w[j];
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = t;
            }
            for (int j = 40; j < 60; ++j) {
                final int t = Integer.rotateLeft(a, 5) + maj32(b, c, d) + e + K_SHA1[2] + w[j];
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = t;
            }
            for (int j = 60; j < 80; ++j) {
                final int t = Integer.rotateLeft(a, 5) + parity32(b, c, d) + e + K_SHA1[3] + w[j];
                e = d;
                d = c;
                c = Integer.rotateLeft(b, 30);
                b = a;
                a = t;
            }

            h0 += a;
            h1 += b;
            h2 += c;
            h3 += d;
            h4 += e;

            blockStart += 64;
        }

        return new byte[]{
                (byte) (h0 >>> 24), (byte) (h0 >>> 16), (byte) (h0 >>> 8), (byte) h0,
                (byte) (h1 >>> 24), (byte) (h1 >>> 16), (byte) (h1 >>> 8), (byte) h1,
                (byte) (h2 >>> 24), (byte) (h2 >>> 16), (byte) (h2 >>> 8), (byte) h2,
                (byte) (h3 >>> 24), (byte) (h3 >>> 16), (byte) (h3 >>> 8), (byte) h3,
                (byte) (h4 >>> 24), (byte) (h4 >>> 16), (byte) (h4 >>> 8), (byte) h4
        };
    }
}


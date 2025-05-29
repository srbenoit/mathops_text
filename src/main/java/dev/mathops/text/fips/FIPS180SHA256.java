package dev.mathops.text.fips;

/**
 * An implementation of the SHA-256 algorithm defined in
 * <a href='https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf'>FIPS 180-4 (Secure Hash Standard)</a>.
 *
 * <p>
 * This implementation assumes messages to be hashed will consist of a number of bits that is evenly divisible by 8. It
 * cannot handle messages larger than 2^31 bytes (2^34 bits).
 */
public final class FIPS180SHA256 extends FIPS180 {

    /** Constants used for SHA-224 and SHA-256. */
     static final int[] K_SHA256 = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2};

    /** The single instance of SHA-1 hash. */
    public static final FIPS180SHA256 INSTANCE = new FIPS180SHA256();

    /**
     * Constructs a new {@code FIPS180SHA256}.
     */
    private FIPS180SHA256() {

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

        return 32;
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

        int h0 = 0x6a09e667;
        int h1 = 0xbb67ae85;
        int h2 = 0x3c6ef372;
        int h3 = 0xa54ff53a;
        int h4 = 0x510e527f;
        int h5 = 0x9b05688c;
        int h6 = 0x1f83d9ab;
        int h7 = 0x5be0cd19;

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
                final int t1 = h + ucSigma_1_256(e) + ch32(e, f, g) + K_SHA256[t] + w[t];
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
                (byte) (h6 >>> 24), (byte) (h6 >>> 16), (byte) (h6 >>> 8), (byte) h6,
                (byte) (h7 >>> 24), (byte) (h7 >>> 16), (byte) (h7 >>> 8), (byte) h7};
    }
}


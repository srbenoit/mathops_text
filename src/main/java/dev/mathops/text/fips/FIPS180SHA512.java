package dev.mathops.text.fips;

/**
 * An implementation of the SHA-512 algorithm defined in
 * <a href='https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf'>FIPS 180-4 (Secure Hash Standard)</a>.
 *
 * <p>
 * This implementation assumes messages to be hashed will consist of a number of bits that is evenly divisible by 8. It
 * cannot handle messages larger than 2^31 bytes (2^34 bits).
 */
public final class FIPS180SHA512 extends FIPS180 {

    /** Constants used for SHA-384, SHA-512, SHA-512/224, and SHA-512/256. */
    static final long[] K_SHA512 = {
            0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
            0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
            0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
            0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
            0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
            0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
            0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
            0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
            0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
            0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
            0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
            0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
            0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
            0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
            0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
            0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
            0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
            0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
            0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
            0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L};

    /** The single instance of SHA-1 hash. */
    public static final FIPS180SHA512 INSTANCE = new FIPS180SHA512();

    /**
     * Constructs a new {@code FIPS180SHA512}.
     */
    private FIPS180SHA512() {

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
    public int digestSize() {

        return 64;
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

        long h0 = 0x6a09e667f3bcc908L;
        long h1 = 0xbb67ae8584caa73bL;
        long h2 = 0x3c6ef372fe94f82bL;
        long h3 = 0xa54ff53a5f1d36f1L;
        long h4 = 0x510e527fade682d1L;
        long h5 = 0x9b05688c2b3e6c1fL;
        long h6 = 0x1f83d9abfb41bd6bL;
        long h7 = 0x5be0cd19137e2179L;

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
                final long t1 = h + ucSigma_1_512(e) + ch64(e, f, g) + K_SHA512[t] + w[t];
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
                (byte) (h3 >>> 24), (byte) (h3 >>> 16), (byte) (h3 >>> 8), (byte) h3,
                (byte) (h4 >>> 56), (byte) (h4 >>> 48), (byte) (h4 >>> 40), (byte) (h4 >> 32),
                (byte) (h4 >>> 24), (byte) (h4 >>> 16), (byte) (h4 >>> 8), (byte) h4,
                (byte) (h5 >>> 56), (byte) (h5 >>> 48), (byte) (h5 >>> 40), (byte) (h5 >> 32),
                (byte) (h5 >>> 24), (byte) (h5 >>> 16), (byte) (h5 >>> 8), (byte) h5,
                (byte) (h6 >>> 56), (byte) (h6 >>> 48), (byte) (h6 >>> 40), (byte) (h6 >> 32),
                (byte) (h6 >>> 24), (byte) (h6 >>> 16), (byte) (h6 >>> 8), (byte) h6,
                (byte) (h7 >>> 56), (byte) (h7 >>> 48), (byte) (h7 >>> 40), (byte) (h7 >> 32),
                (byte) (h7 >>> 24), (byte) (h7 >>> 16), (byte) (h7 >>> 8), (byte) h7};
    }
}


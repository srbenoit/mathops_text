package dev.mathops.text.fips;

/**
 * An implementation of <a href='https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf'>FIPS 180-4 (Secure Hash
 * Standard)</a>.
 *
 * <p>
 * This implementation assumes messages to be hashed will consist of a number of bits that is evenly divisible by 8.  It
 * cannot handle messages larger than 2^31 bytes (2^34 bits).
 */
public abstract class FIPS180 {

    /**
     * Gets the block size, in bytes.
     *
     * @return the block size, in bytes
     */
    public abstract int blockSize();

    /**
     * Gets the word size, in bytes.
     *
     * @return the word size, in bytes
     */
    public abstract int wordSize();

    /**
     * Gets the hash size, in bytes.
     *
     * @return the hash size, in bytes
     */
    public abstract int hashSize();

    /**
     * Computes a hash of a message.
     *
     * @param message the message
     * @return the hash
     */
    public abstract byte[] hash(byte[] message);

    /**
     * Performs the "Ch(x,y,z)" function using 32-bit words.
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @return the function result
     */
    public static int ch32(int x, int y, int z) {

        return (x & y) ^ ((~x) & z);
    }

    /**
     * Performs the "Ch(x,y,z)" function using 64-bit words.
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @return the function result
     */
    public static long ch64(long x, long y, long z) {

        return (x & y) ^ ((~x) & z);
    }

    /**
     * Performs the "Parity(x,y,z)" function using 32-bit words.
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @return the function result
     */
    public static int parity32(int x, int y, int z) {

        return x ^ y ^ z;
    }

    /**
     * Performs the "Parity(x,y,z)" function using 64-bit words.
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @return the function result
     */
    public static long parity64(long x, long y, long z) {

        return x ^ y ^ z;
    }

    /**
     * Performs the "Maj(x,y,z)" function using 32-bit words.
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @return the function result
     */
    public static int maj32(int x, int y, int z) {

        return (x & y) ^ (x & z) ^ (y & z);
    }

    /**
     * Performs the "Maj(x,y,z)" function using 64-bit words.
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     * @return the function result
     */
    public static long maj64(long x, long y, long z) {

        return (x & y) ^ (x & z) ^ (y & z);
    }

    /**
     * Implements a right shift of a 32-bit word by some number of bits.
     *
     * @param value the word to be shifted
     * @param bits  the number of bits
     * @return the result
     */
    public static int shr32(int value, int bits) {

        return value >>> bits;
    }

    /**
     * Implements a right shift of a 32-bit word by some number of bits.
     *
     * @param value the word to be shifted
     * @param bits  the number of bits
     * @return the result
     */
    public static long shr64(long value, int bits) {

        return value >>> bits;
    }

    /**
     * Implements the uppercase Sigma (0 to 256) function for 32-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static int ucSigma_0_256(int x) {

        return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13) ^ Integer.rotateRight(x, 22);
    }

    /**
     * Implements the uppercase Sigma (1 to 256) function for 32-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static int ucSigma_1_256(int x) {

        return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11) ^ Integer.rotateRight(x, 25);
    }

    /**
     * Implements the lowercase sigma (0 to 256) function for 32-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static int lcSigma_0_256(int x) {

        return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18) ^ shr32(x, 3);
    }

    /**
     * Implements the lowercase sigma (1 to 256) function for 32-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static int lcSigma_1_256(int x) {

        return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19) ^ shr32(x, 10);
    }

    /**
     * Implements the uppercase Sigma (0 to 512) function for 64-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static long ucSigma_0_512(long x) {

        return Long.rotateRight(x, 28) ^ Long.rotateRight(x, 34) ^ Long.rotateRight(x, 39);
    }

    /**
     * Implements the uppercase Sigma (1 to 251256) function for 64-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static long ucSigma_1_512(long x) {

        return Long.rotateRight(x, 14) ^ Long.rotateRight(x, 18) ^ Long.rotateRight(x, 41);
    }

    /**
     * Implements the lowercase sigma (0 to 512) function for 64-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static long lcSigma_0_512(long x) {

        return Long.rotateRight(x, 1) ^ Long.rotateRight(x, 8) ^ shr64(x, 7);
    }

    /**
     * Implements the lowercase sigma (1 to 512) function for 64-bit words.
     *
     * @param x the input value
     * @return the result
     */
    public static long lcSigma_1_512(long x) {

        return Long.rotateRight(x, 19) ^ Long.rotateRight(x, 61) ^ shr64(x, 6);
    }

    /**
     * Pads a message so it will be a multiple of 64 bytes in length.
     *
     * @param input the input message
     * @return the padded message (a multiple of 64 bytes in length)
     */
    public static byte[] padToMultipleOf64(final byte[] input) {

        final int inLen = input.length;
        final int minPaddedLen = inLen + 9;

        final int numBlocks = (minPaddedLen + 63) >> 6;
        final int finalSize = numBlocks * 64;

        final byte[] result = new byte[finalSize];
        System.arraycopy(input, 0, result, 0, inLen);
        result[inLen] = (byte) 0x80;
        final long inBits = (long) inLen << 3;

        result[finalSize - 5] = (byte) ((inBits >> 32) & 0xFF);
        result[finalSize - 4] = (byte) ((inBits >> 24) & 0xFF);
        result[finalSize - 3] = (byte) ((inBits >> 16) & 0xFF);
        result[finalSize - 2] = (byte) ((inBits >> 8) & 0xFF);
        result[finalSize - 1] = (byte) (inBits & 0xFF);

        return result;
    }

    /**
     * Pads a message so it will be a multiple of 128 bytes in length.
     *
     * @param input the input message
     * @return the padded message (a multiple of 128 bytes in length)
     */
    public static byte[] padToMultipleOf128(final byte[] input) {

        final int inLen = input.length;
        final int minPaddedLen = inLen + 17;

        final int numBlocks = (minPaddedLen + 127) >> 7;
        final int finalSize = numBlocks * 128;

        final byte[] result = new byte[finalSize];
        System.arraycopy(input, 0, result, 0, inLen);
        result[inLen] = (byte) 0x80;
        final long inBits = (long) inLen << 3;
        result[finalSize - 5] = (byte) ((inBits >> 32) & 0xFF);
        result[finalSize - 4] = (byte) ((inBits >> 24) & 0xFF);
        result[finalSize - 3] = (byte) ((inBits >> 16) & 0xFF);
        result[finalSize - 2] = (byte) ((inBits >> 8) & 0xFF);
        result[finalSize - 1] = (byte) (inBits & 0xFF);

        return result;
    }

    /**
     * Extract 4 bytes from a byte array and convert it to an integer, treating the first (left-most) byte as the most
     * significant.
     *
     * @param data       the data array
     * @param firstIndex the first byte index
     * @return the integer
     */
    public static int fourBytesToWord32(final byte[] data, final int firstIndex) {

        final int data0 = (int) data[firstIndex] & 0xFF;
        final int data1 = (int) data[firstIndex + 1] & 0xFF;
        final int data2 = (int) data[firstIndex + 2] & 0xFF;
        final int data3 = (int) data[firstIndex + 3] & 0xFF;

        return (data0 << 24) | (data1 << 16) | (data2 << 8) | data3;
    }

    /**
     * Extract 8 bytes from a byte array and convert it to a long, treating the first (left-most) byte as the most
     * significant.
     *
     * @param data       the data array
     * @param firstIndex the first byte index
     * @return the long
     */
    public static long eightBytesToWord64(final byte[] data, final int firstIndex) {

        final long data0 = (long) data[firstIndex] & 0xFFL;
        final long data1 = (long) data[firstIndex + 1] & 0xFFL;
        final long data2 = (long) data[firstIndex + 2] & 0xFFL;
        final long data3 = (long) data[firstIndex + 3] & 0xFFL;
        final long data4 = (long) data[firstIndex + 4] & 0xFFL;
        final long data5 = (long) data[firstIndex + 5] & 0xFFL;
        final long data6 = (long) data[firstIndex + 6] & 0xFFL;
        final long data7 = (long) data[firstIndex + 7] & 0xFFL;

        return (data0 << 56) | (data1 << 48) | (data2 << 40) | (data3 << 32) | (data4 << 24) | (data5 << 16)
               | (data6 << 8) | data7;
    }

}

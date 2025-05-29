package dev.mathops.text.fips;

import dev.mathops.commons.Rnd;
import dev.mathops.commons.Seed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@code FIPS180} class.
 */
public class TestFIPS180 {

    /**
     * A test case.
     */
    @Test
    @DisplayName("ch32")
    void test001() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();
            final int y = rnd.nextInt();
            final int z = rnd.nextInt();

            final int xAndY = x & y;
            final int notX = ~x;
            final int notXAndZ = notX & z;
            final int expect = xAndY ^ notXAndZ;

            int result = FIPS180.ch32(x, y, z);

            assertEquals(expect, result, "Invalid value from ch32 for x=0x" + Integer.toHexString(x) + " y=0x"
                                         + Integer.toHexString(y) + " z=0x" + Integer.toHexString(z));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("ch64")
    void test002() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();
            final long y = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();
            final long z = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();

            final long xAndY = x & y;
            final long notX = ~x;
            final long notXAndZ = notX & z;
            final long expect = xAndY ^ notXAndZ;

            long result = FIPS180.ch64(x, y, z);

            assertEquals(expect, result, "Invalid value from ch64 for x=0x" + Long.toHexString(x) + " y=0x"
                                         + Long.toHexString(y) + " z=0x" + Long.toHexString(z));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("parity32")
    void test003() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();
            final int y = rnd.nextInt();
            final int z = rnd.nextInt();

            final int expect = x ^ y ^ z;

            int result = FIPS180.parity32(x, y, z);

            assertEquals(expect, result, "Invalid value from parity32 for x=0x" + Integer.toHexString(x) + " y=0x"
                                         + Integer.toHexString(y) + " z=0x" + Integer.toHexString(z));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("parity64")
    void test004() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();
            final long y = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();
            final long z = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();

            final long expect = x ^ y ^ z;

            long result = FIPS180.parity64(x, y, z);

            assertEquals(expect, result, "Invalid value from parity64 for x=0x" + Long.toHexString(x) + " y=0x"
                                         + Long.toHexString(y) + " z=0x" + Long.toHexString(z));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("maj32")
    void test005() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();
            final int y = rnd.nextInt();
            final int z = rnd.nextInt();

            final int xAndY = x & y;
            final int xAndZ = x & z;
            final int yAndZ = y & z;
            final int expect = xAndY ^ xAndZ ^ yAndZ;

            int result = FIPS180.maj32(x, y, z);

            assertEquals(expect, result, "Invalid value from maj32 for x=0x" + Integer.toHexString(x) + " y=0x"
                                         + Integer.toHexString(y) + " z=0x" + Integer.toHexString(z));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("maj64")
    void test006() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();
            final long y = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();
            final long z = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();

            final long xAndY = x & y;
            final long xAndZ = x & z;
            final long yAndZ = y & z;
            final long expect = xAndY ^ xAndZ ^ yAndZ;

            long result = FIPS180.maj64(x, y, z);

            assertEquals(expect, result, "Invalid value from maj64 for x=0x" + Long.toHexString(x) + " y=0x"
                                         + Long.toHexString(y) + " z=0x" + Long.toHexString(z));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("shr32")
    void test007() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();
            final int numBits = 1 + rnd.nextInt(30);
            int expect = x >>> numBits;

            int result = FIPS180.shr32(x, numBits);

            assertEquals(expect, result, "Invalid value from shr32 for x=0x" + Integer.toHexString(x) + " numBits="
                                         + numBits);
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("shr64")
    void test008() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();
            final int numBits = 1 + rnd.nextInt(62);
            long expect = x >>> numBits;

            long result = FIPS180.shr64(x, numBits);

            assertEquals(expect, result, "Invalid value from shr64 for x=0x" + Long.toHexString(x) + " numBits="
                                         + numBits);
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("ucSigma_0_256")
    void test013() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();

            final int rotr2 = Integer.rotateRight(x, 2);
            final int rotr13 = Integer.rotateRight(x, 13);
            final int rotr22 = Integer.rotateRight(x, 22);
            final int expect = rotr2 ^ rotr13 ^ rotr22;

            int result = FIPS180.ucSigma_0_256(x);

            assertEquals(expect, result, "Invalid value from ucSigma_0_256 for x=0x" + Integer.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("ucSigma_1_256")
    void test014() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();

            final int rotr2 = Integer.rotateRight(x, 6);
            final int rotr13 = Integer.rotateRight(x, 11);
            final int rotr22 = Integer.rotateRight(x, 25);
            final int expect = rotr2 ^ rotr13 ^ rotr22;

            int result = FIPS180.ucSigma_1_256(x);

            assertEquals(expect, result, "Invalid value from ucSigma_1_256 for x=0x" + Integer.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("lcSigma_0_256")
    void test015() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();

            final int rotr7 = Integer.rotateRight(x, 7);
            final int rotr18 = Integer.rotateRight(x, 18);
            final int shr3 = FIPS180.shr32(x, 3);
            final int expect = rotr7 ^ rotr18 ^ shr3;

            int result = FIPS180.lcSigma_0_256(x);

            assertEquals(expect, result, "Invalid value from lcSigma_0_256 for x=0x" + Integer.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("lcSigma_1_256")
    void test016() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final int x = rnd.nextInt();

            final int rotr17 = Integer.rotateRight(x, 17);
            final int rotr19 = Integer.rotateRight(x, 19);
            final int shr10 = FIPS180.shr32(x, 10);
            final int expect = rotr17 ^ rotr19 ^ shr10;

            int result = FIPS180.lcSigma_1_256(x);

            assertEquals(expect, result, "Invalid value from lcSigma_1_256 for x=0x" + Integer.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("ucSigma_0_512")
    void test017() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();

            final long rotr28 = Long.rotateRight(x, 28);
            final long rotr34 = Long.rotateRight(x, 34);
            final long rotr39 = Long.rotateRight(x, 39);
            final long expect = rotr28 ^ rotr34 ^ rotr39;

            long result = FIPS180.ucSigma_0_512(x);

            assertEquals(expect, result, "Invalid value from ucSigma_0_512 for x=0x" + Long.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("ucSigma_1_512")
    void test018() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();

            final long rotr14 = Long.rotateRight(x, 14);
            final long rotr18 = Long.rotateRight(x, 18);
            final long rotr41 = Long.rotateRight(x, 41);
            final long expect = rotr14 ^ rotr18 ^ rotr41;

            long result = FIPS180.ucSigma_1_512(x);

            assertEquals(expect, result, "Invalid value from ucSigma_1_512 for x=0x" + Long.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("lcSigma_0_512")
    void test019() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();

            final long rotr1 = Long.rotateRight(x, 1);
            final long rotr8 = Long.rotateRight(x, 8);
            final long shr7 = FIPS180.shr64(x, 7);
            final long expect = rotr1 ^ rotr8 ^ shr7;

            long result = FIPS180.lcSigma_0_512(x);

            assertEquals(expect, result, "Invalid value from lcSigma_0_512 for x=0x" + Long.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("lcSigma_1_512")
    void test020() {

        final Seed seed = new Seed(System.nanoTime(), System.currentTimeMillis());
        final Rnd rnd = new Rnd(seed);

        for (int i = 0; i < 100; ++i) {
            final long x = (long) rnd.nextInt() << 32 + (long) rnd.nextInt();

            final long rotr19 = Long.rotateRight(x, 19);
            final long rotr61 = Long.rotateRight(x, 61);
            final long shr6 = FIPS180.shr64(x, 6);
            final long expect = rotr19 ^ rotr61 ^ shr6;

            long result = FIPS180.lcSigma_1_512(x);

            assertEquals(expect, result, "Invalid value from lcSigma_1_512 for x=0x" + Long.toHexString(x));
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("padToMultipleOf64")
    void test021() {

        for (int origLen = 1; origLen < 10000; ++origLen) {
            final byte[] data = new byte[origLen];
            for (int i = 0; i < origLen; ++i) {
                data[i] = (byte) (i & 0xFF);
            }

            final byte[] padded = FIPS180.padToMultipleOf64(data);
            final int paddedLen = padded.length;

            // Padding should add 9 bytes then pad to next multiple of 64 bytes
            final int lengthToPad = origLen + 9;
            final int numBlocks = (lengthToPad + 63) / 64;
            final int expectLen = numBlocks * 64;

            assertEquals(expectLen, paddedLen,
                    "Bad padded size: orig=" + origLen + " padded=" + paddedLen + " expect=" + expectLen);

            for (int i = 0; i < origLen; ++i) {
                assertEquals(data[i], padded[i], "Data byte not copied into padded bytes at position " + i);
            }
            assertEquals((byte) 0x80, padded[origLen], "Byte after padded data was not 0x80");

            final int origBits = origLen * 8;
            final byte size0 = (byte) ((origBits >> 24) & 0xFF);
            final byte size1 = (byte) ((origBits >> 16) & 0xFF);
            final byte size2 = (byte) ((origBits >> 8) & 0xFF);
            final byte size3 = (byte) (origBits & 0xFF);

            assertEquals(size0, padded[paddedLen - 4], "Invalid size byte 0 at end of padded block.");
            assertEquals(size1, padded[paddedLen - 3], "Invalid size byte 1 at end of padded block.");
            assertEquals(size2, padded[paddedLen - 2], "Invalid size byte 2 at end of padded block.");
            assertEquals(size3, padded[paddedLen - 1], "Invalid size byte 3 at end of padded block.");
        }
    }

    /**
     * A test case.
     */
    @Test
    @DisplayName("padToMultipleOf128")
    void test022() {

        for (int origLen = 1; origLen < 10000; ++origLen) {
            final byte[] data = new byte[origLen];
            for (int i = 0; i < origLen; ++i) {
                data[i] = (byte) (i & 0xFF);
            }

            final byte[] padded = FIPS180.padToMultipleOf128(data);
            final int paddedLen = padded.length;

            // Padding should add 17 bytes then pad to next multiple of 128 bytes
            final int lengthToPad = origLen + 17;
            final int numBlocks = (lengthToPad + 127) / 128;
            final int expectLen = numBlocks * 128;

            assertEquals(expectLen, paddedLen,
                    "Bad padded size: orig=" + origLen + " padded=" + paddedLen + " expect=" + expectLen);

            for (int i = 0; i < origLen; ++i) {
                assertEquals(data[i], padded[i], "Data byte not copied into padded bytes at position " + i);
            }
            assertEquals((byte) 0x80, padded[origLen], "Byte after padded data was not 0x80");

            final int origBits = origLen * 8;
            final byte size0 = (byte) ((origBits >> 24) & 0xFF);
            final byte size1 = (byte) ((origBits >> 16) & 0xFF);
            final byte size2 = (byte) ((origBits >> 8) & 0xFF);
            final byte size3 = (byte) (origBits & 0xFF);

            assertEquals(size0, padded[paddedLen - 4], "Invalid size byte 0 at end of padded block.");
            assertEquals(size1, padded[paddedLen - 3], "Invalid size byte 1 at end of padded block.");
            assertEquals(size2, padded[paddedLen - 2], "Invalid size byte 2 at end of padded block.");
            assertEquals(size3, padded[paddedLen - 1], "Invalid size byte 3 at end of padded block.");
        }
    }
}

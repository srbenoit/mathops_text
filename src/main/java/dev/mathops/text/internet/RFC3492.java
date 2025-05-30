package dev.mathops.text.internet;

import dev.mathops.text.IUSVSequence;
import dev.mathops.text.USVQueue;
import dev.mathops.text.builder.CharHtmlBuilder;

/**
 * An implementation of <a href='https://datatracker.ietf.org/doc/html/rfc3492'>RFC 3492 - Punycode: A Bootstring
 * encoding of Unicode for Internationalized Domain Names in Applications (IDNA)</a>.
 *
 * <p>
 * This class only produces lowercase Punycode encodings, as needed by RFC 5890.
 */
public enum RFC3492 {
    ;

    /** A parameter for the Punycode implementation of Bootstring. */
    private static final int PUNYCODE_BASE = 36;

    /** A parameter for the Punycode implementation of Bootstring. */
    private static final int PUNYCODE_TMIN = 1;

    /** A parameter for the Punycode implementation of Bootstring. */
    private static final int PUNYCODE_TMAX = 26;

    /** A parameter for the Punycode implementation of Bootstring. */
    private static final int PUNYCODE_SKEW = 38;

    /** A parameter for the Punycode implementation of Bootstring. */
    private static final int PUNYCODE_DAMP = 700;

    /** A parameter for the Punycode implementation of Bootstring. */
    private static final int PUNYCODE_INITIAL_BIAS = 72;

    /** A parameter for the Punycode implementation of Bootstring. */
    private static final int PUNYCODE_INITIAL_N = 128;

    /** The delimiter. */
    private static final char DELIMITER = '-';


    /**
     * Converts Unicode to Punycode.  The input is represented as an array of Unicode code points (not code units;
     * surrogate pairs are not allowed), and the output will be represented as an array of ASCII code points.  The
     * output string is *not* null-terminated; it will contain zeros if and only if the input contains zeros.  (Of
     * course the caller can leave room for a terminator and add one if needed.)  The input_length is the number of code
     * points in the input.  The output_length is an in/out argument: the caller passes in the maximum number of code
     * points that it can receive, and on successful return it will contain the number of code points actually output.
     * The case_flags array holds input_length boolean values, where nonzero suggests that the corresponding Unicode
     * character be forced to uppercase after being decoded (if possible), and zero suggests that  it be forced to
     * lowercase (if possible).  ASCII code points are encoded literally, except that ASCII letters are forced to
     * uppercase or lowercase according to the corresponding uppercase flags.  If case_flags is a null pointer then
     * ASCII letters are left as they are, and other code points are treated as if their uppercase flags were zero. The
     * return value can be any of the EPunycodeStatus values except BAD_INPUT; if not SUCCESS, then output might contain
     * garbage.
     *
     * @param input  the string to encode
     * @param output the queue to which to add encoded output
     * @return the status
     */
    public static EPunycodeStatus punycodeEncode(final IUSVSequence input, final CharHtmlBuilder output) {

        int n;
        int delta;
        int h;
        int b;
        int out;
        int bias;
        int j;
        int m;
        int q;
        int k;
        int t;

        /* Initialize the state: */

        n = PUNYCODE_INITIAL_N;
        delta = 0;
        out = 0;
        bias = PUNYCODE_INITIAL_BIAS;

        /* Handle the basic code points: */
        for (j = 0; j < input.size(); ++j) {
            final int cp = input.get(j);
            if (isBasic(cp)) {
                output.addChar((char) cp);
                ++out;
            }
        }

        /* h is the number of code points that have been handled, b is the  */
        /* number of basic code points, and out is the number of characters */
        /* that have been output.                                           */

        h = out;
        b = out;

        if (b > 0) {
            output.addChar(DELIMITER);
        }

        while (h < input.size()) {
            m = Integer.MAX_VALUE;
            for (j = 0; j < input.size(); ++j) {
                final int cp = input.get(j);
                if (cp >= n && cp < m) {
                    m = cp;
                }
            }

            if (m - n > (Integer.MAX_VALUE - delta) / (h + 1)) {
                return EPunycodeStatus.OVERFLOW;
            }

            delta += (m - n) * (h + 1);
            n = m;

            for (j = 0; j < input.size(); ++j) {
                int cp = input.get(j);
                if (cp < n) {
                    ++delta;
                    if (delta == 0) {
                        return EPunycodeStatus.OVERFLOW;
                    }
                }

                if (cp == n) {
                    q = delta;
                    for (k = PUNYCODE_BASE; ; k += PUNYCODE_BASE) {

                        t = k <= bias ? PUNYCODE_TMIN : k >= bias + PUNYCODE_TMAX ? PUNYCODE_TMAX : k - bias;
                        if (q < t) {
                            break;
                        }
                        final char encoded = encodeDigit(t + (q - t) % (PUNYCODE_BASE - t));
                        output.addChar(encoded);
                        ++out;
                        q = (q - t) / (PUNYCODE_BASE - t);
                    }

                    final char encoded = encodeDigit(q);
                    output.addChar(encoded);
                    ++out;
                    bias = adapt(delta, h + 1, h == b);
                    delta = 0;
                    ++h;
                }
            }

            ++delta;
            ++n;
        }

        return EPunycodeStatus.SUCCESS;
    }

    /**
     * Converts Punycode to Unicode.  The input is represented as an array of ASCII code points, and the output will be
     * represented as an array of Unicode code points.
     *
     * <p>
     * The return value can be any of the EPunycodeStatus values; if not SUCCESS, then output might contain garbage. On
     * SUCCESS, the decoder will never need to write an output of length greater than the input length  because of how
     * the encoding is defined.
     *
     * @param input  the string to decode
     * @param output the queue to which to add decoded output
     * @return the status
     */
    public static EPunycodeStatus punycodeDecode(final CharSequence input, final USVQueue output) {

        final int[] decoded = new int[input.length()];
        int n = PUNYCODE_INITIAL_N;
        int bias = PUNYCODE_INITIAL_BIAS;

        /* Handle the basic code points:  Let b be the number of input code */
        /* points before the last delimiter, or 0 if there is none, then    */
        /* copy the first b code points to the output.                      */

        int b = 0;
        for (int j = 0; j < input.length(); ++j) {
            if ((int) input.charAt(j) == (int) DELIMITER) {
                b = j;
            }
        }

        int out = 0;
        for (int j = 0; j < b; ++j) {
            final int ch = (int) input.charAt(j);
            if (!isBasic(ch)) {
                return EPunycodeStatus.BAD_INPUT;
            }
            decoded[out] = ch;
            ++out;
        }

        /* Main decoding loop:  Start just after the last delimiter if any  */
        /* basic code points were copied; start at the beginning otherwise. */

        int i = 0;
        for (int in = b > 0 ? b + 1 : 0; in < input.length(); ++out) {

            final int oldi = i;
            int w = 1;
            for (int k = PUNYCODE_BASE; ; k += PUNYCODE_BASE) {
                if (in >= input.length()) {
                    return EPunycodeStatus.BAD_INPUT;
                }
                final char cp = input.charAt(in);
                final int digit = decodeDigit(cp);
                ++in;
                if (digit >= PUNYCODE_BASE) {
                    return EPunycodeStatus.BAD_INPUT;
                }
                if (digit > (Integer.MAX_VALUE - i) / w) {
                    return EPunycodeStatus.OVERFLOW;
                }
                i += digit * w;
                final int t = k <= bias ? PUNYCODE_TMIN :
                        k >= bias + PUNYCODE_TMAX ? PUNYCODE_TMAX : k - bias;
                if (digit < t) {
                    break;
                }
                if (w > Integer.MAX_VALUE / (PUNYCODE_BASE - t)) {
                    return EPunycodeStatus.OVERFLOW;
                }
                w *= (PUNYCODE_BASE - t);
            }

            bias = adapt(i - oldi, out + 1, oldi == 0);

            if (i / (out + 1) > Integer.MAX_VALUE - n) {
                return EPunycodeStatus.OVERFLOW;
            }
            n += i / (out + 1);
            i %= (out + 1);

            System.arraycopy(decoded, i, decoded, i + 1, out - i);
            decoded[i] = n;
            ++i;
        }

        for (int j = 0; j < out; ++j) {
            output.append(decoded[j]);
        }

        return EPunycodeStatus.SUCCESS;
    }

    /**
     * Tests whether a code point is a "basic" code point  (less than 0x80).
     *
     * @param cp the code point to test
     * @return {@code true} if the code point is basic
     */
    private static boolean isBasic(final int cp) {
        return cp < 0x80;
    }

    /**
     * Returns the numeric value of a basic code point (for use in representing integers) in the range 0 to base-1, or
     * base if cp is does not represent a value.
     *
     * @param cp the code point to decode
     */
    private static int decodeDigit(final int cp) {
        return cp - 48 < 10 ? cp - 22 : cp - 65 < 26 ? cp - 65 :
                cp - 97 < 26 ? cp - 97 : PUNYCODE_BASE;
    }

    /**
     * Returns the basic code point whose value (when used for representing integers) is d, which needs to be in the
     * range 0 to base-1.  The lowercase form is used unless flag is nonzero, in which case the uppercase form is used.
     * The behavior is undefined if flag is nonzero and digit d has no uppercase form.
     *
     * <p>
     * 0..25 map to ASCII a..z, 26..35 map to ASCII 0..9
     */
    private static char encodeDigit(final int d) {

        return (char) ((d < 26) ? 'a' + d : '0' + d - 26);
    }

    /**
     * Bias adaptation function.
     *
     * @param delta     the delta
     * @param numpoints the number of points
     * @param firsttime true if this is the first time
     **/
    private static int adapt(final int delta, final int numpoints, final boolean firsttime) {

        int k;

        int i = firsttime ? delta / PUNYCODE_DAMP : delta >> 1;
        i += i / numpoints;

        for (k = 0; i > ((PUNYCODE_BASE - PUNYCODE_TMIN) * PUNYCODE_TMAX) / 2; k += PUNYCODE_BASE) {
            i /= PUNYCODE_BASE - PUNYCODE_TMIN;
        }

        return k + (PUNYCODE_BASE - PUNYCODE_TMIN + 1) * i / (i + PUNYCODE_SKEW);
    }

    /**
     * Status results from Punycode operations.
     */
    public enum EPunycodeStatus {

        /** Success. */
        SUCCESS,

        /** Input is invalid. */
        BAD_INPUT,

        /** Output would exceed the space provided. */
        BIG_OUTPUT,

        /** Input needs wider integers to process. */
        OVERFLOW
    }
}

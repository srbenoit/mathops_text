package dev.mathops.text;

import dev.mathops.commons.CoreConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Text manipulation algorithms defined in section 4 (Primitive data types) of the
 * <a href="https://infra.spec.whatwg.org/">WhatWG "Infra" living standard</a>.
 *
 * <p>
 * Null, boolean, byte, and String are represented by the Java equivalents. Code points are represented by Java ints.
 *
 * <p>
 * The WhatWG "byte sequence" object is a byte array in this class. Java exposes its length directly.
 */
public enum TextUtils {
    ;

    /** The HTML namespace. */
    public static final String HTML_NS = "http://www.w3.org/1999/xhtml";

    /** The MathML namespace. */
    public static final String MATHML_NS = "http://www.w3.org/1998/Math/MathML";

    /** The SVG namespace. */
    public static final String SVG_NS = "http://www.w3.org/2000/svg";

    /**
     * To <b>byte-lowercase</b> a byte sequence, increase each byte it contains, in the range 0x41 (A) to 0x5A (Z),
     * inclusive, by 0x20.
     *
     * @param sequence the sequence to make lowercase
     */
    public static void byteLowercase(final byte[] sequence) {

        for (int i = sequence.length - 1; i >= 0; --i) {
            final int value = (int) sequence[i];
            if (value >= 0x41 && value <= 0x5A) {
                sequence[i] = (byte) (value + 0x20);
            }
        }
    }

    /**
     * To <b>byte-uppercase</b> a byte sequence, decrease each byte it contains, in the range 0x61 (A) to 0x7A (Z),
     * inclusive, by 0x20.
     *
     * @param sequence the sequence to make lowercase
     */
    public static void byteUppercase(final byte[] sequence) {

        for (int i = sequence.length - 1; i >= 0; --i) {
            final int value = (int) sequence[i];
            if (value >= 0x61 && value <= 0x7A) {
                sequence[i] = (byte) (value - 0x20);
            }
        }
    }

    /**
     * A byte sequence A is a {@code byte-case-insensitive} match for a byte sequence B, if the byte-lowercase of A is
     * the byte-lowercase of B.
     *
     * @param array1 the first sequence
     * @param array2 the second sequence
     * @return true if the sequences match
     */
    public static boolean byteCaseInsensitiveMatch(final byte[] array1, final byte[] array2) {

        boolean match = true;

        final int len = array1.length;

        if (array2.length == len) {
            for (int i = len - 1; i >= 0; --i) {
                final int b1 = (int) array1[i];
                final int b2 = (int) array2[i];

                if (b1 != b2) {
                    if (b1 < 0x41 || b1 > 0x7a) {
                        match = false;
                        break;
                    } else if (b1 <= 0x5a) {
                        if (b2 != (b1 + 0x20)) {
                            match = false;
                            break;
                        }
                    } else if (b1 < 0x61 || (b1 != (b2 + 0x20))) {
                        match = false;
                        break;
                    }
                }
            }
        } else {
            match = false;
        }

        return match;
    }

    /**
     * Tests whether a byte sequence is a <b>prefix</b> of an input byte sequence, which means the input byte sequence
     * <b>starts with</b> the prefix.
     *
     * @param potentialPrefix the potential prefix
     * @param input           the input sequence
     * @return true if {@code potentialPrefix} is a prefix for {@code input}
     */
    public static boolean isPrefix(final byte[] potentialPrefix, final byte[] input) {

        boolean isPrefix = true;

        final int len = potentialPrefix.length;

        if (input.length < len) {
            isPrefix = false;
        } else {
            for (int i = len - 1; i >= 0; --i) {
                if ((int) potentialPrefix[i] != (int) input[i]) {
                    isPrefix = false;
                    break;
                }
            }
        }

        return isPrefix;
    }

    /**
     * Tests whether one byte sequence is a <b>byte less than</b> another.
     *
     * @param a the first sequence
     * @param b the second sequence
     * @return true if {@code a} is byte less than {@code b}
     */
    public static boolean isByteLessThan(final byte[] a, final byte[] b) {

        boolean lessThan = true;

        final int compare = Math.min(a.length, b.length);

        int firstDiff = -1;

        for (int i = 0; i < compare; ++i) {
            final int aValue = (int) a[i];
            final int bValue = (int) b[i];

            if (aValue < bValue) {
                firstDiff = i;
                break;
            }
            if (bValue < aValue) {
                firstDiff = i;
                lessThan = false;
                break;
            }
        }

        // The shorter one is a prefix for the longer
        if (firstDiff == -1 && b.length < a.length) {
            lessThan = false;
        }

        return lessThan;
    }

    /**
     * To <b>isomorphic decode</b> a byte sequence input, return a string whose code point length is equal to input's
     * length and whose code points have the same values as the values of input's bytes, in the same order.
     *
     * @param bytes the byte sequence
     * @return the decoded string
     */
    public static String isomorphicDecode(final byte[] bytes) {

        final char[] chars = new char[bytes.length];
        for (int i = bytes.length - 1; i >= 0; --i) {
            chars[i] = (char) ((int) bytes[i] & 0xFF);
        }

        return new String(chars);
    }

    /**
     * Tests whether a code point is a <b>surrogate</b>. A <b>surrogate</b> is a code point that is in the range U+D800
     * to U+DFFF, inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is a surrogate
     */
    public static boolean isSurrogate(final int cp) {

        return cp >= 0xD800 && cp <= 0xDFFF;
    }

    /**
     * Tests whether a code point is a <b>scalar value</b>. A <b>scalar value</b> is a code point that is not a
     * surrogate.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is a scalar value
     */
    public static boolean isScalarValue(final int cp) {

        return cp < 0xD800 || cp > 0xDFFF;
    }

    /**
     * Tests whether a code point is a <b>noncharacter</b>. A <b>noncharacter</b> is a code point that is in the range
     * U+FDD0 to U+FDEF, inclusive, or U+FFFE, U+FFFF, U+1FFFE, U+1FFFF, U+2FFFE, U+2FFFF, U+3FFFE, U+3FFFF, U+4FFFE,
     * U+4FFFF, U+5FFFE, U+5FFFF, U+6FFFE, U+6FFFF, U+7FFFE, U+7FFFF, U+8FFFE, U+8FFFF, U+9FFFE, U+9FFFF, U+AFFFE,
     * U+AFFFF, U+BFFFE, U+BFFFF, U+CFFFE, U+CFFFF, U+DFFFE, U+DFFFF, U+EFFFE, U+EFFFF, U+FFFFE, U+FFFFF, U+10FFFE, or
     * U+10FFFF.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is a noncharacter
     */
    public static boolean isNoncharacter(final int cp) {

        return (cp >= 0xFDD0 && cp <= 0xFDEF) || cp == 0xFFFE || cp == 0xFFFF || cp == 0x1FFFE
                || cp == 0x1FFFF || cp == 0x2FFFE || cp == 0x2FFFF || cp == 0x3FFFE || cp == 0x3FFFF
                || cp == 0x4FFFE || cp == 0x4FFFF || cp == 0x5FFFE || cp == 0x5FFFF || cp == 0x6FFFE
                || cp == 0x6FFFF || cp == 0x7FFFE || cp == 0x7FFFF || cp == 0x8FFFE || cp == 0x8FFFF
                || cp == 0x9FFFE || cp == 0x9FFFF || cp == 0xAFFFE || cp == 0xAFFFF || cp == 0xBFFFE
                || cp == 0xBFFFF || cp == 0xCFFFE || cp == 0xCFFFF || cp == 0xDFFFE || cp == 0xDFFFF
                || cp == 0xEFFFE || cp == 0xEFFFF || cp == 0xFFFFE || cp == 0xFFFFF || cp == 0x10FFFE
                || cp == 0x10FFFF;
    }

    /**
     * Tests whether a code point is an <b>ASCII code point</b>. An <b>ASCII code point</b> is a code point in the range
     * U+0000 NULL to U+007F DELETE, inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII code point
     */
    public static boolean isAsciiCodePoint(final int cp) {

        return cp >= 0 && cp <= 0x007F;
    }

    /**
     * Tests whether a code point is an <b>ASCII tab or newline</b>. An <b>ASCII tab or newline</b> is U+0009 TAB,
     * U+000A LF, or U+000D CR.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII tab or newline
     */
    public static boolean isAsciiTabOrNewline(final int cp) {

        return cp == 0x09 || cp == 0x0A || cp == 0x0D;
    }

    /**
     * Tests whether a code point is <b>ASCII whitespace</b>. <b>ASCII whitespace</b> is U+0009 TAB, U+000A LF, U+000C
     * FF, U+000D CR, or U+0020 SPACE. "Whitespace" is a mass noun.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is ASCII whitespace
     */
    public static boolean isAsciiWhitespcae(final int cp) {

        return cp == 0x09 || cp == 0x0A || cp == 0x0C || cp == 0x0D || cp == 0x20;
    }

    /**
     * Tests whether a code point is a <b>C0 control</b>. A <b>C0 control</b> is a code point in the range U+0000 NULL
     * to U+001F INFORMATION SEPARATOR ONE, inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is a C0 control
     */
    public static boolean isC0Control(final int cp) {

        return cp >= 0x00 && cp <= 0x1F;
    }

    /**
     * Tests whether a code point is a <b>C0 control or space</b>. A <b>C0 control or space</b> is a C0 control or
     * U+0020 SPACE.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is a C0 control or space
     */
    public static boolean isC0ControlOrSpace(final int cp) {

        return cp >= 0x00 && cp <= 0x20;
    }

    /**
     * Tests whether a code point is a <b>control</b>. A <b>control</b> is a C0 control or a code point in the range
     * U+007F DELETE to U+009F APPLICATION PROGRAM COMMAND, inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is a control
     */
    public static boolean isControl(final int cp) {

        return (cp >= 0x00 && cp <= 0x1F) || (cp >= 0x7F && cp <= 0x9F);
    }

    /**
     * Tests whether a code point is an <b>ASCII digit</b>. An <b>ASCII digit</b> is a code point in the range U+0030
     * (0) to U+0039 (9), inclusive
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII digit
     */
    public static boolean isAsciiDigit(final int cp) {

        return cp >= 0x30 && cp <= 0x39;
    }

    /**
     * Tests whether a code point is an <b>ASCII upper hex digit</b>. An <b>ASCII upper hex digit</b> is an ASCII digit
     * or a code point in the range U+0041 (A) to U+0046 (F), inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII upper hex digit
     */
    public static boolean isAsciiUpperHexDigit(final int cp) {

        return (cp >= 0x30 && cp <= 0x39) || (cp >= 0x41 && cp <= 0x46);
    }

    /**
     * Tests whether a code point is an <b>ASCII lower hex digit</b>. An <b>ASCII lower hex digit</b> is an ASCII digit
     * or a code point in the range U+0061 (A) to U+0066 (F), inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII lower hex digit
     */
    public static boolean isAsciiLowerHexDigit(final int cp) {

        return (cp >= 0x30 && cp <= 0x39) || (cp >= 0x61 && cp <= 0x66);
    }

    /**
     * Tests whether a code point is an <b>ASCII hex digit</b>. An <b>ASCII hex digit</b> is an ASCII upper hex digit or
     * ASCII lower hex digit.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII hex digit
     */
    public static boolean isAsciiHexDigit(final int cp) {

        return (cp >= 0x30 && cp <= 0x39) || (cp >= 0x41 && cp <= 0x46)
                || (cp >= 0x61 && cp <= 0x66);
    }

    /**
     * Tests whether a code point is an <b>ASCII upper alpha</b>. An <b>ASCII upper alpha</b> is a code point in the
     * range U+0041 (A) to U+005A (Z), inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII upper alpha
     */
    public static boolean isAsciiUpperAlpha(final int cp) {

        return cp >= 0x41 && cp <= 0x5A;
    }

    /**
     * Tests whether a code point is an <b>ASCII lower alpha</b>. An <b>ASCII lower alpha</b> is a code point in the
     * range U+0061 (a) to U+007A (z), inclusive.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII lower alpha
     */
    public static boolean isAsciiLowerAlpha(final int cp) {

        return cp >= 0x61 && cp <= 0x7A;
    }

    /**
     * Tests whether a code point is an <b>ASCII alpha</b>. An <b>ASCII alpha</b> is an ASCII upper alpha or ASCII lower
     * alpha.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII alpha
     */
    public static boolean isAsciiAlpha(final int cp) {

        return (cp >= 0x41 && cp <= 0x5A) || (cp >= 0x61 && cp <= 0x7A);
    }

    /**
     * Tests whether a code point is an <b>ASCII alphanumeric</b>. An <b>ASCII alphanumeric</b> is an ASCII digit or
     * ASCII alpha.
     *
     * @param cp the code point to test
     * @return true if {@code cp} is an ASCII alphanumeric
     */
    public static boolean isAsciiAlphanumeric(final int cp) {

        return (cp >= 0x41 && cp <= 0x5A) || (cp >= 0x61 && cp <= 0x7A)
                || (cp >= 0x30 && cp <= 0x39);
    }

    /**
     * To <b>convert</b> a string into a scalar value string, replace any surrogates with U+FFFD.
     *
     * @param s the string to convert
     * @return the converted string
     */
    public static USVString convertStringToScalarString(final String s) {

        final int[] cps = s.codePoints().toArray();

        for (int i = cps.length - 1; i >= 0; --i) {
            if (isSurrogate(cps[i])) {
                cps[i] = 0xFFFD;
            }
        }

        return new USVString(cps, 0, cps.length);
    }

    /**
     * The <b>code point substring</b> within a string {@code string} from start with length {@code length} is
     * determined as follows:
     *
     * @param seq   the string
     * @param start the start code point position
     * @param end   the end code point position
     * @return the substring
     */
    public static String codePointSubstring(final CharSequence seq, final int start, final int end) {

        final IntStream codePoints = seq.codePoints();
        final int[] array = codePoints.toArray();
        return new String(array, start, end - start);
    }

    /**
     * To <b>ASCII-lowercase</b> a String, replace all ASCII upper alphas in the string with their corresponding code
     * point in ASCII lower alpha.
     *
     * @param str the String to make ASCII lowercase
     * @return the result
     */
    public static String asciiLowercase(final String str) {

        final char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i >= 0; --i) {
            final int value = (int) chars[i];
            if (value >= 0x41 && value <= 0x5A) {
                chars[i] = (char) (value + 0x20);
            }
        }

        return new String(chars);
    }

    /**
     * To <b>ASCII-lowercase</b> a String, replace all ASCII upper alphas in the string with their corresponding code
     * point in ASCII lower alpha.
     *
     * @param seq the String to make ASCII lowercase
     * @return the result
     */
    public static USVString asciiLowercase(final IUSVSequence seq) {

        final int[] cp = seq.getCodePoints();
        for (int i = cp.length - 1; i >= 0; --i) {
            final int value = cp[i];
            if (value >= 0x41 && value <= 0x5A) {
                cp[i] = value + 0x20;
            }
        }

        return new USVString(cp);
    }

    /**
     * To <b>ASCII-uppercase</b> a String, replace all ASCII lower alphas in the string with their corresponding code
     * point in ASCII upper alpha.
     *
     * @param str the String to make ASCII uppercase
     * @return the result
     */
    public static String asciiUppercase(final String str) {

        final char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i >= 0; --i) {
            final int value = (int) chars[i];
            if (value >= 0x61 && value <= 0x7A) {
                chars[i] = (char) (value - 0x20);
            }
        }

        return new String(chars);
    }

    /**
     * To <b>ASCII-uppercase</b> a String, replace all ASCII lower alphas in the string with their corresponding code
     * point in ASCII upper alpha.
     *
     * @param seq the String to make ASCII uppercase
     * @return the result
     */
    public static USVString asciiUppercase(final IUSVSequence seq) {

        final int[] cp = seq.getCodePoints();
        for (int i = cp.length - 1; i >= 0; --i) {
            final int value = cp[i];
            if (value >= 0x61 && value <= 0x7A) {
                cp[i] = value - 0x20;
            }
        }

        return new USVString(cp);
    }

    /**
     * A String A is a <b>byte-case-insensitive</b> match for a String B, if the ASCII lowercase of A is the ASCII
     * lowercase of B.
     *
     * @param str1 the first String
     * @param str2 the second String
     * @return true if the Strings match
     */
    public static boolean asciiCsaseInsensitiveMatch(final String str1, final String str2) {

        boolean match = true;

        final int len = str1.length();

        if (str2.length() == len) {

            for (int i = len - 1; i >= 0; --i) {
                final int c1 = (int) str1.charAt(i);
                final int c2 = (int) str2.charAt(i);

                if (c1 != c2) {
                    if (c1 < 0x41 || c1 > 0x7a) {
                        match = false;
                        break;
                    } else if (c1 <= 0x5a) {
                        if (c2 != (c1 + 0x20)) {
                            match = false;
                            break;
                        }
                    } else if (c1 < 0x61 || (c1 != (c2 + 0x20))) {
                        match = false;
                        break;
                    }
                }
            }
        } else {
            match = false;
        }

        return match;
    }

    /**
     * A String A is a <b>byte-case-insensitive</b> match for a String B, if the ASCII lowercase of A is the ASCII
     * lowercase of B.
     *
     * @param str1 the first String
     * @param str2 the second String
     * @return true if the Strings match
     */
    public static boolean asciiCaseInsensitiveMatch(final IUSVSequence str1, final IUSVSequence str2) {

        boolean match = true;

        final int len = str1.size();

        if (str2.size() == len) {

            for (int i = len - 1; i >= 0; --i) {
                final int c1 = str1.get(i);
                final int c2 = str2.get(i);

                if (c1 != c2) {
                    if (c1 < 0x41 || c1 > 0x7a) {
                        match = false;
                        break;
                    } else if (c1 <= 0x5a) {
                        if (c2 != (c1 + 0x20)) {
                            match = false;
                            break;
                        }
                    } else if (c1 < 0x61 || (c1 != (c2 + 0x20))) {
                        match = false;
                        break;
                    }
                }
            }
        } else {
            match = false;
        }

        return match;
    }

    /**
     * To <b>strip newlines</b> from a string, remove any U+000A LF and U+000D CR code points from the string.
     *
     * @param input the String to strip
     * @return the stripped string
     */
    public static String stripNewlines(final CharSequence input) {

        final int len = input.length();
        final StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; ++i) {
            final char ch = input.charAt(i);
            if ((int) ch != 0x0A && (int) ch != 0x0D) {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    /**
     * To <b>normalize newlines</b> in a string, replace every U+000D CR U+000A LF code point pair with a single U+000A
     * LF code point, and then replace every remaining U+000D CR code point with a U+000A LF code point.
     *
     * @param input the String to normalized
     * @return the normalized string
     */
    public static String normalizeNewlines(final String input) {

        final int len = input.length();
        final StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; ++i) {
            final char ch = input.charAt(i);
            if ((int) ch == 0x0D) {
                sb.append(0x0A);
                if (i + 1 < len && (int) input.charAt(i + 1) == 0x0A) {
                    ++i;
                }
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    /**
     * To <b>strip leading and trailing ASCII whitespace</b> from a string, remove all ASCII whitespace that are at the
     * start or the end of the string.
     *
     * @param input the String to normalized
     * @return the normalized string
     */
    public static String stripLeadingAndTrailingAsciiWhitespace(final String input) {

        String result = input;

        if (!input.isEmpty()) {
            final char[] chars = input.toCharArray();
            final int len = chars.length;

            int firstNonspace = 0;
            while (firstNonspace < len && isAsciiWhitespcae((int) chars[firstNonspace])) {
                ++firstNonspace;
            }

            if (firstNonspace == len) {
                result = CoreConstants.EMPTY;
            } else {
                // There is a non-whitespace character
                int lastNonspace = len - 1;
                while (isAsciiWhitespcae((int) chars[lastNonspace])) {
                    --lastNonspace;
                }

                result = new String(chars, firstNonspace, lastNonspace - firstNonspace + 1);
            }
        }

        return result;
    }

    /**
     * To <b>strip and collapse ASCII whitespace</b> in a string, replace any sequence of one or more consecutive code
     * points that are ASCII whitespace in the string with a single U+0020 SPACE code point, and then remove any leading
     * and trailing ASCII whitespace from that string.
     *
     * @param input the String to normalized
     * @return the normalized string
     */
    public static String stripAndCollapseAsciiWhitespace(final String input) {

        String result = input;

        if (!input.isEmpty()) {
            final char[] chars = input.toCharArray();
            final int len = chars.length;

            int first = 0;
            while (first < len && isAsciiWhitespcae((int) chars[first])) {
                ++first;
            }

            if (first == len) {
                result = CoreConstants.SPC;
            } else {
                int last = len - 1;
                while (isAsciiWhitespcae((int) chars[last])) {
                    --last;
                }

                final StringBuilder sb = new StringBuilder(last - first + 1);
                boolean notInSpace = true;
                for (int i = first; i <= last; ++i) {
                    final char c = chars[i];

                    if (isAsciiWhitespcae((int) c)) {
                        if (notInSpace) {
                            sb.append(' ');
                            notInSpace = false;
                        }
                    } else {
                        sb.append(c);
                        notInSpace = true;
                    }
                }

                result = sb.toString();
            }
        }

        return result;
    }

    /**
     * Strictly splits a string on a code point delimiter.
     *
     * @param seq       the string to split
     * @param delimiter the delimiter code point
     * @return the list of resulting tokens (if the input string is empty, this will contain one empty string, and there
     *         will be one empty string for every pair of adjacent delimiters in the input string
     */
    public static List<String> strictlySplit(final CharSequence seq, final int delimiter) {

        final List<String> tokens = new ArrayList<>(20);

        final int[] codePoints = seq.codePoints().toArray();
        int start = 0;
        final int len = codePoints.length;
        int current = 0;

        while (current < len) {
            if (codePoints[current] == delimiter) {
                tokens.add(new String(codePoints, start, current - start));
                start = current + 1;
            }
            ++current;
        }

        return tokens;
    }

    /**
     * Splits a string using runs of ASCII whitespace as delimiters.
     *
     * @param seq the string to split
     * @return the list of resulting tokens (if the input string is empty or contains only whitespace , this will be an
     *         empty list)
     */
    public static List<String> splitOnAsciiWhitespace(final CharSequence seq) {

        final List<String> tokens = new ArrayList<>(20);

        final int[] codePoints = seq.codePoints().toArray();
        final int len = codePoints.length;

        // Skip leading whitespace
        int start = 0;
        while (start < len && isAsciiWhitespcae(codePoints[start])) {
            ++start;
        }

        while (start < len) {
            // Start is the index of the start of a token
            int end = start + 1;

            while (end < len && !isAsciiWhitespcae(codePoints[end])) {
                ++end;
            }

            // End is either 'len' or a whitespace character
            tokens.add(new String(codePoints, start, end - start));

            start = end;
            while (start < len && isAsciiWhitespcae(codePoints[start])) {
                ++start;
            }
        }

        return tokens;
    }

    /**
     * Splits a string using commas as delimiters, removing any ASCII whitespace that surrounds each comma-delimited
     * token.
     *
     * @param seq the string to split
     * @return the list of resulting tokens (if the input string is empty or contains only whitespace , this will be an
     *         empty list)
     */
    public static List<String> splitOnCommas(final CharSequence seq) {

        final List<String> tokens = new ArrayList<>(10);

        final int[] codePoints = seq.codePoints().toArray();
        final int len = codePoints.length;

        int start = 0;
        while (start < len) {

            // Find the next comma, or the end of the string
            int comma = start;
            while (comma < len && codePoints[comma] != 0x2C) {
                ++comma;
            }

            // Remove leading and trailing whitespace
            int tokStart = start;
            int tokEnd = comma;

            while (tokStart < comma && isAsciiWhitespcae(codePoints[tokStart])) {
                ++tokStart;
            }
            while (tokEnd > tokStart && isAsciiWhitespcae(codePoints[tokEnd - 1])) {
                --tokEnd;
            }
            tokens.add(new String(codePoints, tokStart, tokEnd - tokStart));

            start = comma + 1;
        }

        return tokens;
    }

    /**
     * Concatenates a list of strings with an optional separator.
     *
     * @param strings   the list of string to concatenate
     * @param separator a separator, null is treated as an empty string
     * @return the concatenated string
     */
    public static String concatenate(final List<String> strings, final String separator) {

        final StringBuilder sb = new StringBuilder(100);

        final int count = strings.size();

        if (count > 0) {
            final String str = strings.getFirst();
            sb.append(str);
            for (int i = 1; i < count; ++i) {
                if (separator != null) {
                    sb.append(separator);
                }
                final String str1 = strings.get(i);
                sb.append(str1);
            }
        }

        return sb.toString();
    }
}

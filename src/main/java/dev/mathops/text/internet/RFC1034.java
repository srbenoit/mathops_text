package dev.mathops.text.internet;

/**
 * An implementation of <a href='https://datatracker.ietf.org/doc/html/rfc1034'>RFC 1034 (DOMAIN NAMES - CONCEPTS AND
 * FACILITIES)</a>.
 */
public enum RFC1034 {
    ;

    /**
     * Tests if a code point is a 'letter' (a-z or A-Z).
     *
     * @param cp the code point
     * @return true if a letter
     */
    public static boolean isLetter(final int cp) {

        return (cp >= 'a' && cp <= 'z') || (cp >= 'A' && cp <= 'Z');
    }

    /**
     * Tests if a code point is a 'digit' (0-9).
     *
     * @param cp the code point
     * @return true if a digit
     */
    public static boolean isDigit(final int cp) {

        return cp >= '0' && cp <= '9';
    }

    /**
     * Tests if a code point is an "LDH character" (a letter, digit, or hyphen).
     *
     * @param cp the code point
     * @return true if a letter, digit, or hyphen
     */
    public static boolean isLdh(final int cp) {

        return cp == '-' || isLetter(cp) || isDigit(cp);
    }

    /**
     * Tests whether a sequence of code points matches the &lt;label&gt; production.
     *
     * <pre>
     * &lt;label&gt; ::= &lt;letter&gt; [ [ &lt;ldh-str&gt; ] &lt;let-dig&gt; ]
     * </pre>
     *
     * @param codePoints the sequence of code points
     * @return true if the sequence matches the production and is no more than 63 code points in length
     */
    public static boolean isValidLabel(final int[] codePoints) {

        return isValidLabel(codePoints, 0, codePoints.length);
    }

    /**
     * Tests whether a sequence of code points matches the &lt;label&gt; production.
     *
     * <pre>
     * &lt;label&gt; ::= &lt;letter&gt; [ [ &lt;ldh-str&gt; ] &lt;let-dig&gt; ]
     * </pre>
     *
     * @param codePoints the sequence of code points
     * @param start      the start index at which to begin testing
     * @param length     the number of code points to test
     * @return true if the sequence matches the production and is no more than 63 code points in length
     */
    public static boolean isValidLabel(final int[] codePoints, final int start, final int length) {

        boolean valid;

        if (length < 1 || length > 63 || start + length < codePoints.length) {
            valid = false;
        } else {
            final int last = codePoints[start + length - 1];
            valid = isLetter(codePoints[start]) && (isLetter(last) || isDigit(last));

            if (valid) {
                final int end = start + length;
                for (int i = start + 1; valid && i < end; ++i) {
                    valid = isLdh(codePoints[i]);
                }
            }
        }

        return valid;
    }

    /**
     * Tests whether a sequence of code points matches the &lt;subdomain&gt; production.
     *
     * <pre>
     * &lt;subdomain&gt; ::= &lt;label&gt; | &lt;subdomain&gt; "." &lt;label&gt;
     * </pre>
     *
     * @param codePoints the sequence of code points
     * @return true if the sequence matches the production and is no more than 63 code points in length
     */
    public static boolean isValidSubdomain(final int[] codePoints) {

        return isValidSubdomain(codePoints, 0, codePoints.length);
    }

    /**
     * Tests whether a sequence of code points matches the &lt;subdomain&gt; production.
     *
     * <pre>
     * &lt;subdomain&gt; ::= &lt;label&gt; | &lt;subdomain&gt; "." &lt;label&gt;
     * </pre>
     *
     * @param codePoints the sequence of code points
     * @param start      the start index at which to begin testing
     * @param length     the number of code points to test
     * @return true if the sequence matches the production and is no more than 63 code points in length
     */
    public static boolean isValidSubdomain(final int[] codePoints, final int start, final int length) {

        boolean valid = isValidLabel(codePoints, start, length);

        if (!valid) {
            int lastDot = -1;
            for (int i = start + length - 1; i >= start; --i) {
                if (codePoints[i] == '.') {
                    lastDot = i;
                    break;
                }
            }

            if (lastDot > 0) {
                final int end = start + length;
                valid = isValidSubdomain(codePoints, start, lastDot)
                        && isValidLabel(codePoints, lastDot + 1, end - lastDot - 1);
            }
        }

        return valid;
    }

    /**
     * Tests whether a sequence of code points matches the &lt;domain&gt; production.
     *
     * <pre>
     * &lt;domain&gt; ::= &lt;subdomain&gt; | " "
     * </pre>
     *
     * @param codePoints the sequence of code points
     * @return true if the sequence matches the production and is no more than 63 code points in length
     */
    public static boolean isValidDomain(final int[] codePoints) {

        return (codePoints.length == 1 && codePoints[0] == ' ') || isValidSubdomain(codePoints);
    }
}

package dev.mathops.text.parser;

import dev.mathops.commons.CoreConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for a sequence of "lines" from a source text file.  This supports parsers reporting error or warning
 * messages relative to line numbers and column offsets, rather than as simple character offsets from the beginning of a
 * file.
 */
public final class LineOrientedParserInput  {

    /** A newline character. */
    private static final int NEWLINE = (int) '\n';

    /** A carriage return character. */
    private static final int CARRIAGE_RETURN = (int) '\r';

    /** The lines of the file. */
    private final List<String> lines;

    /**
     * Constructs a {@code LineOrientedParserInput} from a string or an array of strings.  This method scans each string
     * for newlines ('\n') and breaks each string at each newline found.  The '\n' characters (and any '\r' characters
     * that precede them) are removed.
     *
     * @param content the string content
     */
    public LineOrientedParserInput(final String... content) {

        if (content == null || content.length == 0) {
            this.lines = new ArrayList<>(0);
        } else {
            // Accumulate lines in a temporary list that will grow (perhaps far too large)
            final List<String> temp = new ArrayList<>(100);
            for (final String s : content) {
                accumulateLines(s, temp);
            }
            // Prune to the actual size needed
            this.lines = new ArrayList<>(temp);
        }
    }

    /**
     * Breaks a string on '\n' boundaries and adds each piece to a list.
     *
     * @param s    the string
     * @param list the list
     */
    private static void accumulateLines(final String s, final List<? super String> list) {

        int start = 0;
        int index = s.indexOf(NEWLINE);

        while (index > -1) {
            if (index == start) {
                list.add(CoreConstants.EMPTY);
                ++start;
            } else if ((int) s.charAt(index - 1) == CARRIAGE_RETURN) {
                final String sub = s.substring(start, index - 1);
                list.add(sub);
                start = index + 2;
            } else {
                final String sub = s.substring(start, index);
                list.add(sub);
                start = index + 1;
            }

            index = s.indexOf(NEWLINE, start);
        }
    }

    /**
     * Gets the number of lines.
     *
     * @return the number of lines
     */
    public int getNumLines() {

        return this.lines.size();
    }

    /**
     * Gets the line at a specified index.
     *
     * @param index the 0-based index
     * @return the line
     */
    public String getLine(final int index) {

        return this.lines.get(index);
    }

    /**
     * Generates the string representation, which concatenates all lines with a simple '\n' line terminator (including a
     * terminator as the last character in the returned string).
     *
     * @return the concatenated contents
     */
    public String toString() {

        int totalLen = 0;
        for (final String s : this.lines) {
            totalLen += s.length() + 1;
        }

        final StringBuilder builder = new StringBuilder(totalLen);
        for (final String s : this.lines) {
            builder.append(s);
            builder.append(NEWLINE);
        }

        return builder.toString();
    }

    /**
     * Generates the string representation, which concatenates all lines with the Windows '\r\n' line terminator
     * (including a terminator as the last character in the returned string).
     *
     * @return the concatenated contents
     */
    public String toWidowsString() {

        int totalLen = 0;
        for (final String s : this.lines) {
            totalLen += s.length() + 2;
        }

        final StringBuilder builder = new StringBuilder(totalLen);
        for (final String s : this.lines) {
            builder.append(s);
            builder.append(CARRIAGE_RETURN);
            builder.append(NEWLINE);
        }

        return builder.toString();
    }
}

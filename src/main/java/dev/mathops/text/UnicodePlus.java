/*
 * Copyright (C) 2022 Steve Benoit
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the  License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If  not, see
 * <https://www.gnu.org/licenses/>.
 */

package dev.mathops.text;

import dev.mathops.commons.log.Log;

import java.util.List;

/**
 * Code point constants used for Unicode+.
 */
public enum UnicodePlus {
    ;

    /** The minimum Unicode high surrogate code point. */
    public static final int MIN_UNICODE_HIGH_SURROGATE = 0xD800;

    /** The minimum Unicode low surrogate code point. */
    public static final int MIN_UNICODE_LOW_SURROGATE = 0xDC00;

    /** The minimum Unicode private use code point. */
    public static final int MIN_UNICODE_PRIVATE_USE = 0xE000;

    /** The minimum Unicode+ high surrogate code point. */
    public static final int MIN_UNICODE_PLUS_HIGH_SURROGATE = 0xF000;

    /** The minimum Unicode+ low surrogate code point. */
    public static final int MIN_UNICODE_PLUS_LOW_SURROGATE = 0xF400;

    /** The minimum Unicode private use code point beyond the Unicode+ surrogate block. */
    public static final int MIN_UNICODE_PRIVATE_USE2 = 0xF800;

    /** The low-order 16 bits of a non-character code point. */
    public static final int NON_CHARACTER_MASK_1 = 0xFFFE;

    /** The low-order 16 bits of a non-character code point. */
    public static final int NON_CHARACTER_MASK_2 = 0xFFFF;

    /** The minimum 2-byte Unicode code point. */
    public static final int MAX_TWO_BYTE = 0xFFFD;

    /** The minimum code point value that requires 4 bytes. */
    public static final int MIN_FOUR_BYTE = 0x10000;

    /** The minimum code point value in the extended non-Unicode range. */
    public static final int MIN_NON_UNICODE = 0x110000;

    /** The minimum code point value in the extended non-Unicode range. */
    public static final int MAX_NON_UNICODE = 0x1FFFFD;

    /**
     * Generates a UTF-16 string that encodes an array of USV values.
     *
     * @param data  the source data array of USV values
     * @param start the position of the first code point to include
     * @param end   the position after the last code point to include
     * @return the UTF-16 string representation
     */
    public static String usvArrayToString(final int[] data, final int start, final int end) {

        final String result;

        boolean hasExtended = false;
        for (int i = start; i < end; ++i) {
            final int cp = data[i];
            if (cp >= MIN_NON_UNICODE) {
                hasExtended = true;
                break;
            }
        }

        final int len = end - start;
        if (hasExtended) {
            final StringBuilder builder = new StringBuilder(len * 11 / 10);

            for (int i = start; i < end; ++i) {
                final int cp = data[i];
                if (cp > MAX_NON_UNICODE) {
                    final String msg = Res.get(Res.UNI_CP_OUT_OF_RANGE);
                    Log.warning(msg);
                } else if (cp >= MIN_NON_UNICODE) {
                    final int reduced = cp - MIN_NON_UNICODE;
                    final int high = 0xF000 + (reduced >> 10);
                    final int low = 0xF400 + (reduced & 0x3FF);
                    builder.append((char) high);
                    builder.append((char) low);
                } else {
                    builder.appendCodePoint(cp);
                }
            }
            result = builder.toString();
        } else {
            result = new String(data, start, len);
        }

        return result;
    }

    /**
     * Generates an escaped version of a sequence of USV values.  The escaped string will consist only of ASCII
     * characters.
     *
     * @param data  the data to escape
     * @param start the first position in the data array to escape
     * @param end   the position after the last entry in the data array to escape
     * @return the escaped ASCII string
     */
    public String escape(final int[] data, final int start, final int end) {

        // TODO:

        return null;
    }

    /**
     * Generates a list of USV values from an escaped string.  The entire input string is processed.  All recognized
     * escape sequences are converted to the corresponding USV value.  All other characters in the input stream are
     * emitted as their corresponding code points (where UTF-16 surrogate pairs are converted to a USV value).  The
     * input string does not need to be ASCII-only.
     *
     * <p>
     * Escapes follow (and are compatible with) the LaTeX system, to the extent possible.  LaTeX defines escapes that
     * map to specific glyphs, where this system maps escapes to USV values.  LaTeX may map multiple escapes to
     * different glyphs that both correspond to the same USV value.  In these cases, we try to find the most reasonable
     * substitutions to maintain the best LaTeX compatibility possible.
     *
     * <p>
     * The backslash character starts an escape.  The following single-character escapes are recognized:
     *
     * <ul>
     *     <li>"\\" is unescaped as "\"</li>
     *     <li>"\$" is unescaped as "$"</li>
     *     <li>"\%" is unescaped as "%"</li>
     *     <li>"\_" is unescaped as "_"</li>
     *     <li>"\{" is unescaped as "{"</li>
     *     <li>"\{" is unescaped as "}"</li>
     *     <li>"\&" is unescaped as "&"</li>
     *     <li>"\#" is unescaped as "#"</li>
     * </ul>
     * <p>
     * Of these, only the backslash actually needs to be escaped - the others, if encountered in the input string,
     * will simply be emitted to the USV sequence.
     *
     * <p>
     * Otherwise, characters after the backslash are scanned and compared to a database of escape codes.  If a match
     * is found, the corresponding USV is emitted. If not, a warning is logged and the characters are written unchanged.
     * Escape codes, when matched and followed by a space character will consume the space as well.  So "2\pi r" would
     * be unescaped to three USV values: the digit 2, the lowercase Pi symbol, and the letter r, while "2 \pi  r" would
     * be escaped to four USVs (2, Pi, a space, then r).  To terminate an escape without consuming the space that
     * follows, the escape can be terminated by "{}", which will be consumed. This is consistent with LaTeX.  Some
     * escape sequences include arguments surrounded by curly braces - these do not need additional termination, and
     * will not consume a space that follows the closing brace.
     *
     * <p>
     * Control codes in the input string are retained (tab characters, line terminators, etc.).  An escape that ends
     * at the end of a line does not consume the line terminator that follows.  LaTeX may have several escape sequences
     * that map to the same USV - all will be recognized by this method, but if the resulting string is then escaped,
     * the "canonical" escape sequence will be emitted.
     *
     * @param escaped  the escaped string
     * @param warnings a list to which to add warnings for unrecognized or malformed escape sequences
     * @return the escaped ASCII string
     */
    public int[] unescape(final String escaped, final List<String> warnings) {

        // TODO:

        return null;
    }
}

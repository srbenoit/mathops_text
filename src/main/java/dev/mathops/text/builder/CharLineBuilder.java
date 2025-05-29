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

package dev.mathops.text.builder;

/**
 * Supplements the methods of {@code CharSimpleBuilder} to add CRLF to the ends of lines.
 */
class CharLineBuilder extends CharSimpleBuilder {

    /**
     * Constructs a new {@code CharLineBuilder}.
     *
     * @param capacity the initial capacity to allocate for storage
     */
    CharLineBuilder(final int capacity) {

        super(capacity);
    }

    /**
     * Appends a {code char} argument followed by a newline to this sequence.  The UTF-16 character is appended to this
     * object's contents, and then the two-character CRLF newline is appended.
     *
     * @param value the character value to append
     */
    public final void addlnChar(final char value) {

        addChar(value);
        addln();
    }

    /**
     * Appends an array of characters followed by a newline to this sequence.  The UTF-16 characters are appended to
     * this object's contents, and then the two-character CRLF newline is appended.
     *
     * @param value the character value to append
     */
    public final void addlnChars(final char... value) {

        addChars(value);
        addln();
    }

    /**
     * Appends the UTF-16 characters that represent a "Unicode+" code point, in the range 0x0 to 0x1FFFFF, followed by a
     * newline.  If the provided value is outside that range, or if it falls in the surrogate range, no action is taken
     * and a warning message is logged.
     *
     * @param cp the code point value to append
     */
    public final void addlnCodePoint(final int cp) {

        addCodePoint(cp);
        addln();
    }

    /**
     * Appends a {code String} argument followed by a newline to this sequence.  The UTF-16 characters that make up the
     * string are appended to this object's contents, and then the two-character CRLF newline is appended.
     *
     * @param value the {code String} to append
     */
    public final void addlnString(final String value) {

        addString(value);
        addln();
    }

    /**
     * Appends an array of {@code String}s followed by a newline to this sequence.
     * <p>
     * If the argument is null or empty, nothing is appended.  Otherwise, each argument is added in turn as described in
     * {@code innerAddString}.  Finally, the two-character CRLF newline is appended.
     *
     * @param value the objects to append
     */
    public final void addlnStrings(final String... value) {

        addStrings(value);
        addln();
    }

    /**
     * Appends the string representation of a {code boolean} argument followed by a newline to the sequence.  The
     * argument is converted to a string as if by the method {@code String.valueOf}, the characters of that string are
     * then appended to this sequence, and then the two-character CRLF newline is appended.
     *
     * @param value the boolean value to append
     */
    public final void addlnBoolean(final boolean value) {

        addBoolean(value);
        addln();
    }

    /**
     * Appends the string representation of an {code int} argument followed by a newline to this sequence. The argument
     * is converted to a string as if by the method {@code String.valueOf}, the characters of that string are then
     * appended to this sequence, and then the two-character CRLF newline is appended.
     *
     * @param value the int value to append
     */
    public final void addlnInt(final int value) {

        addInt(value);
        addln();
    }

    /**
     * Appends the string representation of a {code long} argument followed by a newline to this sequence. The argument
     * is converted to a string as if by the method {@code String.valueOf}, the characters of that string are then
     * appended to this sequence, and then the two-character CRLF newline is appended.
     *
     * @param value the long value to append
     */
    public final void addlnLong(final long value) {

        addLong(value);
        addln();
    }

    /**
     * Appends the string representation of a {code float} argument followed by a newline to this sequence. The argument
     * is converted to a string as if by the method {@code String.valueOf}, the characters of that string are then
     * appended to this sequence, and then the two-character CRLF newline is appended.
     *
     * @param value the float value to append
     */
    public final void addlnFloat(final float value) {

        addFloat(value);
        addln();
    }

    /**
     * Appends the string representation of a {code double} argument followed by a newline to this sequence. The
     * argument is converted to a string as if by the method {@code String.valueOf}, the characters of that string are
     * then appended to this sequence, and then the two-character CRLF newline is appended.
     *
     * @param value the double value to append
     */
    public final void addlnDouble(final double value) {

        addDouble(value);
        addln();
    }

    /**
     * Appends the string representation of an {@code Object} followed by a newline to this sequence.
     * <p>
     * If the argument is {code null}, the characters of "null" are appended; if the argument is a character array, its
     * contents are appended directly; otherwise the argument is converted to a string by its {@code toString} method,
     * and the characters of that string are then appended to this sequence.  Finally, the two-character CRLF newline is
     * appended.
     *
     * @param value the objects to append
     */
    public final void addlnObject(final Object value) {

        addObject(value);
        addln();
    }

    /**
     * Appends the string representation of any number of {@code Object}s followed by a newline to this sequence.
     * <p>
     * If the argument is null or empty, nothing is appended.  Otherwise, each argument is added in turn as described in
     * {@code innerAddObject}.  Finally, the two-character CRLF newline is appended.
     *
     * @param value the objects to append
     */
    public final void addlnObjects(final Object... value) {

        addObjects(value);
        addln();
    }
}

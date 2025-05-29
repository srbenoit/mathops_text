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

import dev.mathops.commons.CoreConstants;

/**
 * Provides methods to concatenate string representations of various primitive or object types to the superclass
 * character sequence.
 */
public class CharSimpleBuilder extends CharBuilder {

    /** A commonly used character array. */
    static final char[] TRUE = Boolean.TRUE.toString().toCharArray();

    /** A commonly used character array. */
    static final char[] FALSE = Boolean.FALSE.toString().toCharArray();

    /** A commonly used character array. */
    static final char[] NULL = "null".toCharArray();

    /** A commonly used character array. */
    static final char[] CR_LF_CHARS = CoreConstants.CRLF.toCharArray();

    /**
     * Constructs a new {@code CharSimpleBuilder}.
     *
     * @param capacity the initial capacity to allocate for storage
     */
    protected CharSimpleBuilder(final int capacity) {

        super(capacity);
    }

    /**
     * Appends a {code String} argument to this sequence.  The UTF-16 characters that make up the string are appended to
     * this object's contents.
     *
     * @param value the {code String} to append
     */
    public final void addString(final String value) {

        if (value == null) {
            addChars(NULL);
        } else {
            final char[] valueChars = value.toCharArray();
            addChars(valueChars);
        }
    }

    /**
     * Appends an array of {code String} arguments to this sequence.  The UTF-16 characters that make up each string are
     * appended to this object's contents in turn.  If the array contains a null value, "null" is appended for that
     * value.
     *
     * @param value the {code String}s to append
     */
    public final void addStrings(final String... value) {

        if (value != null) {
            for (final String val : value) {
                addString(val);
            }
        }
    }

    /**
     * Appends the string representation of a {code boolean} argument to the sequence.
     * <p>
     * The argument is converted to a string as if by the method {@code String.valueOf}, and the characters of that
     * string are then appended to this sequence.
     *
     * @param value the boolean value to append
     */
    public final void addBoolean(final boolean value) {

        addChars(value ? TRUE : FALSE);
    }

    /**
     * Appends the string representation of an {code int} argument to this sequence.
     * <p>
     * The argument is converted to a string as if by the method {@code String.valueOf}, and the characters of that
     * string are then appended to this sequence.
     *
     * @param value the int value to append
     */
    public final void addInt(final int value) {

        final String valueString = Integer.toString(value);
        addString(valueString);
    }

    /**
     * Appends the string representation of a {code long} argument to this sequence.
     * <p>
     * The argument is converted to a string as if by the method {@code String.valueOf}, and the characters of that
     * string are then appended to this sequence.
     *
     * @param value the long value to append
     */
    public final void addLong(final long value) {

        final String valueString = Long.toString(value);
        addString(valueString);
    }

    /**
     * Appends the string representation of a {code float} argument to this sequence.
     * <p>
     * The argument is converted to a string as if by the method {@code String.valueOf}, and the characters of that
     * string are then appended to this sequence.
     *
     * @param value the float value to append
     */
    public final void addFloat(final float value) {

        final String valueString = Float.toString(value);
        addString(valueString);
    }

    /**
     * Appends the string representation of a {code double} argument to this sequence.
     * <p>
     * The argument is converted to a string as if by the method {@code String.valueOf}, and the characters of that
     * string are then appended to this sequence.
     *
     * @param value the double value to append
     */
    public final void addDouble(final double value) {

        final String valueString = Double.toString(value);
        addString(valueString);
    }

    /**
     * Appends the string representation of an {@code Object} to this sequence.
     * <p>
     * If the argument is {code null}, the characters of "null" are appended, and if the argument is a character array,
     * its contents are appended directly.  Otherwise, the argument is converted to a string by its {@code toString}
     * method, and the characters of that string are then appended to this sequence.
     *
     * @param value the object to append
     */
    public final void addObject(final Object value) {

        if (value == null) {
            addChars(NULL);
        } else if (value instanceof char[]) {
            addChars((char[]) value);
        } else {
            final String valueString = value.toString();
            addString(valueString);
        }
    }

    /**
     * Appends the string representation of any number of {@code Object}s to this sequence.
     * <p>
     * If the argument is null or empty, nothing is appended.  Otherwise, each argument is added in turn as described in
     * {@code innerAddObject}.
     *
     * @param value the objects to append
     */
    public final void addObjects(final Object... value) {

        if (value != null) {
            for (final Object val : value) {
                addObject(val);
            }
        }
    }

    /**
     * Appends the characters for a CRLF end-of-line to this sequence.
     */
    public final void addln() {

        addChars(CR_LF_CHARS);
    }

    /**
     * Generates a String by concatenating a list of strings.
     *
     * @param args the strings to concatenate; the argument accepts general objects (converted to strings by their {code
     *             toString} method), including null values (which cause "null" to be added to the concatenation)
     * @return the constructed string
     */
    public static String concat(final Object... args) {

        final CharSimpleBuilder builder = new CharSimpleBuilder(100);

        for (final Object arg : args) {
            if (arg != null) {
                builder.addObject(arg);
            }
        }

        return builder.toString();
    }
}

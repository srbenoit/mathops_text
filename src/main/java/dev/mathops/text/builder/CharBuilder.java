/*
 * Copyright (C) 2022 Steve Benoit
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the  License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  General Public License for more
 * details.
 *
 *  You should have received a copy of the GNU General Public License along with this program. If  not, see
 * <https://www.gnu.org/licenses/>.
 */

package dev.mathops.text.builder;

import dev.mathops.commons.log.Log;
import dev.mathops.text.UnicodePlus;

/**
 * Supports the construction of character strings by appending to a character array which resizes as needed.
 */
public class CharBuilder {

    /** The space character. */
    public static final char SPC = ' ';

    /** The array of characters, generally larger than the current size. */
    private char[] chars;

    /** The number of characters in the array. */
    private int size;

    /**
     * Constructs a new {@code CharBuilder}.
     *
     * @param capacity the initial capacity to allocate for storage
     */
    CharBuilder(final int capacity) {

        this.chars = new char[capacity];
        this.size = 0;
    }

    /**
     * Gets the current length (number of characters) currently in the builder.
     *
     * @return the length
     */
    public final int length() {

        return this.size;
    }

    /**
     * Adds spaces to the buffer until the length is at least some specified length. Useful for constructing vertically
     * aligned reports or tables.
     *
     * @param len the length to which to pad
     */
    public final void padToLength(final int len) {

        ensureCapacity(len);

        while (this.size < len) {
            this.chars[this.size] = SPC;
            ++this.size;
        }
    }

    /**
     * Gets the character at a specified position.
     *
     * @param index the index
     * @return the character at that index
     */
    public final char charAt(final int index) {

        return this.chars[index];
    }

    /**
     * Resets the builder (clears any string content).  This method does not change allocated storage.
     */
    public final void reset() {

        this.size = 0;
    }

    /**
     * Truncates the string at a specified size.
     *
     * @param newSize the new size (if longer than current string, no action is taken)
     */
    public final void truncate(final int newSize) {

        if (newSize >= 0) {
            this.size = Math.min(this.size, newSize);
        }
    }

    /**
     * Returns a string representing the data in this sequence. A new {@code String} object is allocated and initialized
     * to contain the character sequence currently represented by this object. This {@code String} is then returned.
     * Subsequent changes to this sequence do not affect the contents of the {@code String}.
     *
     * @return the string
     */
    @Override
    public final String toString() {

        return new String(this.chars, 0, this.size);
    }

    /**
     * Ensures that the builder has at least a given capacity.
     *
     * @param cap the target capacity
     */
    private void ensureCapacity(final int cap) {

        if (this.chars.length < cap) {
            final int newLen = Math.max(cap, this.chars.length * 3 / 2);
            final char[] array = new char[newLen];
            System.arraycopy(this.chars, 0, array, 0, this.size);
            this.chars = array;
        }
    }

    /**
     * Appends a character to the builder's character array.
     *
     * @param ch the character
     */
    public final void addChar(final char ch) {

        ensureCapacity(this.size + 1);
        this.chars[this.size] = ch;
        ++this.size;
    }

    /**
     * Appends some characters to the builder's character array.
     *
     * @param source the source character array
     */
    public final void addChars(final char[] source) {

        addChars(source, source.length);
    }

    /**
     * Appends some characters to the builder's character array.
     *
     * @param source the source character array
     * @param len    the number of characters to copy
     */
    public final void addChars(final char[] source, final int len) {

        ensureCapacity(this.size + len);
        System.arraycopy(source, 0, this.chars, this.size, len);
        this.size += len;
    }

    /**
     * Appends the UTF-16 characters that represent a "Unicode+" code point, in the range 0x0 to 0x1FFFFF.  If the
     * provided value is outside that range, or if it falls in the surrogate range, no action is taken and a warning
     * message is logged.
     *
     * @param cp the code point
     */
    public final void addCodePoint(final int cp) {

        if (cp < 0) {
            // Invalid Unicode+ code point
            final String msg = Res.get(Res.B_ADD_NEGATIVE_CODE_POINT);
            Log.warning(msg);
        } else if (cp < UnicodePlus.MIN_UNICODE_HIGH_SURROGATE) {
            // Valid code point before the surrogate block
            addChar((char) cp);
        } else if (cp < UnicodePlus.MIN_UNICODE_PRIVATE_USE) {
            // Unicode surrogate - reject (we can't add a surrogate as a code point)
            final String hex = Integer.toHexString(cp);
            final String msg = Res.fmt(Res.B_ADD_SURROGATE_CODE_POINT, hex);
            Log.warning(msg);
        } else if (cp < UnicodePlus.MIN_UNICODE_PLUS_HIGH_SURROGATE) {
            // Valid private use code point
            addChar((char) cp);
        } else if (cp < UnicodePlus.MIN_UNICODE_PRIVATE_USE2) {
            // Unicode+ surrogate - reject (we can't add a surrogate as a code point)
            final String hex = Integer.toHexString(cp);
            final String msg = Res.fmt(Res.B_ADD_SURROGATE_CODE_POINT_2, hex);
            Log.warning(msg);
        } else if (cp <= UnicodePlus.MAX_TWO_BYTE) {
            // Valid 2-byte code point beyond Unicode+ surrogate block
            addChar((char) cp);
        } else if (cp < UnicodePlus.MIN_FOUR_BYTE) {
            // Invalid ("non-character") code point at end of Page 0
            final String hex = Integer.toHexString(cp);
            final String msg = Res.fmt(Res.B_ADD_NON_CHARACTER, hex);
            Log.warning(msg);
        } else {
            // Code point is more than 2 bytes - must be encoded with surrogate pairs
            final int test = cp & 0xFFFF;
            if (test == UnicodePlus.NON_CHARACTER_MASK_1 || test == UnicodePlus.NON_CHARACTER_MASK_2) {
                // Invalid ("non-character") code point
                final String hex = Integer.toHexString(cp);
                final String msg = Res.fmt(Res.B_ADD_NON_CHARACTER, hex);
                Log.warning(msg);
            } else if (cp < UnicodePlus.MIN_NON_UNICODE) {
                // Encode using Unicode surrogate pair
                final int shifted = cp - UnicodePlus.MIN_FOUR_BYTE;
                final int high = UnicodePlus.MIN_UNICODE_HIGH_SURROGATE + (shifted >> 10);
                final int low = UnicodePlus.MIN_UNICODE_LOW_SURROGATE + (shifted & 0x3FF);
                addChar((char) high);
                addChar((char) low);
            } else if (cp <= UnicodePlus.MAX_NON_UNICODE) {
                // Encode using Unicode+ surrogate pair
                final int shifted = cp - UnicodePlus.MIN_NON_UNICODE;
                final int high = UnicodePlus.MIN_UNICODE_PLUS_HIGH_SURROGATE + (shifted >> 10);
                final int low = UnicodePlus.MIN_UNICODE_PLUS_LOW_SURROGATE + (shifted & 0x3FF);
                addChar((char) high);
                addChar((char) low);
            } else {
                // Invalid Unicode+ code point
                final String hex = Integer.toHexString(cp);
                final String msg = Res.fmt(Res.B_ADD_OUT_OF_RANGE, hex);
                Log.warning(msg);
            }
        }
    }
}

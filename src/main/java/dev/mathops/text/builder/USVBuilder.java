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
import dev.mathops.text.USVString;
import dev.mathops.text.UnicodePlus;

/**
 * Supports the construction of code point strings by appending to a Unicode+ code point array which resizes as needed.
 */
public class USVBuilder {

    /** The space code point. */
    public static final int SPC = (int) ' ';

    /** The array of code points, generally larger than the current size. */
    private int[] codePoints;

    /** The number of code points in the array. */
    private int size;

    /**
     * Constructs a new {@code USVBuilder}.
     *
     * @param capacity the initial capacity to allocate for storage
     */
    USVBuilder(final int capacity) {

        this.codePoints = new int[capacity];
        this.size = 0;
    }

    /**
     * Gets the current length (number of code points) currently in the builder.
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
            this.codePoints[this.size] = SPC;
            ++this.size;
        }
    }

    /**
     * Gets the code point at a specified position.
     *
     * @param index the index
     * @return the code point at that index
     */
    public final int codePointAt(final int index) {

        return this.codePoints[index];
    }

    /**
     * Resets the builder (clears any code point content).  This method does not change allocated storage.
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

        return UnicodePlus.usvArrayToString(this.codePoints, 0, this.size);
    }

    /**
     * Returns a USV string representing the data in this sequence. A new {@code USVString} object is allocated and
     * initialized to contain the code point sequence currently represented by this object. This {@code USVString} is
     * then returned. Subsequent changes to this sequence do not affect the contents of the {@code USVString}.
     *
     * @return the USV string
     */
    public final USVString toUSVString() {

        return new USVString(this.codePoints, 0, this.size);
    }

    /**
     * Ensures that the builder has at least a given capacity.
     *
     * @param cap the target capacity
     */
    private void ensureCapacity(final int cap) {

        if (this.codePoints.length < cap) {
            final int newLen = Math.max(cap, this.codePoints.length * 3 / 2);
            final int[] array = new int[newLen];
            System.arraycopy(this.codePoints, 0, array, 0, this.size);
            this.codePoints = array;
        }
    }

    /**
     * Appends a code point to the builder's code point array without performing any range checks.
     *
     * @param codePoint the code point
     */
    private void innerAddCodePoint(final int codePoint) {

        ensureCapacity(this.size + 1);
        this.codePoints[this.size] = codePoint;
        ++this.size;
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
            // Valid code point before surrogate block
            innerAddCodePoint(cp);
        } else if (cp < UnicodePlus.MIN_UNICODE_PRIVATE_USE) {
            // Unicode surrogate - reject (we can't add a surrogate as a code point)
            final String hex = Integer.toHexString(cp);
            final String msg = Res.fmt(Res.B_ADD_SURROGATE_CODE_POINT, hex);
            Log.warning(msg);
        } else if (cp < UnicodePlus.MIN_UNICODE_PLUS_HIGH_SURROGATE) {
            // Valid private use code point
            innerAddCodePoint(cp);
        } else if (cp < UnicodePlus.MIN_UNICODE_PRIVATE_USE2) {
            // Unicode+ surrogate - reject (we can't add a surrogate as a code point)
            final String hex = Integer.toHexString(cp);
            final String msg = Res.fmt(Res.B_ADD_SURROGATE_CODE_POINT_2, hex);
            Log.warning(msg);
        } else if (cp <= UnicodePlus.MAX_NON_UNICODE) {
            // Everything beyond Unicode+ surrogate block
            final int test = cp & 0xFFFF;
            if (test == UnicodePlus.NON_CHARACTER_MASK_1 || test == UnicodePlus.NON_CHARACTER_MASK_2) {
                // Invalid ("non-character") code point
                final String hex = Integer.toHexString(cp);
                final String msg = Res.fmt(Res.B_ADD_NON_CHARACTER, hex);
                Log.warning(msg);
            } else {
                innerAddCodePoint(cp);
            }
        } else {
            // Invalid Unicode+ code point
            final String hex = Integer.toHexString(cp);
            final String msg = Res.fmt(Res.B_ADD_OUT_OF_RANGE, hex);
            Log.warning(msg);
        }
    }

    /**
     * Appends some code points to the builder's character array.
     *
     * @param source the source code point array
     */
    public final void addCodePoints(final int[] source) {

        addCodePoints(source, source.length);
    }

    /**
     * Appends some code points to the builder's code point array.
     *
     * @param source the source code point array
     * @param len    the number of code points to copy
     */
    public final void addCodePoints(final int[] source, final int len) {

        ensureCapacity(this.size + len);
        for (int i = 0; i < len; ++i) {
            addCodePoint(source[i]);
        }
    }

    /**
     * Appends a character's code point to the builder's code point array.
     *
     * @param ch the character
     */
    public final void addChar(final char ch) {

        addCodePoint((int) ch);
    }

    /**
     * Appends some characters to the builder's character array.  This method will convert any surrogate pairs in the
     * supplied character array (Unicode or Unicode+) into the corresponding code points when adding.
     *
     * @param source the source character array
     */
    public final void addChars(final char[] source) {

        addChars(source, source.length);
    }

    /**
     * Appends some characters to the builder's character array.  This method will convert any surrogate pairs in the *
     * supplied character array (Unicode or Unicode+) into the corresponding code points when adding.
     *
     * @param source the source character array
     * @param len    the number of characters to copy
     */
    public final void addChars(final char[] source, final int len) {

        // Merge surrogate pairs into code points as we go
        for (int i = 0; i < len; ++i) {
            final int cp = (int) source[i];
            if (cp < UnicodePlus.MIN_UNICODE_HIGH_SURROGATE) {
                // Valid code point before surrogate block
                addCodePoint(cp);
            } else if (cp < UnicodePlus.MIN_UNICODE_LOW_SURROGATE) {
                // High surrogate
                if (i + 1 < len) {
                    final int cp2 = (int) source[i + 1];
                    if (cp2 >= UnicodePlus.MIN_UNICODE_LOW_SURROGATE && cp2 < UnicodePlus.MIN_UNICODE_PRIVATE_USE) {
                        final int high = cp - UnicodePlus.MIN_UNICODE_HIGH_SURROGATE;
                        final int low = cp2 - UnicodePlus.MIN_UNICODE_LOW_SURROGATE;
                        final int joined = UnicodePlus.MIN_FOUR_BYTE + (high << 10) + low;
                        addCodePoint(joined);
                        ++i;
                    } else {
                        // High surrogate without matching low surrogate
                        final String hex = Integer.toHexString(cp);
                        final String fmt = Res.fmt(Res.B_HIGH_SURROGATE_W_NO_LOW, hex);
                        Log.warning(fmt);
                    }
                } else {
                    // High surrogate without paired low surrogate
                    final String hex = Integer.toHexString(cp);
                    final String msg = Res.get(Res.B_HIGH_SURROGATE_AT_END);
                    Log.warning(msg);
                }
            } else if (cp < UnicodePlus.MIN_UNICODE_PLUS_HIGH_SURROGATE) {
                addCodePoint(cp);
            } else if (cp < UnicodePlus.MIN_UNICODE_PLUS_LOW_SURROGATE) {
                // High surrogate
                if (i + 1 < len) {
                    final int cp2 = (int) source[i + 1];
                    if (cp2 >= UnicodePlus.MIN_UNICODE_PLUS_LOW_SURROGATE
                            && cp2 < UnicodePlus.MIN_UNICODE_PRIVATE_USE2) {
                        final int high = cp - UnicodePlus.MIN_UNICODE_PLUS_HIGH_SURROGATE;
                        final int low = cp2 - UnicodePlus.MIN_UNICODE_PLUS_LOW_SURROGATE;
                        final int joined = UnicodePlus.MIN_NON_UNICODE + (high << 10) + low;
                        addCodePoint(joined);
                        ++i;
                    } else {
                        // High surrogate without matching low surrogate
                        final String hex = Integer.toHexString(cp);
                        final String fmt = Res.fmt(Res.B_HIGH_SURROGATE_W_NO_LOW, hex);
                        Log.warning(fmt);
                    }
                } else {
                    // High surrogate without paired low surrogate
                    final String hex = Integer.toHexString(cp);
                    final String msg = Res.get(Res.B_HIGH_SURROGATE_AT_END);
                    Log.warning(msg);
                }
            } else {
                addCodePoint(cp);
            }
        }
    }
}

package dev.mathops.text;

import java.util.Arrays;

/**
 * An immutable string made up of code points (rather than UTF-16 code units), as defined in
 * <a href="https://webidl.spec.whatwg.org/#idl-USVString">WebIDL</a>.  This string supports
 * the non-Unicode extended code point range N+110000 - N+1FFFFF in addition to Unicode.
 */
public class USVString implements IUSVSequence {

    /** An empty USVString. */
    public static final USVString EMPTY = new USVString(new int[0]);

    /** The code points in the string. */
    private final int[] codePoints;

    /**
     * Constructs a new {@code USVString}.
     *
     * @param theCodePoints the array of code points
     */
    public USVString(final int[] theCodePoints) {

        this.codePoints = theCodePoints.clone();
    }

    /**
     * Constructs a new {@code USVString} by copying code points from a portion of an array.
     *
     * @param theCodePoints the array from which to copy code points
     * @param start         the index of the first code point to copy
     * @param length        the number of code points to copy
     */
    public USVString(final int[] theCodePoints, final int start, final int length) {

        this.codePoints = new int[length];
        System.arraycopy(theCodePoints, start, this.codePoints, 0, length);
    }

    /**
     * Constructs a new {@code USVString} by converting a Java string. unpaired surrogates in the source string are
     * replaced by the 0xFFFD replacement code point.
     *
     * @param theString the string with whose code points to initialize this object
     */
    public USVString(final CharSequence theString) {

        this.codePoints = theString.codePoints().toArray();

        // If there are leftover, unpaired surrogates, replace with the replacement code point
        for (int i = 0; i < this.codePoints.length; ++i) {
            final int cp = this.codePoints[i];

            if (cp >= 0xD800 && cp <= 0xDFFF) {
                this.codePoints[i] = 0xFFFD;
            }
        }
    }

    /**
     * Tests whether the list is empty.
     *
     * @return true if the list is empty; false if not
     */
    @Override
    public final boolean isEmpty() {

        return this.codePoints.length == 0;
    }

    /**
     * Gets the size of the string (the number of code points).
     *
     * @return the size
     */
    @Override
    public final int size() {

        return this.codePoints.length;
    }

    /**
     * Gets a code point.
     *
     * @param index the index of the code point to retrieve
     * @return the code point
     */
    @Override
    public final int get(final int index) {

        return this.codePoints[index];
    }

    /**
     * Gets an array of the code points in the sequence. The returned array is independent of the sequence and can be
     * modified without affecting this object.
     *
     * @return the array of code points
     */
    @Override
    public final int[] getCodePoints() {

        return this.codePoints.clone();
    }

    /**
     * Gets the hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {

        int hash = 0;

        for (final int cp : this.codePoints) {
            hash += cp;
        }

        return hash;
    }

    /**
     * Gets an array of the code points in the sequence. The returned array is independent of the sequence and can be
     * modified without affecting this object.
     *
     * @param other the sequence against which to compare
     * @return {@code true} if this sequence and {@code other} have identical code point arrays
     */
    @Override
    public final boolean equalsCodePoints(final IUSVSequence other) {

        final int[] otherCPs = other.getCodePoints();
        final int[] myCPs = getCodePoints();
        return Arrays.equals(otherCPs, myCPs);
    }

    /**
     * Tests whether this object is equal to another object.
     *
     * @param obj the object against which to test
     * @return true if this object is equal to {@code o}; false if not
     */
    @Override
    public final boolean equals(final Object obj) {

        boolean equal;

        if (obj == this) {
            equal = true;
        } else if (obj instanceof USVString u) {
            equal = this.codePoints.length == u.codePoints.length;

            if (equal) {
                for (int i = 0; i < this.codePoints.length; ++i) {
                    if (this.codePoints[i] != u.codePoints[i]) {
                        equal = false;
                        break;
                    }
                }
            }
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Generates the Java String representation of the USVString.
     *
     * @return the Java String
     */
    @Override
    public final String toString() {

        return UnicodePlus.usvArrayToString(this.codePoints, 0, this.codePoints.length);
    }
}

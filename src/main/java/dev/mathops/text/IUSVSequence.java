package dev.mathops.text;

/**
 * An interface implemented by classes that can provide an array of USV code points. Implementing classes must support
 * the non-Unicode extended code point range N+110000 - N+1FFFFF in addition to Unicode.
 */
public interface IUSVSequence {

    /**
     * The value returned to indicate end of file. If this value is retrieved at the end of a queue, further attempts to
     * retrieve data will continue to return this value.
     */
    int EOF = -1;

    /** The minimum code point value in the extended non-Unicode range. */
    int MIN_NON_UNICODE = 0x110000;

    /** The minimum code point value in the extended non-Unicode range. */
    int MAX_NON_UNICODE = 0x1FFFFF;

    /**
     * Tests whether the sequence is empty.
     *
     * @return true if the sequence is empty; false if not
     */
    boolean isEmpty();

    /**
     * Gets the size of the sequence (the number of code points).
     *
     * @return the size
     */
    int size();

    /**
     * Gets a code point.
     *
     * @param index the index of the code point to retrieve
     * @return the code point
     */
    int get(int index);

    /**
     * Gets an array of the code points in the sequence. The returned array is independent of the sequence and can be
     * modified without affecting this object.
     *
     * @return the array of code points
     */
    int[] getCodePoints();

    /**
     * Tests whether two code point sequences have identical code point arrays.
     *
     * @param other the sequence against which to compare
     * @return {@code true} if this sequence and {@code other} have identical code point arrays
     */
    boolean equalsCodePoints(IUSVSequence other);

    /**
     * Generates the string representation, which is the Java string formed by doing a UTF-16 encoding of the sequence
     * of code points.  This method supports encoding of the non-Unicode extended code point range N+110000 - N+1FFFFF
     * using pairs of code units from the private use area.
     *
     * @return the string representation
     */
    @Override
    String toString();
}

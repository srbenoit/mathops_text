package dev.mathops.text;

/**
 * An interface implemented by classes that can provide an array of bytes.
 */
public interface IByteSequence {

    /**
     * The value returned to indicate end of file. If this value is retrieved at the end of a queue, further attempts to
     * retrieve data will continue to return the same value.
     */
    int EOF = -1;

    /**
     * Tests whether the list is empty.
     *
     * @return true if the list is empty; false if not
     */
    boolean isEmpty();

    /**
     * Gets the size of the string (the number of bytes).
     *
     * @return the size
     */
    int size();

    /**
     * Gets a byte
     *
     * @param index the index of the byte to retrieve
     * @return the byte (an integer in the range 0 - 255), or EOF to indicate {@code index} lies beyond the end of data
     */
    int get(int index);

    /**
     * Generates the string representation, which is the Java string formed by doing a UTF-8 decoding of the sequence of
     * bytes.
     *
     * @return the string representation
     */
    @Override
    String toString();
}

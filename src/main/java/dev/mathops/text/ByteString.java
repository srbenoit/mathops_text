package dev.mathops.text;

import java.nio.charset.StandardCharsets;

/**
 * An immutable string made up of bytes, as defined in
 * <a href="https://infra.spec.whatwg.org/#bytes">Infra</a>. This class uses int values to store
 * bytes so their values will be non-negative numbers from 0x00 to 0xFF.
 */
public class ByteString implements IByteSequence {

    /** An empty ByteString. */
    public static final ByteString EMPTY = new ByteString(new int[0]);

    /** The bytes in the string. */
    private final int[] data;

    /**
     * Constructs a new {@code ByteString}.
     *
     * @param theBytes the array of bytes
     */
    public ByteString(final byte[] theBytes) {

        this.data = new int[theBytes.length];

        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = (int) theBytes[i] & 0xFF;
        }
    }

    /**
     * Constructs a new {@code ByteString}.
     *
     * @param theBytes the array of integers whose values will be masked to their least significant 8 bits and treated
     *                 as unsigned bytes
     */
    public ByteString(final int[] theBytes) {

        this.data = new int[theBytes.length];

        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = theBytes[i] & 0xFF;
        }
    }

    /**
     * Constructs a new {@code ByteString} by copying bytes from a portion of an array.
     *
     * @param theBytes the array from which to copy bytes
     * @param start    the index of the first byte to copy
     * @param length   the number of bytes to copy
     */
    public ByteString(final byte[] theBytes, final int start, final int length) {

        this.data = new int[length];

        for (int i = 0; i < length; ++i) {
            this.data[i] = (int) theBytes[start + i] & 0xFF;
        }
    }

    /**
     * Constructs a new {@code ByteString} by copying bytes from a portion of an array.
     *
     * @param theBytes the array from which to copy bytes (array entries will be masked to their least significant 8
     *                 bits and treated as unsigned bytes)
     * @param start    the index of the first byte to copy
     * @param length   the number of bytes to copy
     */
    public ByteString(final int[] theBytes, final int start, final int length) {

        this.data = new int[length];

        for (int i = 0; i < length; ++i) {
            this.data[i] = theBytes[start + i] & 0xFF;
        }
    }

    /**
     * Tests whether the list is empty.
     *
     * @return true if the list is empty; false if not
     */
    @Override
    public final boolean isEmpty() {

        return this.data.length == 0;
    }

    /**
     * Gets the size of the string (the number of bytes).
     *
     * @return the size
     */
    @Override
    public final int size() {

        return this.data.length;
    }

    /**
     * Gets a byte.
     *
     * @param index the index of the byte to retrieve
     * @return the byte
     */
    @Override
    public final int get(final int index) {

        return index >= this.data.length ? EOF : this.data[index];
    }

    /**
     * Retrieves the byte data as a Java byte array.
     *
     * @return the byte array
     */
    public final byte[] asByteArray() {

        final byte[] result = new byte[this.data.length];

        for (int i = 0; i < this.data.length; ++i) {
            result[i] = (byte) this.data[i];
        }

        return result;
    }

    /**
     * Gets the hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {

        int hash = 0;

        for (final int cp : this.data) {
            hash += cp;
        }

        return hash;
    }

    /**
     * Tests whether this object is equal to another object.
     *
     * @param obj the object against which to test
     * @return true if this object is equal to {@code obj}; false if not
     */
    @Override
    public final boolean equals(final Object obj) {

        boolean equal;

        if (obj == this) {
            equal = true;
        } else if (obj instanceof final ByteString u) {
            equal = this.data.length == u.data.length;

            if (equal) {
                for (int i = 0; i < this.data.length; ++i) {
                    if (this.data[i] != u.data[i]) {
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
     * Generates the Java String representation of the ByteString.
     *
     * @return the Java String
     */
    @Override
    public final String toString() {

        final byte[] bytes = asByteArray();
        return new String(bytes, 0, this.data.length, StandardCharsets.UTF_8);
    }
}

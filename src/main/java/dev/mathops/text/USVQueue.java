package dev.mathops.text;

import java.util.Arrays;

/**
 * A "list" whose items are Unicode code points. This class is designed to act as a stack or queue as well as a list,
 * and serves as the code-point "I/O queue" object. It is essentially a mutable USVString.
 *
 * <p>
 * NOTE: This list does not implement the "sort in ascending order" or "sort in descending order" algorithms. If it is
 * found that these are ever executed on a list containing code points, they will be implemented at that time, and it
 * does not implement the "Stack" operations.
 *
 * <p>
 * This class implements the algorithms for a "Queue", allowing it to be used as those types directly. It can serve as
 * the code point queue to which a byte stream is decoded, or from which a byte stream is encoded.
 */
public class USVQueue implements IUSVSequence {

    /** Mask for USV values. */
    public static final int MASK = 0x7FFFFFFF;

    /** The value returned to indicate the queue is empty but not yet at end of file. */
    public static final int QUEUE_EMPTY = -1;

    /** The code points in the list. */
    private int[] data;

    /**
     * The position in the int array of the first element in the list. Tracking this allows "remove the first item" to
     * be performed by a simple integer increment.
     */
    private int start;

    /** The position in the int array after the last element in the list. */
    private int end;

    /**
     * A flag indicating EOF has been enqueued, which prevents further data from being enqueued. Data may still be
     * inserted before the EOF marker.
     */
    private boolean eofQueued;

    /**
     * Constructs a new {@code USVQueue}.
     *
     * @param initialCapacity the initial capacity of the list (clamped to the range 10 to 1000000)
     */
    public USVQueue(final int initialCapacity) {

        final int max = Math.max(10, initialCapacity);
        final int clamped = Math.min(max, 1000000);

        this.data = new int[clamped];
    }

    /**
     * Constructs a new {@code USVQueue}.
     *
     * @param seq the string whose code points should initialize the code point list
     */
    public USVQueue(final CharSequence seq) {

        this.data = seq.codePoints().toArray();
    }

    /**
     * Constructs a new {@code USVQueue} whose data is taken from another.
     *
     * @param src the list whose data to copy
     */
    public USVQueue(final IUSVSequence src) {

        final int len = src.size();

        this.data = src.getCodePoints();
        this.start = 0;
        this.end = len;
    }

    /**
     * Constructs a new {@code USVQueue} whose data is taken from another.
     *
     * @param theData   the source data buffer
     * @param theStart  the start offset from which to copy data
     * @param theLength the number of bytes to copy from the source buffer
     */
    public USVQueue(final int[] theData, final int theStart, final int theLength) {

        this.data = new int[theLength];

        for (int i = 0; i < theLength; ++i) {
            this.data[i] = theData[theStart + i] & MASK;
        }

        this.start = 0;
        this.end = theLength;
    }

    /**
     * Gets the code point at a specified index.
     *
     * @param index the index
     * @return the code point (-1 to indicate EOF)
     */
    @Override
    public final int get(final int index) {

        return this.start + index >= this.end ? EOF : this.data[this.start + index];
    }

    /**
     * Gets the index the next code point to be returned by {@code consume} or {@code dequeue}.
     *
     * @return the code point index
     */
    public final int getCurrentIndex() {

        return this.start;
    }

    /**
     * Sets the index the next code point to be returned by {@code consume} or {@code dequeue} (used to restore the
     * condition as of the last call to {@code getCurrentIndex}).
     *
     * @param newStart the new start code point index
     */
    public final void setCurrentIndex(final int newStart) {

        this.start = newStart;
    }

    /**
     * Gets the size of the list (the number of code points).
     *
     * @return the size
     */
    @Override
    public final int size() {

        return this.end - this.start;
    }

    /**
     * Appends a code point to the list. If {@code indicateEndOfFile} has already been called, this has no effect.
     *
     * @param item the int to append
     * @return this, for invocation chaining
     */
    public final USVQueue append(final int item) {

        if (!this.eofQueued) {
            if (this.end >= this.data.length) {
                extendStorage();
            }

            this.data[this.end] = item & MASK;
            ++this.end;
        }

        return this;
    }

    /**
     * Appends the code points from a string to the list. If {@code indicateEndOfFile} has already been called, this has
     * no effect.
     *
     * @param seq the string whose code points to append
     * @return this, for invocation chaining
     */
    public final USVQueue append(final IUSVSequence seq) {

        if (!this.eofQueued) {
            final int len = seq.size();
            for (int i = 0; i < len; ++i) {
                final int item = seq.get(i);
                append(item);
            }
        }

        return this;
    }

    /**
     * Appends the code points from a string to the list. If {@code indicateEndOfFile} has already been called, this has
     * no effect.
     *
     * @param seq the string whose code points to append
     * @return this, for invocation chaining
     */
    public final USVQueue append(final CharSequence seq) {

        if (!this.eofQueued) {
            for (final int cp : seq.codePoints().toArray()) {
                append(cp);
            }
        }

        return this;
    }

    /**
     * Appends the EOF code to the end of the stream.
     */
    public final void indicateEndOfFile() {

        this.eofQueued = true;
    }

    /**
     * Extends this list by appending all items from a given list. If {@code indicateEndOfFile} has already been called,
     * this has no effect.
     *
     * @param list the list to append
     */
    public final void extend(final USVQueue list) {

        if (!this.eofQueued) {
            final int lengthToAdd = list.size();

            if (this.end + lengthToAdd > this.data.length) {
                // Extend using the length to add to control how much space to allocate
                final int growSize = lengthToAdd * 6 / 5;
                final int newLen = this.data.length + growSize;
                final int[] newData = new int[newLen];
                System.arraycopy(this.data, 0, newData, 0, this.end);
                this.data = newData;
            }

            System.arraycopy(list.data, list.start, this.data, this.end, lengthToAdd);
            this.end += lengthToAdd;
        }
    }

    /**
     * Prepends a code point to the list.
     *
     * @param item the code point to prepend
     */
    public final void prepend(final int item) {

        if (this.start == 0) {
            if (this.end == this.data.length) {
                extendStorage();
            }

            // Shift by 1/2 the extra space
            final int len = this.end - this.start;
            final int extra = this.data.length - len;
            final int shift = Math.max(1, extra / 2);

            System.arraycopy(this.data, this.start, this.data, this.start + shift, len);
            this.start += shift;
            this.end += shift;
        }

        --this.start;
        this.data[this.start] = item & MASK;
    }

    /**
     * Replaces all code points that match a given code point with a replacement int.
     *
     * @param match       the match int
     * @param replacement the replacement code point
     */
    public final void replace(final int match, final int replacement) {

        final int m = match & MASK;
        final int r = replacement & MASK;

        for (int i = this.start; i < this.end; ++i) {
            if (this.data[i] == m) {
                this.data[i] = r;
            }
        }
    }

    /**
     * Replaces all code points that match a given condition with a replacement code point.
     *
     * @param match       the match condition
     * @param replacement the replacement code point
     */
    public final void replace(final Condition match, final int replacement) {

        final int r = replacement & MASK;

        for (int i = this.start; i < this.end; ++i) {
            if (match.test(this.data[i])) {
                this.data[i] = r;
            }
        }
    }

    /**
     * Inserts a code point into the list before a given index.
     *
     * @param item        the code point to insert
     * @param beforeIndex the index before which to insert the code point
     */
    public final void insert(final int item, final int beforeIndex) {

        if (this.end == this.data.length) {
            extendStorage();
        }

        final int len = this.end - this.start;
        System.arraycopy(this.data, this.start + beforeIndex, this.data,
                this.start + beforeIndex + 1, len - beforeIndex);
        ++this.end;

        this.data[this.start + beforeIndex] = item & MASK;
    }

    /**
     * Removes all code points that match a given code point from this list.
     *
     * @param match the code point to remove
     */
    public final void remove(final int match) {

        final int m = match & MASK;

        int pos = this.start;
        for (int i = this.start; i < this.end; ++i) {
            if (this.data[i] != m) {
                this.data[pos] = this.data[i];
                ++pos;
            }
        }
        this.end = pos;
    }

    /**
     * Removes all code points that match a given condition from this list.
     *
     * @param match the code point to remove
     */
    public final void remove(final Condition match) {

        int pos = this.start;
        for (int i = this.start; i < this.end; ++i) {
            if (!match.test(this.data[i])) {
                this.data[pos] = this.data[i];
                ++pos;
            }
        }
        this.end = pos;
    }

    /**
     * Empties the list.
     */
    public final void empty() {

        this.end = this.start;
    }

    /**
     * Tests whether the list is empty.
     *
     * @return true if the list is empty; false if not
     */
    @Override
    public final boolean isEmpty() {

        return this.end == this.start;
    }

    /**
     * Finds the index of the first occurrence of a code point in the list. This can be used for the "contains"
     * operation.
     *
     * @param cp the code point for which to search
     * @return the index of the first instance; -1 if the list does not contain the code point
     */
    public final int indexOf(final int cp) {

        int result = -1;

        for (int i = this.start; i < this.end; ++i) {
            if (this.data[i] == cp) {
                result = i - this.start;
                break;
            }
        }

        return result;
    }

    /**
     * Creates a clone of this list. The clone's data array will be exactly the size of this list's actual data (so it
     * will likely be smaller than this list in memory). This can be used to compact a list - replacing the list by its
     * clone.
     *
     * @return the duplicate {code USVQueue}
     */
    public final USVQueue duplicate() {

        return new USVQueue(this);
    }

    /**
     * Enqueues a code point to the tail of the queue.
     *
     * @param item the code point to enqueue
     */
    public final void enqueue(final int item) {

        append(item);
    }

    /**
     * Dequeues the code point at the head of the queue.
     *
     * @return the dequeued code point (a value from 0 to 0x10FFFF), EOF to indicate end of file, or "QUEUE_EMPTY" if
     *         the queue was empty but EOF has not been enqueued.
     */
    public final int dequeue() {

        return consume();
    }

    /**
     * Returns but does not remove the item at the head of the queue.
     *
     * @return the item
     */
    public final int peek() {

        return this.end == this.start ? -1 : this.data[this.start];
    }

    /**
     * Returns but does not remove the i-th item following the item at the head of the queue.
     *
     * @param i the number of code points to skip before peeking an item
     * @return the item
     */
    public final int peekPlus(final int i) {

        return this.end <= this.start + i ? -1 : this.data[this.start + i];
    }

    /**
     * Returns the next code points at a specified position in the queue, without affecting the queue at all. This
     * method does not do range checking, so callers must ensure their request falls within the range of queued data.
     *
     * @param rangeStart the first index to retrieve
     * @param rangeEnd   the index after the last index to retrieve
     * @param target     the int array into which to store retrieved code points
     */
    public final void peek(final int rangeStart, final int rangeEnd, final int[] target) {

        System.arraycopy(this.data, this.start + rangeStart, target, 0, rangeEnd - rangeStart);
    }

    /**
     * Consumes and returns the next code point.
     *
     * @return the next code point; EOF if there are no more code points in the stream
     */
    public final int consume() {

        final int result;

        if (this.end == this.start) {
            result = -1;
        } else {
            result = this.data[this.start];
            ++this.start;
        }

        return result;
    }

    /**
     * Consumes the next N code points.
     *
     * @param n the number of code points to consume
     */
    public final void consume(final int n) {

        this.start = Math.min(this.start + n, this.end);
    }

    /**
     * "Reconsumes" the most recent code point. This simply reduces the current code point index by one unit so the most
     * recently consumed code point will be returned again by the next call to {@code consume}.
     */
    public final void reconsume() {

        if (this.start > 0) {
            --this.start;
        }
    }

    /**
     * Tests whether the next N code points in this list match the code points of a given string of length N.
     *
     * @param seq the string (it is assumed that each Java character is a code point)
     * @return true if {@code str} matches the next N code points in this list; false if not
     */
    public final boolean peekEquals(final CharSequence seq) {

        boolean result;

        final int len = seq.length();
        if (this.start + len >= this.end) {
            result = false;
        } else {
            result = true;
            for (int i = 0; i < len; ++i) {
                if (this.data[this.start + i] != (int) seq.charAt(i)) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Tests whether this list's code points match the code points of a given string.
     *
     * @param seq the string (it is assumed that each Java character is a code point)
     * @return true if {@code str} matches the code points in this list; false if not
     */
    public final boolean equalsString(final CharSequence seq) {

        boolean result;

        final int len = seq.length();
        if (this.start + len == this.end) {
            result = true;
            for (int i = 0; i < len; ++i) {
                if (this.data[this.start + i] != (int) seq.charAt(i)) {
                    result = false;
                    break;
                }
            }
        } else {
            result = false;
        }

        return result;
    }

    /**
     * Extends storage. Storage grows by the smallest of half the current data size and 1 million bytes.
     */
    private void extendStorage() {

        final int growSize = Math.min(this.data.length / 2, 1000000);
        final int newLen = this.data.length + growSize;
        final int[] newData = new int[newLen];
        System.arraycopy(this.data, 0, newData, 0, this.end);
        this.data = newData;
    }

    /**
     * Gets an array of the code points in the sequence. The returned array is independent of the sequence and can be
     * modified without affecting this object.
     *
     * @return the array of code points
     */
    @Override
    public final int[] getCodePoints() {

        final int len = this.end - this.start;
        final int[] result = new int[len];
        System.arraycopy(this.data, this.start, result, 0, len);

        return result;
    }

    /**
     * Computes a hash code for the list.
     */
    @Override
    public final int hashCode() {

        int hash = 0;

        for (int i = this.start; i < this.end; ++i) {
            hash += this.data[i];
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
     * Tests whether this object is equal to another.
     *
     * @param obj the other object
     */
    @Override
    public final boolean equals(final Object obj) {

        boolean equal;

        if (obj == this) {
            equal = true;
        } else if (obj instanceof USVQueue list) {
            final int sz = size();
            if (sz == list.size()) {
                equal = true;
                for (int i = 0; i < sz; ++i) {
                    if (get(i) != list.get(i)) {
                        equal = false;
                        break;
                    }
                }
            } else {
                equal = false;
            }
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Generates the string representation, which is the Java string formed by doing a UTF-16 encoding of the sequence
     * of code points.
     *
     * @return the string representation
     */
    @Override
    public final String toString() {

        return UnicodePlus.usvArrayToString(this.data, this.start, this.end);
    }

    /**
     * Generates a USVString with this object's code points.
     *
     * @return the USVString
     */
    public final USVString toUSVString() {

        return new USVString(this.data, this.start, this.end - this.start);
    }

    /**
     * A condition that can be used to select bytes to replace.
     */
    public interface Condition {

        /**
         * Tests whether an int satisfies the condition.
         *
         * @param cp the int to test
         * @return true if the int satisfies the condition
         */
        boolean test(int cp);
    }
}

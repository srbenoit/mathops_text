package dev.mathops.text.lexparse;

/**
 * The base class for tokens. This base class tracks the starting position and length of the token.
 */
public abstract class AbstractToken implements IToken {

    /** The start position in the source string. */
    private final int startPosition;

    /** The number of characters in the source string that were matched to produce this token. */
    private final int length;

    /**
     * Constructs a new {@code AbstractToken}.
     *
     * @param theStartPosition the start position
     * @param theLength the length
     */
    protected AbstractToken(final int theStartPosition, final int theLength) {

        this.startPosition = theStartPosition;
        this.length = theLength;
    }

    /**
     * Gets the offset in the source string where this token's pattern begins.
     *
     * @return the start position
     */
    @Override
    public final int getStartPosition() {

        return this.startPosition;
    }

    /**
     * Gets the number of characters in the source string that were matched to produce this token.
     *
     * @return the number of characters
     */
    @Override
    public final int getLength() {

        return this.length;
    }

    /**
     * Generates a string representation of the token.
     *
     * @return the string representation
     */
    @Override
    public abstract String toString();
}

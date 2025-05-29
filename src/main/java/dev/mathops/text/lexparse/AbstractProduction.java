package dev.mathops.text.lexparse;

/**
 * The base class for objects produced by the parser. This base class tracks the starting position in the source token
 * list and length (in tokens) of the object.
 */
public abstract class AbstractProduction implements IProduction {

    /** The start position in the source token list. */
    private final int startPosition;

    /** The number of tokens in the source list that were matched to produce this object. */
    private final int length;

    /**
     * Constructs a new {@code AbstractProduction}.
     *
     * @param theStartPosition the position in the source token list where this production's pattern begins
     * @param theLength the number of tokens in the source string that were matched to produce this production
     */
    protected AbstractProduction(final int theStartPosition, final int theLength) {

        this.startPosition = theStartPosition;
        this.length = theLength;
    }

    /**
     * Gets the offset in the source token list where this production's pattern begins.
     *
     * @return the start position
     */
    @Override
    public final int getStartPosition() {

        return this.startPosition;
    }

    /**
     * Gets the number of tokens in the source string that were matched to produce this production.
     *
     * @return the number of tokens
     */
    @Override
    public final int getLength() {

        return this.length;
    }
}

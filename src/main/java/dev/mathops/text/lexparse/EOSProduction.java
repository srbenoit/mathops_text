package dev.mathops.text.lexparse;

/**
 * A general "end of stream" production that is returned by the syntactic scanner if scanning past the end of the source
 * token list.
 */
public final class EOSProduction extends AbstractProduction {

    /**
     * Constructs a new {@code EOSProduction}.
     *
     * @param theStartPosition the start position (the length of the source token list)
     */
    public EOSProduction(final int theStartPosition) {

        super(theStartPosition, 0);
    }
}

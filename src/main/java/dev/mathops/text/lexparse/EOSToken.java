package dev.mathops.text.lexparse;

/**
 * A general "end of stream" token that is returned by the lexical scanner if scanning past the end of the source
 * string.
 */
public final class EOSToken extends AbstractToken {

    /**
     * Constructs a new {@code EOSToken}.
     *
     * @param theStartPosition the start position (the length of the source string)
     */
    public EOSToken(final int theStartPosition) {

        super(theStartPosition, 0);
    }

    /**
     * Generates a string representation of the token.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        return "[EOS]";
    }
}

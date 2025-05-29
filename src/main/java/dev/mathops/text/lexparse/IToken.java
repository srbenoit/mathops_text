package dev.mathops.text.lexparse;

/**
 * The interface for all tokens that can be produced by patterns matching strings.
 */
public interface IToken {

    /**
     * Gets the position within the source string where this token's pattern begins.
     *
     * @return the start position
     */
    int getStartPosition();

    /**
     * Gets the number of characters in the source string that were matched to produce this token.
     *
     * @return the number of characters
     */
    int getLength();

    /**
     * Generates a string representation of the token.
     *
     * @return the string representation
     */
    @Override
    String toString();
}

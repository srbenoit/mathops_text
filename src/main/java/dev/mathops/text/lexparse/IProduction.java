package dev.mathops.text.lexparse;

/**
 * The interface for all tokens that can be produced by patterns matching strings.
 */
public interface IProduction {

    /**
     * Gets the position within the source token list where this production's pattern begins.
     *
     * @return the start position
     */
    int getStartPosition();

    /**
     * Gets the number of tokens in the source string that were matched to produce this production.
     *
     * @return the number of tokens
     */
    int getLength();
}

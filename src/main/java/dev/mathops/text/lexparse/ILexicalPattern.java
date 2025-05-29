package dev.mathops.text.lexparse;

/**
 * The interface for a lexical pattern that maps to a token.
 */
public interface ILexicalPattern {

    /**
     * Attempts to match a pattern and produce a token. The token corresponding to the longest sequence of characters
     * that matches an allowed pattern is returned. {code null} is returned if no allowed pattern matches the string at
     * the specified position.
     *
     * @param codePoints the string, as a sequence of Unicode code points
     * @param pos        the start position in the string
     * @return the token; null if none matched
     */
    IToken match(int[] codePoints, int pos);
}

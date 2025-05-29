package dev.mathops.text.lexparse;

import java.util.List;

/**
 * The interface for a syntactic pattern that maps to an object.
 */
public interface ISyntacticPattern {

    /**
     * Attempts to match a pattern of tokens and produce an object. The object corresponding to the longest sequence of
     * tokens that matches an allowed pattern is returned. Null is returned if no allowed pattern matches the token list
     * at the specified position.
     *
     * @param tokens the list of tokens
     * @param start  the start position in the token list
     * @return the object; null if none matched
     */
    IProduction match(List<IToken> tokens, int start);
}

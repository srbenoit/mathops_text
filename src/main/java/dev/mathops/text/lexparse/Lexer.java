package dev.mathops.text.lexparse;

import dev.mathops.commons.log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A lexical scanner ("lexer"), constructed with a source string and a list of allowed lexical patterns.
 */
public class Lexer {

    /** The source string, as a sequence of Unicode code points. */
    private final int[] sourceString;

    /** The lexical grammar. */
    private final List<ILexicalPattern> grammar;

    /** The current position in the source string. */
    private int pos;

    /**
     * Constructs a new {@code Lexer}.
     *
     * @param theSourceString the string the lexer will scan for pattern matches
     * @param theGrammar      the list of lexical patterns allowed (a lexical grammar)
     */
    public Lexer(final CharSequence theSourceString, final List<ILexicalPattern> theGrammar) {

        if (theSourceString == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        if (theGrammar == null || theGrammar.isEmpty()) {
            throw new IllegalArgumentException("Grammar may not be null or empty");
        }

        this.sourceString = theSourceString.codePoints().toArray();
        this.grammar = new ArrayList<>(theGrammar);

        this.pos = 0;
    }

    /**
     * Constructs a new {@code Lexer}.
     *
     * @param theSourceString the string the lexer will scan for pattern matches, as a sequence of Unicode code points
     * @param theGrammar      the list of lexical patterns allowed (a lexical grammar)
     */
    public Lexer(final int[] theSourceString, final List<ILexicalPattern> theGrammar) {

        if (theSourceString == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        if (theGrammar == null || theGrammar.isEmpty()) {
            throw new IllegalArgumentException("Grammar may not be null or empty");
        }

        this.sourceString = theSourceString.clone();
        this.grammar = new ArrayList<>(theGrammar);

        this.pos = 0;
    }

    /**
     * Consumes one token from the source string. This method can be used by an incremental scanner that wants to
     * consume tokens as it is parsing a syntactic grammar.
     *
     * @return the next token; {@code null} if a token cannot be matched by any lexical pattern, an {@code EOSToken} if
     *         no more characters remain in the source string
     */
    public final IToken consumeToken() {

        IToken result;

        if (this.pos >= this.sourceString.length) {
            result = new EOSToken(this.sourceString.length);
        } else {
            result = null;
            int longest = 0;
            for (final ILexicalPattern test : this.grammar) {
                final IToken match = test.match(this.sourceString, this.pos);
                if (match != null && match.getLength() > longest) {
                    result = match;
                    longest = match.getLength();
                }
            }

            if (result == null) {
                Log.warning("Unable to recognize next token");
            } else {
                this.pos += longest;
            }
        }

        return result;
    }

    /**
     * Scans the entire source string, and returns the matching collection of tokens.
     *
     * @param tokens the list to which to add all scanned tokens
     * @return {@code true} if the entire source string was scanned and the last token in {@code tokens} is an
     *         {@code EOSToken}; {@code false} if scanning reached a point where no pattern matched the string, in which
     *         case the last token in {@code tokens} can locate the end of the successfully scanned portion of the
     *         source string
     */
    public final boolean scan(final Collection<? super IToken> tokens) {

        boolean result = true;

        this.pos = 0;
        for (; ; ) {
            final IToken token = consumeToken();
            if (token == null) {
                result = false;
                break;
            }
            Log.info("Adding token: ", token, " of length " + token.getLength());
            tokens.add(token);
            if (token instanceof EOSToken) {
                break;
            }
        }

        return result;
    }
}

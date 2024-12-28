package dev.mathops.text.parser.xml;

import dev.mathops.text.parser.ParsingException;

/**
 * A token that represents the start tag of a nonempty element, beginning with < and ending with >, with no < or >
 * characters between.
 */
final class TokSTag extends AbstractXmlTagToken {

    /**
     * Constructs a new {@code TokSTag}.
     *
     * @param theContent the content this token belongs to
     * @param theStart   the index of the '<' character
     * @param theEnd     the index after '>' character
     * @param theLineNumber the line number of the start of the span
     * @param theColumn     the column of the start of the span
     */
    TokSTag(final XmlContent theContent, final int theStart, final int theEnd,
            final int theLineNumber, final int theColumn) {

        super(theContent, theStart, theEnd, theLineNumber, theColumn);
    }

    /**
     * Generates the string representation of the TokSTag token.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        return genString("STag: ");
    }

    /**
     * Validates the contents of the TokSTag token, which may store internal structure that can be used when the token
     * in assembled into a node.
     *
     * @throws ParsingException if the contents are invalid
     */
    @Override
    public void validate() throws ParsingException {

        extractNameAndAttributes(1);
    }
}

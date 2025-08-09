package dev.mathops.text.model;

/**
 * A log entry.
 *
 * @param type      the entry type
 * @param startLine the 0-based line index of the first character of marked content
 * @param startCol  the 0-based column index of the first character of marked content
 * @param endLine   the 0-based line index of the last character of marked content
 * @param endCol    the 0-based column index of the last character of marked content (this could hold the length of the
 *                  line to indicate content was missing at the end of the line)
 * @param message   the message
 */
public record ParsingLogEntry(ParsingLogEntryType type, int startLine, int startCol, int endLine, int endCol,
                              String message) {

    /**
     * Generates a string representation of the log message.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        final String myMessage = message();
        final ParsingLogEntryType myType = type();
        final int myStartLine = startLine();
        final int myStartCol = startCol();
        final int myEndLine = endLine();
        final int myEndCol = endCol();

        final int len = myMessage.length() + 100;
        final StringBuilder builder = new StringBuilder(len);

        final String typeName = myType.name();
        builder.append(typeName);
        builder.append(" (Line ");
        builder.append(myStartLine);
        builder.append(" Column ");
        builder.append(myStartCol);

        if (myStartLine != myEndLine || myStartCol != myEndCol) {
            // Character range error
            builder.append(" to Line ");
            builder.append(myEndLine);
            builder.append(" Column ");
            builder.append(myEndCol);
        }

        builder.append("): ");
        builder.append(myMessage);

        return builder.toString();
    }
}

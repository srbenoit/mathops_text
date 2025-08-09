package dev.mathops.text.parser;

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

}

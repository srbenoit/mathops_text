package dev.mathops.text.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for errors, warnings, and notes logged during parsing.  This object is provided to a parser, accumulates
 * messages during a parsing operation, and can present those results to the user, either in the form of a log, or by
 * highlighting and annotating regions of the source document.
 */
public final class ParsingLog {

    /** Log entries. */
    private final List<ParsingLogEntry> entries;

    /**
     * Constructs a new {@code ParsingLog}.
     *
     * @param initialSize the initial size list of entries to allocate
     */
    public ParsingLog(final int initialSize) {

        this.entries = new ArrayList<>(initialSize);
    }

    /**
     * Adds a log entry.
     *
     * @param entry the entry
     */
    public void add(final ParsingLogEntry entry) {

        this.entries.add(entry);
    }

    /**
     * Constructs and adds a log entry that refers to a range of characters.
     *
     * @param type      the entry type
     * @param startLine the 0-based line index of the first character of marked content
     * @param startCol  the 0-based column index of the first character of marked content
     * @param endLine   the 0-based line index of the last character of marked content
     * @param endCol    the 0-based column index of the last character of marked content
     * @param message   the message
     */
    public void add(ParsingLogEntryType type, int startLine, int startCol, int endLine, int endCol,
                    String message) {

        final ParsingLogEntry entry = new ParsingLogEntry(type, startLine, startCol, endLine, endCol, message);

        this.entries.add(entry);
    }

    /**
     * Constructs and adds a log entry that refers to a single character.
     *
     * @param type    the entry type
     * @param line    the 0-based line index of the only character of marked content
     * @param col     the 0-based column index of the only character of marked content
     * @param message the message
     */
    public void add(ParsingLogEntryType type, int line, int col, String message) {

        final ParsingLogEntry entry = new ParsingLogEntry(type, line, col, line, col, message);

        this.entries.add(entry);
    }

    /**
     * Gets the number of log entries.
     *
     * @return the number of entries
     */
    public int getNumEntries() {

        return this.entries.size();
    }

    /**
     * Gets a particular log entry.
     *
     * @param index the 0-based index
     * @return the entry
     */
    public ParsingLogEntry getEntry(final int index) {

        return this.entries.get(index);
    }

    /**
     * Tests whether this log contains one or more entries whose type is "ERROR".
     *
     * @return {@code false} if an ERROR entry was found; {@code true} if not
     */
    public boolean isValid() {

        boolean valid = true;

        for (final ParsingLogEntry entry : this.entries) {
            if (entry.type() == ParsingLogEntryType.ERROR) {
                valid = false;
                break;
            }
        }

        return valid;
    }
}

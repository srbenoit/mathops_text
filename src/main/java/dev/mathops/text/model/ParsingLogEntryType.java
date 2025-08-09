package dev.mathops.text.model;

/**
 * Possible types of log entry.
 */
public enum ParsingLogEntryType {

    /** The marked content represents an error. */
    ERROR,

    /** The log entry is a warning about the marked content. */
    WARNING,

    /** The marked content was ignored. */
    IGNORED,

    /** Potentially useful information about the marked content, which was valid. */
    INFO;
}

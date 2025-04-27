package dev.mathops.text.parser.file;

/**
 * Types of annotation.
 */
public enum EAnnotationType {

    /** An indication that the file content represents an error. */
    ERROR,

    /** A warning that file content is valid but somehow not optimal or recommended. */
    WARNING,

    /** General information attached to a span. */
    INFO,

    /** A spelling error. */
    SPELLING_ERROR,

    /** A grammar error. */
    GRAMMAR_ERROR,

    /** A syntax highlight. */
    SYNTAX_HIGHLIGHT,

    /** A search match. */
    SEARCH_MATCH,

    /**
     * A highlight of a match to a syntax object, like the matching closing tag to a marked opening tag in XML or a
     * matching delimiter or fence symbol.
     */
    SYNTAX_MATCH,

    /** A selection region for operations like copying text to the clipboard. */
    SELECTION,
}

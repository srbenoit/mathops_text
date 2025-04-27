package dev.mathops.text.parser.file;

/**
 * Styles of decoration that can be applied to a span of content.  Note that only the CARET style will be visible if the
 * span has zero length (it appears as a single caret below the span's position).
 */
public enum EDecorationStyle {

    /** Single underline. */
    UNDERLINE,

    /** Double underline. */
    DOUBLE_UNDERLINE,

    /** Dotted underline. */
    DOTTED_UNDERLINE,

    /** Wavy underline. */
    WAVY_UNDERLINE,

    /** Single strikethrough. */
    STRIKETHROUGH,

    /** Double strikethrough. */
    DOUBLE_STRIKETHROUGH,

    /** Dotted strikethrough. */
    DOTTED_STRIKETHROUGH,

    /** Wavy strikethrough. */
    WAVY_STRIKETHROUGH,

    /** Single outline. */
    OUTLINE,

    /** Double outline. */
    DOUBLE_OUTLINE,

    /** Dotted outline. */
    DOTTED_OUTLINE,

    /** Highlighted background. */
    HIGHLIGHT,

    /** Drop shadow drawn behind the text with x/y offset. */
    DROP_SHADOW,

    /** A caret between each pair of characters contained in the span, and including carets at the start and end. */
    CARET,
}

package dev.mathops.text.parser.file;

/**
 * Possible ways a character position can intersect a span.
 */
public enum ESpanIntersection {

    /** The position is not included in or an endpoint of the span. */
    EXCLUDED,

    /** The position is both the start and end of a zero-length span. */
    START_AND_END,

    /** The position is the left end of a nonzero length span. */
    START,

    /** The position is the right end of a nonzero-length span. */
    END,

    /** The position lies in the interior of the span. */
    INTERIOR,
}

package dev.mathops.text.parser.file;

import dev.mathops.commons.CoreConstants;
import dev.mathops.text.builder.SimpleBuilder;

import java.util.Objects;

/**
 * A span of character data from a source file with optional annotation.  Annotations can be used to associate
 * warning/error or structure information with character file content, and can drive visual representations such as
 * syntax highlighting or marking of errors or warnings in the source file.
 *
 * @param startLine  The index of the line on which the span begins.
 * @param startCol   The index of the column at which the span starts. This position should not fall at the end of a
 *                   line. It must be true that {@code startCol >= 0} and {@code startCol < len(startLine)}.
 * @param endLine    The index of the line on which the span begins.  It must be true that
 *                   {@code endLine >= startLine}.
 * @param endCol     The index of the column before which the span ends.  This position should not lie at the start of a
 *                   line * unless this object represents a zero-length span at the start of a line.  It must be true
 *                   that * {@code endCol <= len(endLine)} and if {@code startLine = endLine}, then
 *                   {@code endCol >= startCol}.
 * @param annotation The annotation to attach to the file span (null values allowed of this object is only being * used
 *                   to track s span of content).
 */
public record LineOrientedFileSpan(int startLine, int startCol, int endLine, int endCol,
                                   FileSpanAnnotation annotation) {
    /** The left bracket. */
    private static final String LEFT = " [";

    /** The right bracket. */
    private static final String RIGHT = "]";

    /**
     * Constructs a new {@code LineOrientedFileSpan}.
     *
     * @param startLine  the start line index
     * @param startCol   the index of the first character included in the span
     * @param endLine    the end line index
     * @param endCol     the index of the character after the last character included in the span
     * @param annotation the annotation
     */
    public LineOrientedFileSpan {

        if (startLine() < 0) {
            throw new IllegalArgumentException("Start line may not be negative.");
        }
        if (endLine() < startLine()) {
            throw new IllegalArgumentException("End line may not precede start line.");
        }
        if (startCol() < 0) {
            throw new IllegalArgumentException("Start character position may not be negative.");
        }
        if (endLine() == startLine()) {
            if (endCol() < startCol()) {
                throw new IllegalArgumentException("End character may not precede start character.");
            }
        } else {
            if (endCol() < 0) {
                throw new IllegalArgumentException("End character position may not be negative.");
            }
            if (endCol() == 0) {
                throw new IllegalArgumentException("Multi-line span cannot end at start of a line.");
            }
        }
    }

    /**
     * Tests whether the span contains a particular line index and column index.
     *
     * @param lineIndex the line index
     * @param colIndex  the column index within the line
     * @return {@code ESpanIntersection.EXCLUDED} if the column index is not included in or adjacent to the span;
     *         {@code ESpanIntersection.INTERIOR} if the column index is in the interior of the span;
     *         {@code ESpanIntersection.START} if the column index matches the start of the span;
     *         {@code ESpanIntersection.END} if the column index matches the end of the span;
     *         {@code ESpanIntersection.START_AND_END} if the column index matches both the start and end of the span
     *         (meaning the span has zero length and simply represents a position in the file)
     */
    public ESpanIntersection containsLineChar(final int lineIndex, final int colIndex) {

        ESpanIntersection result = ESpanIntersection.EXCLUDED;

        if (this.startLine == this.endLine) {
            // This is a single-line span
            if (this.startCol == this.endCol) {
                // This is a zero-length span
                if (this.startCol == colIndex) {
                    result = ESpanIntersection.START_AND_END;
                }
            } else {
                // This is a nonzero-length span
                if (colIndex == this.startCol) {
                    result = ESpanIntersection.START;
                } else if (colIndex > this.startCol) {
                    if (colIndex == this.endCol) {
                        result = ESpanIntersection.END;
                    } else if (colIndex < this.endCol) {
                        result = ESpanIntersection.INTERIOR;
                    }
                }
            }
        } else {
            // The span includes characters from multiple lines
            if (lineIndex > this.startLine) {
                if (lineIndex < this.endLine) {
                    // Multi-line span and specified line is in its interior
                    result = ESpanIntersection.INTERIOR;
                } else if (lineIndex == this.endLine) {
                    // Char index lies in the last line of this span
                    if (colIndex == this.endCol) {
                        result = ESpanIntersection.END;
                    } else if (colIndex < this.endCol) {
                        result = ESpanIntersection.INTERIOR;
                    }
                }
            } else if (lineIndex == this.startLine) {
                // Char index lies in the first line of this span
                if (colIndex == this.startCol) {
                    result = ESpanIntersection.START;
                } else if (colIndex > this.startCol) {
                    result = ESpanIntersection.INTERIOR;
                }
            }
        }

        return result;
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return Integer.rotateLeft(this.startLine, 24) + Integer.rotateLeft(this.startCol, 16)
               + Integer.rotateLeft(this.endLine, 8) + this.endCol + annotation().hashCode();

    }

    /**
     * Tests whether {@code o} is "equal to" this object.  To be equal, the start and end line and column indexes must
     * match and the annotations must either both be null or be equal to each other.
     *
     * @param o the reference object with which to compare.
     * @return true of the objects are equal; false if not
     */
    @Override
    public boolean equals(final Object o) {

        boolean equal;

        if (o == this) {
            equal = true;
        } else if (o instanceof final LineOrientedFileSpan span) {
            equal = span.startLine() == startLine()
                    && span.endLine() == endLine()
                    && span.startCol() == startCol()
                    && span.endCol() == endCol()
                    && Objects.equals(span.annotation(), annotation());
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Generates a string representation of the span, in the form "1.2:3.4". meaning line 1, column 2 through line 3
     * column 4.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        return SimpleBuilder.concat(LEFT, startLine(), CoreConstants.DOT, startCol(), CoreConstants.COLON, endLine(),
                CoreConstants.DOT, endCol(), RIGHT);
    }
}


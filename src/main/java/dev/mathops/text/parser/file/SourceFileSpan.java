package dev.mathops.text.parser.file;

/**
 * A span of character data from a source file with annotations.  Used to associate warning/error or structure
 * information with character file content.  Such annotations can then drive visual representations such as syntax
 * highlighting or marking of errors or warnings in the source file.
 *
 * @param startLine  The index of the line on which the span begins.
 * @param startChar  The index of the character at which the span starts. This position should not fall at the end of a
 *                   line. It must be true that {@code startChar >= 0} and {@code startChar < len(startLine)}.
 * @param endLine    The index of the line on which the span begins.  It must be true that
 *                   {@code endLine >= startLine}.
 * @param endChar    The index of the character before which the span ends.  This position should not lie at the start
 *                   of a line * unless this object represents a zero-length span at the start of a line.  It must be
 *                   true that * {@code endChar <= len(endLine)} and if {@code startLine = endLine}, then
 *                   {@code endChar >= startChar}.
 * @param annotation The annotation to attach to the file span (null values allowed of this object is only being * used
 *                   to track s span of content).
 */
public record SourceFileSpan(int startLine, int startChar, int endLine, int endChar, FileSpanAnnotation annotation) {

    /**
     * Constructs a new {@code AnnotatedFileSpan}.
     *
     * @param startLine  the start line index
     * @param startChar  the index of the first character included in the span
     * @param endLine    the end line index
     * @param endChar    the index of the character after the last character included in the span
     * @param annotation the annotation
     */
    public SourceFileSpan {

        if (startLine() < 0) {
            throw new IllegalArgumentException("Start line may not be negative.");
        }
        if (endLine() < startLine()) {
            throw new IllegalArgumentException("End line may not precede start line.");
        }
        if (startChar() < 0) {
            throw new IllegalArgumentException("Start character position may not be negative.");
        }
        if (endLine() == startLine()) {
            if (endChar() < startChar()) {
                throw new IllegalArgumentException("End character may not precede start character.");
            }
        } else {
            if (endChar() < 0) {
                throw new IllegalArgumentException("End character position may not be negative.");
            }
            if (endChar() == 0) {
                throw new IllegalArgumentException("Multi-line span cannot end at start of a line.");
            }
        }
    }
}


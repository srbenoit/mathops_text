package dev.mathops.text.parser.file;

/**
 * A range of character positions in a source file, specified by start, end, and current positions, where each is
 * defined by a line index and column index.
 */
public final class LineOrientedFileRange {

    /** The source file. */
    public final LineOrientedFile source;

    /** The start line of the range. */
    public int startLine;

    /** The start column of the range. */
    public int startColumn;

    /** The current line of the range. */
    public int currentLine;

    /** The current column of the range. */
    public int currentColumn;

    /** The end line of the range. */
    public int endLine;

    /** The end column of the range. */
    public int endColumn;

    /**
     * Constructs a new {@code LineOrientedFileRange}.
     *
     * @param theSource the source file
     */
    public LineOrientedFileRange(final LineOrientedFile theSource) {

        this.source = theSource;
    }

    /**
     * Increments the current position, wrapping to new lines as needed.  The current position can only lie at the end
     * of the last line - otherwise, it will automatically wrap to the start of the first subsequent line that has at
     * least one character.
     */
    public void increment() {

        // Current position can be at the end of line only on the last line - otherwise, we jump to the 0
        // position on the next line (and continue forward if that line is empty)

        final int lineLen = this.source.getLineLength(this.currentLine);
        if (this.currentLine == this.endLine) {
            if (this.currentColumn < lineLen) {
                ++this.currentColumn;
            }
        } else if (this.currentColumn < lineLen) {
            ++this.currentColumn;
        } else {
            this.currentColumn = 0;
            ++this.currentLine;

            int newLineLen = this.source.getLineLength(this.currentLine);
            while (newLineLen == 0 && this.currentLine < this.endLine) {
                ++this.currentLine;
                newLineLen = this.source.getLineLength(this.currentLine);
            }
        }
    }

    /**
     * Tests whether the current position is before the end of the range.
     *
     * @return true if the current position lies before the end
     */
    public boolean isBeforeEnd() {

        return this.currentLine < this.endLine || this.currentColumn < this.endColumn;
    }

    /**
     * Tests whether the current position is at the end of the range.
     *
     * @return true if the current position equals the end
     */
    public boolean isAtEnd() {

        return this.currentLine == this.endLine && this.currentColumn == this.endColumn;
    }

    /**
     * Returns the character offset in the entire file of the current position.
     *
     * @return the current position character offset
     */
    public int currentOffset() {

        int offset = this.currentColumn;
        for (int i = 0; i < this.currentLine; ++i) {
            offset += this.source.getLineLength(i);
        }

        return offset;
    }

    /**
     * Returns the character offset in the entire file of the end position.
     *
     * @return the end position character offset
     */
    public int endOffset() {

        int offset = this.endColumn;
        for (int i = 0; i < this.endLine; ++i) {
            offset += this.source.getLineLength(i);
        }

        return offset;
    }
}
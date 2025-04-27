package dev.mathops.text.parser.file;

import dev.mathops.commons.file.FileLoader;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A container for the contents of a file, organized in a way that supports referencing characters by offset or by line
 * and column, and allows warning/error information to be attached to content.
 *
 * <p>
 * This class is designed as a representation of static content; editing of file content is not supported.  It does
 * provide for warning/error messages to be attached to spans of content in the file to allow the file contents to be
 * viewed with errors displayed.
 */
public final class SourceFile {

    /** The file lines. */
    private final List<String> lines;

    /** Spans identified in the file with optional annotations. */
    private final List<SourceFileSpan> spans;

    /**
     * Constructs a new {@code SourceFile} with data from a {@code File} that is loaded by a {@code ClassLoader}.  The
     * file's contents are loaded as character data and organized by line.
     *
     * @param clazz    a class that lies in the same package as the source file to load
     * @param filename the name of the source file to load
     * @param charset  the character set to use when interpreting file bytes as character data
     */
    public SourceFile(final Class<?> clazz, final String filename, final Charset charset) {

        this(FileLoader.loadFileAsLines(clazz, filename, charset, true));
    }

    /**
     * Constructs a new {@code SourceFile} with data from a provided {@code File}.  The file's contents are loaded as
     * character data and organized by line.
     *
     * @param file    the file from which to load data
     * @param charset the character set to use when interpreting file bytes as character data
     */
    public SourceFile(final File file, final Charset charset) {

        this(FileLoader.loadFileAsLines(file, charset, true));
    }

    /**
     * Constructs a new {@code SourceFile} directly from an array of {@code String} objects that represent the lines of
     * the source file.
     *
     * @param theLines the array of line strings (if null or empty, the resulting object is empty)
     */
    public SourceFile(final String... theLines) {

        if (theLines == null) {
            this.lines = new ArrayList<>(0);
        } else {
            this.lines = Arrays.asList(theLines);
        }

        this.spans = new ArrayList<>(10);
    }

    /**
     * Gets the number of lines in the source file.
     *
     * @return the number of lines
     */
    public int getNumLines() {

        return this.lines.size();
    }

    /**
     * Gets a single line of text.
     *
     * @param index the line index
     * @return the line of text
     */
    public String getLine(final int index) {

        return this.lines.get(index);
    }

    /**
     * Gets the maximum number of columns of any line in the source file.
     *
     * @return the maximum number of columns
     */
    public int getMaxColumns() {

        int max = 0;

        for (final String line : this.lines) {
            max = Math.max(max, line.length());
        }

        return max;
    }

    /**
     * Adds an identified span to a file.
     *
     * @param theSpan the span to add
     */
    public void addSpan(final SourceFileSpan theSpan) {

        this.spans.add(theSpan);
    }

    /**
     * Adds an identified span to a file.
     *
     * @param startLine      the start line index
     * @param startChar      the index of the first character included in the span
     * @param endLine        the end line index
     * @param endChar        the index of the character after the last character included in the span
     * @param annotationType the type of the annotation
     * @param description    a text description that should be displayed as detail information about the annotation;
     *                       null if none
     * @param spanStyle      the style to use when representing the annotation visually
     */
    public void addSpan(final int startLine, final int startChar, final int endLine, final int endChar,
                        final EAnnotationType annotationType, final String description,
                        final AnnotationStyle spanStyle) {

        final FileSpanAnnotation annotation = new FileSpanAnnotation(annotationType, description, spanStyle);
        final SourceFileSpan span = new SourceFileSpan(startLine, startChar, endLine, endChar, annotation);
        addSpan(span);
    }

    /**
     * Adds all spans that include any characters on a given line to a target list.
     *
     * @param lineIndex the index of the line
     * @param target    a list to which to add all matching spans (spans are added to this list in the same order they
     *                  were added to the source file)
     */
    public void getSpansIntersectingLine(final int lineIndex, final List<? super SourceFileSpan> target) {

        for (final SourceFileSpan span : this.spans) {
            if (span.startLine() <= lineIndex && span.endLine() >= lineIndex) {
                target.add(span);
            }
        }
    }
}

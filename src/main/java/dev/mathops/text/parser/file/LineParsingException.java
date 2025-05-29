package dev.mathops.text.parser.file;

import dev.mathops.commons.CoreConstants;

import java.io.Serial;

/**
 * An exception while parsing a line-oriented text format.
 */
public final class LineParsingException extends Exception {

    /** Version number for serialization. */
    @Serial
    private static final long serialVersionUID = 3864137494501028762L;

    /**
     * Constructs a new {@code LineParsingException} with the specified detail message.
     *
     * @param span    the span of XML that caused the exception
     * @param message the detail message
     */
    public LineParsingException(final LineOrientedFileSpan span, final String message) {

        super(message + CoreConstants.SPC + span.toString());
    }

    /**
     * Constructs a new {@code ParsingException} with the specified detail message and cause.
     *
     * @param span    the span of XML that caused the exception
     * @param message the detail message
     * @param cause   the cause
     */
    public LineParsingException(final LineOrientedFileSpan span, final String message, final Throwable cause) {

        super(message + CoreConstants.SPC + span.toString(), cause);
    }

    /**
     * Constructs a new {@code ParsingException} with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
     * {@code cause} ).
     *
     * @param cause the cause
     */
    public LineParsingException(final Throwable cause) {

        super(cause);
    }
}

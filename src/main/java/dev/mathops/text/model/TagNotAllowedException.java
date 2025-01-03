package dev.mathops.text.model;

import java.io.Serial;

/**
 * An exception that indicate an XML tag is not allowed as a child of a given parent node.
 */
public class TagNotAllowedException extends Exception {

    /** Version for serialization. */
    @Serial
    private static final long serialVersionUID = 6023580756992283485L;

    /**
     * Constructs a new {@code TagNotAllowedException}.
     */
    public TagNotAllowedException() {

        super();
    }

    /**
     * Constructs a new {@code TagNotAllowedException} with a specified message.
     *
     * @param message the message
     */
    public TagNotAllowedException(final String message) {

        super(message);
    }

    /**
     * Constructs a new {@code TagNotAllowedException} with a specified cause.
     *
     * @param cause the cause
     */
    public TagNotAllowedException(final Throwable cause) {

        super(cause);
    }

    /**
     * Constructs a new {@code TagNotAllowedException} with specified message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public TagNotAllowedException(final String message, final Throwable cause) {

        super(message, cause);
    }
}

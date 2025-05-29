package dev.mathops.text.parser.file;

import dev.mathops.commons.log.Log;

/**
 * The base class for annotations that can be attached to a span of content in a file.
 *
 * @param annotationType the type of the annotation
 * @param description    a text description that should be displayed as detail information about the annotation; null if
 *                       none
 * @param spanStyle      the style to use when representing the annotation visually; null if no visual representation
 */
public record FileSpanAnnotation(EAnnotationType annotationType, String description, AnnotationStyle spanStyle) {

    /**
     * Constructs a new {@code FileSpanAnnotation}.
     *
     * @param annotationType the type of the annotation
     * @param description    a text description that should be displayed as detail information about the annotation;
     *                       null if none
     * @param spanStyle      the style to use when representing the annotation visually; null if no visual
     *                       representation
     */
    public FileSpanAnnotation {

        if (annotationType == null) {
            throw new IllegalArgumentException("Annotation type may not be null");
        }
    }

    /**
     * Generates a hash code for the object.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return this.annotationType.hashCode();

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
        } else if (o instanceof final FileSpanAnnotation annot) {
            equal = annot.annotationType() == annotationType();
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

        return annotationType().toString();
    }
}

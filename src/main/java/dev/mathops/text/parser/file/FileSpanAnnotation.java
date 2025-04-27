package dev.mathops.text.parser.file;

/**
 * The base class for annotations that can be attached to a span of content in a file.
 *
 * @param annotationType the type of the annotation
 * @param description    a text description that should be displayed as detail information about the annotation; null if
 *                       none
 * @param spanStyle      the style to use when representing the annotation visually
 */
public record FileSpanAnnotation(EAnnotationType annotationType, String description, AnnotationStyle spanStyle) {
}

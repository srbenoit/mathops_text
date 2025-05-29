/**
 * Classes that support parsing of line-oriented text files.
 *
 * <dl>
 * <dt>{@code SourceFile}</dt>
 * <dd>Character file content, organized into lines, with a list of attached {@code SourceFileSpan}.</dd>
 * <dt>{@code SourceFileSpan}</dt>
 * <dd>A span of character content (defined by start and end line and character positions within those lines) with an
 *     optional attached annotation.</dd>
 * <dt>{@code FileSpanAnnotation}</dt>
 * <dd>An annotation attached to a span of character content with an annotation type, description, and visual
 *     style.</dd>
 * <dt>{@code EAnnotationType}</dt>
 * <dd>A class of annotations to allow, for example, things like Errors to be distinguished from syntax highlighting or
 *     misspellings.</dd>
 * <dt>{@code AnnotationStyle}</dt>
 * <dd>A visual style to be applied to a span, including an {@code EDecorationStyle} and font/color information.</dd>
 * </dl>
 */
package dev.mathops.text.parser.file;

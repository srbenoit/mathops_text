package dev.mathops.text.parser.file;

import java.awt.Color;

/**
 * A style that can visually represent an annotation attached to a span of character content.
 *
 * <p>
 * Spans of content can overlap and in that situation, annotation styles can 'stack'.  When multiples styles are applied
 * to character content, the behavior is as follows (where styles have a well-defined order of application):
 * <ul>
 *     <li>Resultant font color will be an average (in RGB space) of all applied colors.</li>
 *     <li>Resultant font posture will be italic if any annotation indicates italics.</li>
 *     <li>Resultant font weight will be the maximum of all applied font weights.</li>
 *     <li>All decorations are applied in application order, and if there are multiple decorations with the same
 *     style, the color used will eb the average (in RGB space) of all applied colors.</li>
 * </ul>
 *
 * @param fontColor       the AWT color for the font; null if no change to default font color
 * @param isItalic        true if the style indicates italic font
 * @param fontWeight      a font weight, from 100 (thin) to 900 (thick), where 400 represents a typical "normal" font
 *                        weight, and 700 represents a typical "bold" weight
 * @param decorationStyle the decoration style (null if no decoration)
 * @param decorationColor the AWT color to use when drawing the decoration (ignored if {@code decorationStyle} is null,
 *                        required if {@code decorationStyle} is non-null)
 */
public record AnnotationStyle(Color fontColor, boolean isItalic, int fontWeight, EDecorationStyle decorationStyle,
                              Color decorationColor) {

    /** A font weight threshold at which level (or above) the font is considered "BOLD" */
    public static final int BOLD_THRESHOLD = 550;
}

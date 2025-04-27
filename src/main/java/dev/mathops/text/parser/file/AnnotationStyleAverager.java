package dev.mathops.text.parser.file;

import dev.mathops.commons.ui.ColorAverager;

import java.awt.Color;

/**
 * An averager to accept several annotation styles that may apply to a character in a line of content.  The averager can
 * then generate a "resultant" style to be used to draw the styled character.
 */
public class AnnotationStyleAverager {

    /** The maximum font weight seen. */
    private int maxFontWeight;

    /** True if ony style indicated italic font. */
    private boolean isItalic;

    /** An averager to determine font color. */
    private final ColorAverager fontColorAverager;

    /** An averager to determine underline color. */
    private final ColorAverager underlineColor;

    /** An averager to determine double underline color. */
    private final ColorAverager doubleUnderlineColor;

    /** An averager to determine dotted underline color. */
    private final ColorAverager dottedUnderlineColor;

    /** An averager to determine wavy underline color. */
    private final ColorAverager wavyUnderlineColor;

    /** An averager to determine strikethrough color. */
    private final ColorAverager strikethroughColor;

    /** An averager to determine double strikethrough color. */
    private final ColorAverager doubleStrikethroughColor;

    /** An averager to determine dotted strikethrough color. */
    private final ColorAverager dottedStrikethroughColor;

    /** An averager to determine wavy strikethrough color. */
    private final ColorAverager wavyStrikethroughColor;

    /** An averager to determine highlight color. */
    private final ColorAverager highlightColor;

    /** An averager to determine drop shadow color. */
    private final ColorAverager dropShadowColor;

    /** An averager to determine caret color. */
    private final ColorAverager caretColorAverager;

    /**
     * Constructs a new {@code AnnotationStyleAverager}.
     */
    public AnnotationStyleAverager() {

        this.fontColorAverager = new ColorAverager();
        this.underlineColor = new ColorAverager();
        this.doubleUnderlineColor = new ColorAverager();
        this.dottedUnderlineColor = new ColorAverager();
        this.wavyUnderlineColor = new ColorAverager();
        this.strikethroughColor = new ColorAverager();
        this.doubleStrikethroughColor = new ColorAverager();
        this.dottedStrikethroughColor = new ColorAverager();
        this.wavyStrikethroughColor = new ColorAverager();
        this.highlightColor = new ColorAverager();
        this.dropShadowColor = new ColorAverager();
        this.caretColorAverager = new ColorAverager();
    }

    /**
     * Resets the style averager.
     */
    public void reset() {

        this.maxFontWeight = 0;
        this.isItalic = false;
        this.fontColorAverager.reset();
        this.underlineColor.reset();
        this.doubleUnderlineColor.reset();
        this.dottedUnderlineColor.reset();
        this.wavyUnderlineColor.reset();
        this.strikethroughColor.reset();
        this.doubleStrikethroughColor.reset();
        this.dottedStrikethroughColor.reset();
        this.wavyStrikethroughColor.reset();
        this.highlightColor.reset();
        this.dropShadowColor.reset();
        this.caretColorAverager.reset();
    }

    /**
     * Adds a style.
     *
     * @param theStyle     the style to add
     * @param intersection how the span intersects the character
     */
    public void addStyle(final AnnotationStyle theStyle, final ESpanIntersection intersection) {

        if (intersection == ESpanIntersection.START_AND_END || intersection == ESpanIntersection.END) {
            if (theStyle.decorationStyle() == EDecorationStyle.CARET) {
                this.caretColorAverager.addColor(theStyle.decorationColor());
            }
        } else if (intersection == ESpanIntersection.START || intersection == ESpanIntersection.INTERIOR) {
            if (theStyle.fontColor() != null) {
                this.fontColorAverager.addColor(theStyle.fontColor());
            }
            this.isItalic |= theStyle.isItalic();
            this.maxFontWeight = Math.max(this.maxFontWeight, theStyle.fontWeight());

            switch (theStyle.decorationStyle()) {
                case UNDERLINE -> this.underlineColor.addColor(theStyle.decorationColor());
                case DOUBLE_UNDERLINE -> this.doubleUnderlineColor.addColor(theStyle.decorationColor());
                case DOTTED_UNDERLINE -> this.dottedUnderlineColor.addColor(theStyle.decorationColor());
                case WAVY_UNDERLINE -> this.wavyUnderlineColor.addColor(theStyle.decorationColor());
                case STRIKETHROUGH -> this.strikethroughColor.addColor(theStyle.decorationColor());
                case DOUBLE_STRIKETHROUGH -> this.doubleStrikethroughColor.addColor(theStyle.decorationColor());
                case DOTTED_STRIKETHROUGH -> this.dottedStrikethroughColor.addColor(theStyle.decorationColor());
                case WAVY_STRIKETHROUGH -> this.wavyStrikethroughColor.addColor(theStyle.decorationColor());
                case HIGHLIGHT -> this.highlightColor.addColor(theStyle.decorationColor());
                case DROP_SHADOW -> this.dropShadowColor.addColor(theStyle.decorationColor());
                case CARET -> this.caretColorAverager.addColor(theStyle.decorationColor());
            }
        }
    }

    /**
     * Tests whether the font should be bold.
     *
     * @return true to use a bold font; false to use a normal font
     */
    public boolean isBold() {

        return this.maxFontWeight >= AnnotationStyle.BOLD_THRESHOLD;
    }

    /**
     * Tests whether the font should be italic.
     *
     * @return true to use an italic font; false to use a plain font
     */
    public boolean isItalic() {

        return this.isItalic;
    }

    /**
     * Gets the color to use to draw characters.
     *
     * @param defaultColor the default color if no style specified a font color
     * @return the font color
     */
    public Color getFontColor(final Color defaultColor) {

        return this.fontColorAverager.getN() == 0 ? defaultColor : this.fontColorAverager.getAverageColor();
    }

    /**
     * Gets the color to use for a single underline.
     *
     * @param defaultColor the default color (could be null) if no style specified an underline
     * @return the underline color
     */
    public Color getUnderlineColor(final Color defaultColor) {

        return this.underlineColor.getN() == 0 ? defaultColor : this.underlineColor.getAverageColor();
    }

    /**
     * Gets the color to use for a double underline.
     *
     * @param defaultColor the default color (could be null) if no style specified a double underline
     * @return the double underline color
     */
    public Color getDoubleUnderlineColor(final Color defaultColor) {

        return this.doubleUnderlineColor.getN() == 0 ? defaultColor : this.doubleUnderlineColor.getAverageColor();
    }

    /**
     * Gets the color to use for a dotted  underline.
     *
     * @param defaultColor the default color (could be null) if no style specified a dotted underline
     * @return the dotted underline color
     */
    public Color getDottedUnderlineColor(final Color defaultColor) {

        return this.dottedUnderlineColor.getN() == 0 ? defaultColor : this.dottedUnderlineColor.getAverageColor();
    }

    /**
     * Gets the color to use for a wavy underline.
     *
     * @param defaultColor the default color (could be null) if no style specified a wavy underline
     * @return the wavy underline color
     */
    public Color getWavyUnderlineColor(final Color defaultColor) {

        return this.wavyUnderlineColor.getN() == 0 ? defaultColor : this.wavyUnderlineColor.getAverageColor();
    }

    /**
     * Gets the color to use for a single strikethrough.
     *
     * @param defaultColor the default color (could be null) if no style specified a strikethrough color
     * @return the strikethrough color
     */
    public Color getStrikethroughColor(final Color defaultColor) {

        return this.strikethroughColor.getN() == 0 ? defaultColor : this.strikethroughColor.getAverageColor();
    }

    /**
     * Gets the color to use for a double strikethrough.
     *
     * @param defaultColor the default color (could be null) if no style specified a double strikethrough
     * @return the double strikethrough color
     */
    public Color getDoubleStrikethroughColor(final Color defaultColor) {

        return this.doubleStrikethroughColor.getN() == 0 ? defaultColor :
                this.doubleStrikethroughColor.getAverageColor();
    }

    /**
     * Gets the color to use for a dotted strikethrough.
     *
     * @param defaultColor the default color (could be null) if no style specified a dotted strikethrough
     * @return the dotted strikethrough color
     */
    public Color getDottedStrikethroughColor(final Color defaultColor) {

        return this.dottedStrikethroughColor.getN() == 0 ? defaultColor :
                this.dottedStrikethroughColor.getAverageColor();
    }

    /**
     * Gets the color to use for a wavy strikethrough.
     *
     * @param defaultColor the default color (could be null) if no style specified a wavy strikethrough
     * @return the wavy strikethrough color
     */
    public Color getWavyStrikethroughColor(final Color defaultColor) {

        return this.wavyStrikethroughColor.getN() == 0 ? defaultColor : this.wavyStrikethroughColor.getAverageColor();
    }

    /**
     * Gets the color to use for a highlight.
     *
     * @param defaultColor the default color (could be null) if no style specified a highlight
     * @return the highlight color
     */
    public Color getHighlightColor(final Color defaultColor) {

        return this.highlightColor.getN() == 0 ? defaultColor : this.highlightColor.getAverageColor();
    }

    /**
     * Gets the color to use for a drop shadow.
     *
     * @param defaultColor the default color (could be null) if no style specified a drop shadow
     * @return the drop shadow color
     */
    public Color getDropShadowColor(final Color defaultColor) {

        return this.dropShadowColor.getN() == 0 ? defaultColor : this.dropShadowColor.getAverageColor();
    }

    /**
     * Gets the color to use for a caret.
     *
     * @param defaultColor the default color (could be null) if no style specified a caret
     * @return the caret color
     */
    public Color getCaretColor(final Color defaultColor) {

        return this.caretColorAverager.getN() == 0 ? defaultColor : this.caretColorAverager.getAverageColor();
    }

}

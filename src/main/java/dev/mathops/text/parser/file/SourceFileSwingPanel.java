package dev.mathops.text.parser.file;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel that can present a view of a {@code SourceFile} with all annotations represented.
 */
public final class SourceFileSwingPanel extends JPanel implements Scrollable {

    /** The source file to present. */
    private final SourceFile sourceFile;

    /** An averager to compute a resultant style when several styles may apply. */
    private final AnnotationStyleAverager averager;

    /** The base font (should be a monospace font). */
    private final Font plainFont;

    /** The bold font (derived from the base font). */
    private final Font boldFont;

    /** The italic font (derived from the base font). */
    private final Font italicFont;

    /** The bold italic font (derived from the base font). */
    private final Font boldItalicFont;

    /** Font metrics associated with the base font. */
    private final FontMetrics fontMetrics;

    /** The size of a character box. */
    private final Dimension charBoxSize;

    /**
     * Constructs a new {@code SourceFileSwingPanel}
     *
     * @param theSourceFile the source file to present
     */
    public SourceFileSwingPanel(final SourceFile theSourceFile) {

        super();

        if (theSourceFile == null) {
            throw new IllegalArgumentException("Source file may not be null");
        }

        setBackground(Color.WHITE);

        this.sourceFile = theSourceFile;
        this.averager = new AnnotationStyleAverager();

        this.plainFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        this.boldFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
        this.italicFont = new Font(Font.MONOSPACED, Font.ITALIC, 12);
        this.boldItalicFont = new Font(Font.MONOSPACED, Font.BOLD | Font.ITALIC, 12);

        final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        g2d.setFont(this.plainFont);
        this.fontMetrics = g2d.getFontMetrics();
        g2d.dispose();

        final int leading = this.fontMetrics.getLeading();

        final Border padding = BorderFactory.createEmptyBorder(leading, leading, leading, leading);
        setBorder(padding);
        final Insets insets = getInsets();

        this.charBoxSize = new Dimension(this.fontMetrics.stringWidth("0"), this.fontMetrics.getHeight());

        // Determine preferred size and layout
        final int numLines = theSourceFile.getNumLines();
        final int numColumns = theSourceFile.getMaxColumns();

        final int prefWidth = insets.left + insets.right + numColumns * this.charBoxSize.width;
        final int prefHeight = insets.top + insets.bottom + numLines * this.charBoxSize.height;
        setPreferredSize(new Dimension(prefWidth, prefHeight));
    }

    /**
     * Paints the panel.
     *
     * @param g the {@code Graphics} object to which to draw
     */
    public void paintComponent(final Graphics g) {

        super.paintComponent(g);

        final Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top + this.fontMetrics.getAscent();

        final Rectangle clip = g.getClipBounds();

        final List<SourceFileSpan> spans = new ArrayList<>(10);

        final int numLines = this.sourceFile.getNumLines();
        for (int lineIndex = 0; lineIndex < numLines; ++lineIndex) {
            final int top = y - this.fontMetrics.getAscent();
            final int bottom = top + this.fontMetrics.getHeight();

            if (top < (clip.y + clip.height) && bottom > clip.y) {
                final String line = this.sourceFile.getLine(lineIndex);
                this.sourceFile.getSpansIntersectingLine(lineIndex, spans);

                drawLine(g, line, x, y, lineIndex, spans);
                spans.clear();
            }

            y += this.fontMetrics.getHeight();
        }
    }

    /**
     * Draws a single line of text.
     *
     * @param g         the {@code Graphics} to which to draw
     * @param line      the line text
     * @param x         the x position at which to draw the line
     * @param y         the y position at which to draw the baseline of the line
     * @param lineIndex th eline index
     * @param spans     a list containing all spans that contain any characters in the list
     */
    private void drawLine(final Graphics g, final String line, final int x, final int y,
                          final int lineIndex, final List<SourceFileSpan> spans) {

        if (g instanceof final Graphics2D g2d) {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        if (spans.isEmpty()) {
            g.setFont(this.plainFont);
            g.setColor(Color.BLACK);
            g.drawString(line, x, y);
        } else {
            final char[] lineChars = line.toCharArray();
            final int numChars = lineChars.length;
            final int topY = y - this.fontMetrics.getAscent();
            final int botY = y + this.fontMetrics.getDescent();
            final int midY = y - this.fontMetrics.getAscent() / 3;

            int curX = x;
            for (int charIndex = 0; charIndex < numChars; ++charIndex) {
                // Accumulate style for this character...
                this.averager.reset();

                for (final SourceFileSpan span : spans) {
                    final FileSpanAnnotation annotation = span.annotation();
                    if (annotation == null) {
                        continue;
                    }
                    final AnnotationStyle style = annotation.spanStyle();
                    if (style == null) {
                        continue;
                    }

                    final ESpanIntersection intersection = span.containsLineChar(lineIndex, charIndex);
                    this.averager.addStyle(style, intersection);
                }

                if (this.averager.isBold()) {
                    g.setFont(this.averager.isItalic() ? this.boldItalicFont : this.boldFont);
                } else {
                    g.setFont(this.averager.isItalic() ? this.italicFont : this.plainFont);
                }

                final Color highlightColor = this.averager.getHighlightColor(null);
                if (highlightColor != null) {
                    g.setColor(highlightColor);
                    g.fillRect(curX, topY, this.charBoxSize.width, botY - topY);
                }

                final Color doubleUnderlineColor = this.averager.getDoubleUnderlineColor(null);
                if (doubleUnderlineColor != null) {
                    g.setColor(doubleUnderlineColor);
                    g.drawLine(curX, y + 2, curX + this.charBoxSize.width, y + 2);
                    g.drawLine(curX, y + 4, curX + this.charBoxSize.width, y + 4);
                }

                final Color underlineColor = this.averager.getUnderlineColor(null);
                if (underlineColor != null) {
                    g.setColor(underlineColor);
                    g.drawLine(curX, y + 3, curX + this.charBoxSize.width, y + 3);
                }

                final Color wavyUnderlineColor = this.averager.getWavyUnderlineColor(null);
                if (wavyUnderlineColor != null) {
                    g.setColor(wavyUnderlineColor);
                    drawWavyLine(g, curX, curX + this.charBoxSize.width, y + 3);
                }

                final Color dottedUnderlineColor = this.averager.getDottedUnderlineColor(null);
                if (dottedUnderlineColor != null) {
                    g.setColor(dottedUnderlineColor);
                    drawDottedLine(g, curX, curX + this.charBoxSize.width, y + 3);
                }

                final Color shadowColor = this.averager.getDropShadowColor(null);
                if (shadowColor != null) {
                    g.setColor(shadowColor);
                    g.drawChars(lineChars, charIndex, 1, curX + 1, y + 1);
                }

                g.setColor(this.averager.getFontColor(Color.BLACK));
                g.drawChars(lineChars, charIndex, 1, curX, y);

                final Color doubleStrikethroughColor = this.averager.getDoubleStrikethroughColor(null);
                if (doubleStrikethroughColor != null) {
                    g.setColor(doubleStrikethroughColor);
                    g.drawLine(curX, midY - 1, curX + this.charBoxSize.width, midY - 1);
                    g.drawLine(curX, midY + 1, curX + this.charBoxSize.width, midY + 1);
                }

                final Color strikethroughColor = this.averager.getStrikethroughColor(null);
                if (strikethroughColor != null) {
                    g.setColor(strikethroughColor);
                    g.drawLine(curX, midY, curX + this.charBoxSize.width, midY);
                }

                final Color wavyStrikethroughColor = this.averager.getWavyStrikethroughColor(null);
                if (wavyStrikethroughColor != null) {
                    g.setColor(wavyStrikethroughColor);
                    drawWavyLine(g, curX, curX + this.charBoxSize.width, midY);
                }

                final Color dottedStrikethroughColor = this.averager.getDottedStrikethroughColor(null);
                if (dottedStrikethroughColor != null) {
                    g.setColor(dottedStrikethroughColor);
                    drawDottedLine(g, curX, curX + this.charBoxSize.width, midY);
                }

                final Color caretColor = this.averager.getCaretColor(null);
                if (caretColor != null) {
                    g.setColor(caretColor);
                    // TODO: Draw a caret
                }

                curX += this.charBoxSize.width;
            }
        }
    }

    /**
     * Draws a dotted line.
     *
     * @param g      the {@code Graphics} to which to draw (the line color will have already been set)
     * @param startX the start X coordinate
     * @param endX   the end X coordinate
     * @param y      the Y coordinate
     */
    private void drawDottedLine(final Graphics g, final int startX, final int endX, final int y) {

        // Dotted lines are drawn as segments of 2 pixels.
        for (int x = startX; x < endX; ++x) {
            final int phase = x & 0x03;
            if (phase < 2) {
                g.drawLine(x, y, x + 1, y);
            }
        }
    }

    /**
     * Draws a wavy line.
     *
     * @param g      the {@code Graphics} to which to draw (the line color will have already been set)
     * @param startX the start X coordinate
     * @param endX   the end X coordinate
     * @param y      the Y coordinate
     */
    private void drawWavyLine(final Graphics g, final int startX, final int endX, final int y) {

        // Wavy lines are drawn as 8-pixel "sine" cycles that start on each even multiple of 8.
        for (int x = startX; x < endX; ++x) {
            final int phase = x & 0x07;
            if (phase == 0 || phase == 4) {
                g.drawLine(x, y, x + 1, y);
            } else if (phase < 4) {
                g.drawLine(x, y - 1, x + 1, y - 1);
            } else {
                g.drawLine(x, y + 1, x + 1, y + 1);
            }
        }
    }

    /**
     * Returns the preferred size of the viewport for a view component.
     *
     * @return the preferredSize of a <code>JViewport</code> whose view is this <code>Scrollable</code>
     */
    public Dimension getPreferredScrollableViewportSize() {

        return getPreferredSize();
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one new row or column, depending on the value of orientation.  Ideally, components should handle a partially
     * exposed row or column by returning the distance required to completely expose the item.
     *
     * @param visibleRect the view area visible within the viewport
     * @param direction   less than zero to scroll up/left, greater than zero for down/right
     * @param numSteps    the desired number of steps to scroll
     * @return the "unit" increment for scrolling in the specified direction
     */
    private int getVerticalIncrement(final Rectangle visibleRect, final int direction, final int numSteps) {

        final int numRowsAboveTop = visibleRect.y / this.charBoxSize.height;

        int newNumRows;
        if (direction < 0) {
            // Scrolling up
            newNumRows = Math.max(0, numRowsAboveTop - numSteps);
        } else {
            // Scrolling down
            final int numLines = this.sourceFile.getNumLines();
            final int numRowsVisibleAtATime = visibleRect.height / this.charBoxSize.height;
            final int maxRowsAboveTop = numLines - numRowsVisibleAtATime;
            newNumRows = Math.min(maxRowsAboveTop, numRowsAboveTop + numSteps);
        }

        final int newVisibleRectY = newNumRows * this.charBoxSize.height;

        return Math.abs(newVisibleRectY - visibleRect.y);
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one new row or column, depending on the value of orientation.  Ideally, components should handle a partially
     * exposed row or column by returning the distance required to completely expose the item.
     *
     * @param visibleRect the view area visible within the viewport
     * @param direction   less than zero to scroll up/left, greater than zero for down/right
     * @param numSteps    the desired number of steps to scroll
     * @return the "unit" increment for scrolling in the specified direction
     */
    private int getHorizontalIncrement(final Rectangle visibleRect, final int direction, final int numSteps) {

        final int numColsToLeft = visibleRect.x / this.charBoxSize.width;

        int newNumCols;
        if (direction < 0) {
            // Scrolling left
            newNumCols = Math.max(0, numColsToLeft - numSteps);
        } else {
            // Scrolling right
            final int numCols = this.sourceFile.getMaxColumns();
            final int numColsVisibleAtATime = visibleRect.width / this.charBoxSize.width;
            final int maxColsToLeft = numCols - numColsVisibleAtATime;
            newNumCols = Math.min(maxColsToLeft, numColsToLeft + numSteps);
        }
        final int oneMoreCol = newNumCols * this.charBoxSize.width;

        return Math.abs(oneMoreCol - visibleRect.x);
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one new row or column, depending on the value of orientation.  Ideally, components should handle a partially
     * exposed row or column by returning the distance required to completely expose the item.
     *
     * @param visibleRect the view area visible within the viewport
     * @param orientation either {@code SwingConstants.VERTICAL} or {@code SwingConstants.HORIZONTAL}
     * @param direction   less than zero to scroll up/left, greater than zero for down/right
     * @return the "unit" increment for scrolling in the specified direction
     */
    public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {

        return orientation == SwingConstants.VERTICAL ? getVerticalIncrement(visibleRect, direction, 1)
                : getHorizontalIncrement(visibleRect, direction, 1);
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one block of rows or columns, depending on the value of orientation.
     *
     * @param visibleRect the view area visible within the viewport
     * @param orientation either {@code SwingConstants.VERTICAL} or {@code SwingConstants.HORIZONTAL}
     * @param direction   less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction. This value should always be positive.
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {

        return orientation == SwingConstants.VERTICAL ? getVerticalIncrement(visibleRect, direction, 10)
                : getHorizontalIncrement(visibleRect, direction, 10);
    }

    /**
     * Return true if a viewport should always force the width of this panel to match the width of the viewport.
     *
     * @return true if a viewport should force the panel width to match its own
     */
    public boolean getScrollableTracksViewportWidth() {

        return false;
    }

    /**
     * Return true if a viewport should always force the height of this panel to match the height of the viewport.
     * <p>
     * Scrolling containers, like JViewport, will use this method each time they are validated.
     *
     * @return true if a viewport should force the panel height to match its own
     */
    public boolean getScrollableTracksViewportHeight() {

        return false;
    }
}

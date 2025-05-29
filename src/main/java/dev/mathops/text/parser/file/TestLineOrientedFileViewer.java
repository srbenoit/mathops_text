package dev.mathops.text.parser.file;

import dev.mathops.commons.Seed;
import dev.mathops.commons.ui.UIUtilities;
import dev.mathops.commons.Rnd;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.nio.charset.Charset;

/**
 * A simple Swing application that opens a text file and displays the contents in a {@code SourceFileSwingPanel} within
 * a {@code JScrollPane}.
 */
public class TestLineOrientedFileViewer implements Runnable {

    /**
     * Constructs the UI in the AWT event dispatch thread.
     */
    public void run() {

        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Text File Viewer");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        final Color[] accentColors = new Color[]{
                new Color(158, 0, 0),
                new Color(158, 79, 0),
                new Color(158, 158, 0),
                new Color(79, 158, 0),
                new Color(0, 158, 158),
                new Color(0, 0, 158),
                new Color(79, 0, 158),
                new Color(158, 0, 105)};
        final EDecorationStyle[] decStyles = EDecorationStyle.values();

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            final LineOrientedFile source = new LineOrientedFile(file, Charset.defaultCharset());

            final Rnd rnd = new Rnd(new Seed(System.currentTimeMillis(), System.nanoTime()));

            final int numLines = source.getNumLines();
            for (int i = 0; i < numLines; ++i) {
                final String line = source.getLine(i);
                final int len = line.length();

                if (len > 0) {
                    final int spanLen = rnd.nextInt(len);
                    final int spanStart = rnd.nextInt(len - spanLen);

                    final int fontColorInt = rnd.nextInt(accentColors.length + 1);
                    final Color fontColor = fontColorInt < accentColors.length ? accentColors[fontColorInt] : null;

                    final boolean italic = rnd.nextInt(3) == 2;
                    final int fontWeight = (1 + rnd.nextInt(9)) * 100;
                    final int decStyleInt = i % decStyles.length;
                    final EDecorationStyle decStyle = decStyles[decStyleInt];
                    final int decColorInt = rnd.nextInt(accentColors.length);
                    final Color decColor = accentColors[decColorInt];

                    final AnnotationStyle style = new AnnotationStyle(fontColor, italic, fontWeight, decStyle,
                            decColor);
                    source.addSpan(i, spanStart, i, spanStart + spanLen, EAnnotationType.INFO, null, style);
                }
            }

            final LineOrientedFileSwingPanel panel = new LineOrientedFileSwingPanel(source);
            final JScrollPane scroll = new JScrollPane(panel);
            scroll.getViewport().setBackground(Color.WHITE);
            scroll.setWheelScrollingEnabled(true);

            final JFrame frame = new JFrame("Text File Viewer");
            frame.setContentPane(scroll);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            final Dimension doc = panel.getPreferredSize();

            final int width = Math.min(screen.width / 2,
                    doc.width) + scroll.getVerticalScrollBar().getPreferredSize().width + 10;
            final int height = Math.min(screen.height * 2 / 3,
                    doc.height) + scroll.getHorizontalScrollBar().getPreferredSize().height + 10;

            scroll.setPreferredSize(new Dimension(width, height));

            UIUtilities.packAndCenter(frame);
            frame.setVisible(true);

            panel.init();
        }
    }

    /**
     * Main method to launch the application.
     *
     * @param args command-line arguments
     */
    public static void main(final String... args) {

        SwingUtilities.invokeLater(new TestLineOrientedFileViewer());
    }
}

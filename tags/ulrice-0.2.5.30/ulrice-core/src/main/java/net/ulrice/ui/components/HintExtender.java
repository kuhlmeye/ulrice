package net.ulrice.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

public class HintExtender {

    public static <T extends JTextComponent> void installHint(final T component, final String hint) {
        installHintPainter(component, hint == null || hint.trim().equals("") ? null : hint);
    }

    private static <T extends JTextComponent> void installHintPainter(final T component, final String hint) {
        final Highlighter highlighter = component.getHighlighter();
        try {
            highlighter.addHighlight(0, 0, createHintPainter(component, hint));
        }
        catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static HighlightPainter createHintPainter(final JTextComponent component, final String hint) {
        return new Highlighter.HighlightPainter() {

            private final JLabel label = new JLabel(hint, SwingConstants.TRAILING);

            {
                label.setFont(component.getFont());
                label.setForeground(Color.GRAY);
                label.setOpaque(false);
            }

            @Override
            public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
                if (hint == null || c.getDocument().getLength() != 0 || c.hasFocus() || !c.isEnabled() || !c.isEditable()) {
                    return;
                }

                final Insets insets = c.getInsets();

                final Dimension preferredSize = label.getPreferredSize();
                final int w = Math.min(c.getWidth() - insets.left - insets.right, preferredSize.width);
                final int h = Math.min(c.getHeight() - insets.top - insets.bottom, preferredSize.height);
                final Point firstCharLocation = getLocationOfFirstChar(c);
                final int x = firstCharLocation.x;
                final int y = firstCharLocation.y;

                SwingUtilities.paintComponent(g, label, c, x, y, w, h);
            }

            private Point getLocationOfFirstChar(final JTextComponent c) {
                Point firstCharLocation = null;
                try {
                    final Rectangle modelToView = c.modelToView(0);
                    firstCharLocation = new Point(modelToView.x, modelToView.y);
                }
                catch (BadLocationException ex) {
                    ex.printStackTrace();
                    firstCharLocation = new Point(0, 0);
                }
                return firstCharLocation;
            }
        };
	}
}

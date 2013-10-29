package net.ulrice.databinding.viewadapter.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import net.ulrice.util.Colors;
import net.ulrice.util.Gradients;

public class UBorder implements Border {

    private static final int MAX_BORDER_RADIUS = 4;

    private static final Color FOCUS = new Color(0x73a4d1);
    private static final Color BORDER = new Color(0xbfc5ce);
    private static final Insets BASEINSETS = new Insets(1, 3, 1, 3);

    private final boolean borderVisible;
    private final int clipLeft;
    private final int clipRight;
    private final Insets insets;

    public UBorder(boolean borderVisible) {
        this(borderVisible, false, false);
    }

    public UBorder(boolean borderVisible, Insets baseInsets) {
        this(borderVisible, baseInsets, false, false);
    }

    public UBorder(boolean borderVisible, boolean clipLeft, boolean clipRight) {
        this(borderVisible, null, clipLeft, clipRight);
    }

    public UBorder(boolean borderVisible, Insets baseInsets, boolean clipLeft, boolean clipRight) {
        super();

        this.borderVisible = borderVisible;
        this.clipLeft = clipLeft ? 3 : 0;
        this.clipRight = clipRight ? 3 : 0;

        if (baseInsets == null) {
            baseInsets = BASEINSETS;
        }

        insets = new Insets(3 + baseInsets.top, (3 + baseInsets.left) - this.clipLeft, 3 + baseInsets.bottom, (3 + baseInsets.right) - this.clipRight);
    }

    public boolean isBorderVisible() {
        return borderVisible;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        paintBorder(c, g, null, x, y, width, height);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    protected void paintBorder(Component c, Graphics g, Color highlight, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.clipRect(x, y, width, height);

        x -= clipLeft;
        width += clipLeft + clipRight;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawInlay((Graphics2D) g2d.create(), c, highlight, x, y, width, height);
        drawOverlay((Graphics2D) g2d.create(), c, highlight, x, y, width, height);
        drawBorder((Graphics2D) g2d.create(), c, highlight, x, y, width, height);
    }

    private void drawInlay(Graphics2D g, Component c, Color highlight, int x, int y, int width, int height) {
        g.setColor(c.getBackground());

        if (insets.top > 3) {
            Graphics inlayG = g.create();

            inlayG.clipRect(x + 3, y + 3, width - 6, insets.top - 3);
            inlayG.fillRoundRect(x + 2, y + 2, (width - 4) + (clipRight > 0 ? MAX_BORDER_RADIUS : 0), height - 4, MAX_BORDER_RADIUS, MAX_BORDER_RADIUS);
            // g.drawRect(x + 3, y + 3, width - 7, insets.top - 4);
        }

        if (insets.bottom > 3) {
            Graphics inlayG = g.create();

            inlayG.clipRect(x + 3, (y + height) - insets.bottom, width - 6, insets.bottom - 3);
            inlayG.fillRoundRect(x + 2, y + 2, (width - 4) + (clipRight > 0 ? MAX_BORDER_RADIUS : 0), height - 4, MAX_BORDER_RADIUS, MAX_BORDER_RADIUS);
            // g.drawRect(x + 3, (y + height) - insets.bottom, width - 7, insets.bottom - 4);
        }

        if ((insets.left + clipLeft) > 3) {
            Graphics inlayG = g.create();

            inlayG.clipRect((x + 3) - clipLeft, y + insets.top, (insets.left + clipLeft) - 3, height - insets.top - insets.bottom);
            inlayG.fillRoundRect(x + 2, y + 2, (width - 4) + (clipRight > 0 ? MAX_BORDER_RADIUS : 0), height - 4, MAX_BORDER_RADIUS, MAX_BORDER_RADIUS);
            // g.fillRect((x + 3) - clipLeft, y + insets.top, (insets.left + clipLeft) - 3, height - insets.top -
            // insets.bottom);
        }

        if ((insets.right + clipRight) > 3) {
            Graphics inlayG = g.create();

            inlayG.clipRect((x + width) - insets.right - clipRight, y + insets.top, (insets.right + clipRight) - 3, height - insets.top - insets.bottom);
            inlayG.fillRoundRect(x + 2, y + 2, (width - 4) + (clipRight > 0 ? MAX_BORDER_RADIUS : 0), height - 4, MAX_BORDER_RADIUS, MAX_BORDER_RADIUS);
            // g.fillRect((x + width) - insets.right - clipRight, y + insets.top, (insets.right + clipRight) - 3,
            // height - insets.top - insets.bottom);
        }
    }

    private void drawBorder(Graphics2D g, Component c, Color highlight, int x, int y, int width, int height) {
        boolean enabled = c.isEnabled();
        boolean focus = isFocusOwner(c);

        if ((c instanceof JTextComponent) && (!((JTextComponent) c).isEditable())) {
            enabled = false;
        }

        Color borderColor = BORDER;

        if (highlight != null) {
            borderColor = Colors.blend(borderColor, highlight, 0.5);
        }
        else if (focus) {
            borderColor = Colors.blend(borderColor, FOCUS, 0.5);
        }

        g.setPaint(Gradients.shadow(borderColor, 0, y + 2, 0, height - 4, 0.33, MAX_BORDER_RADIUS + 1));
        g.drawRoundRect(x + 2, y + 2, (width - 5) + (clipRight > 0 ? MAX_BORDER_RADIUS : 0), height - 5, MAX_BORDER_RADIUS, MAX_BORDER_RADIUS);
    }

    private void drawOverlay(Graphics2D g, Component c, Color highlight, int x, int y, int width, int height) {
        boolean focus = isFocusOwner(c);

        g.setColor(Color.WHITE);
        g.drawRoundRect(x + 1, y + 1, width - 3, height - 3, MAX_BORDER_RADIUS + 2, MAX_BORDER_RADIUS + 2);

        g.setStroke(new BasicStroke((focus) ? 1.66f : 1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (highlight != null) {
            g.setColor(Colors.transparent(highlight, (focus) ? 0.33 : 0.66));
            g.drawRoundRect(x + 1, y + 1, (width - 3) + (clipRight > 0 ? MAX_BORDER_RADIUS : 0), height - 3, MAX_BORDER_RADIUS + 2, MAX_BORDER_RADIUS + 2);
        }
        else if (focus) {
            g.setColor(FOCUS);
            g.drawRoundRect(x + 1, y + 1, (width - 3) + (clipRight > 0 ? MAX_BORDER_RADIUS : 0), height - 3, MAX_BORDER_RADIUS + 2, MAX_BORDER_RADIUS + 2);
        }
    }

    private boolean isFocusOwner(Component component) {
        if (component.isFocusOwner()) {
            return true;
        }

        if (component instanceof Container) {
            for (Component inner : ((Container) component).getComponents()) {
                if (isFocusOwner(inner)) {
                    return true;
                }
            }
        }

        return false;
    }
}

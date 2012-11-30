package net.ulrice.databinding.viewadapter.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.border.Border;

import net.ulrice.util.Colors;

public class UBorder implements Border {

    private static final Color BORDER = new Color(0x95989e);
    private static final Color SHADOW = new Color(0x28000000, true);
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
        drawBorder(c, g, null, x, y, width, height);
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

    protected void drawBorder(Component c, Graphics g, Color highlight, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.clipRect(x, y, width, height);

        x -= clipLeft;
        width += clipLeft + clipRight;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw original background
        Container parent = c.getParent();
        Color background = Color.ORANGE;

        while (parent != null) {
            background = parent.getBackground();

            if (parent.isOpaque()) {
                break;
            }

            parent = parent.getParent();
        }

        g2d.setColor(background);
        g2d.drawRect(x, y, width - 1, height - 1);
        g2d.drawRect(x + 1, y + 1, width - 3, height - 3);

        boolean focus = isFocusOwner(c);
        Stroke stroke = g2d.getStroke();

        g2d.setStroke(new BasicStroke((focus) ? 1.66f : 1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // draw the highlight
        if (highlight != null) {
            g2d.setColor(Colors.translucent(highlight, (focus) ? 0.66 : 0.33));
            g2d.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 4, 4);
        }
        // draw focus
        else if (focus) {
            g2d.setColor(new Color(0x73a4d1));
            g2d.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 4, 4);
        }

        g2d.setStroke(stroke);

        // draw the inner stuff

        if (c.isOpaque()) {
            g2d.setColor(c.getBackground());

            if (insets.top > 3) {
                g2d.drawRect(x + 3, y + 3, width - 7, insets.top - 4);
            }

            if (insets.bottom > 3) {
                g2d.drawRect(x + 3, (y + height) - insets.bottom, width - 7, insets.bottom - 4);
            }

            if (insets.left > 3) {
                g2d.fillRect(x + 3, y + insets.top, insets.left - 3, height - insets.top - insets.bottom);
            }

            if (insets.right > 3) {
                g2d.fillRect((x + width) - insets.right, y + insets.top, insets.right - 3, height - insets.top - insets.bottom);
            }
        }

        // draw the original border
        if (c.isEnabled()) {
            g2d.setColor(BORDER);
        }
        else {
            g2d.setColor(new Color(0xbfc5ce));
        }

        g2d.drawRect(x + 2, y + 2, width - 5, height - 5);
        // normalBorder.paintBorder(c, g, x + 2, y + 2, width - 4, height - 4);

        if (c.isEnabled()) {
            // draw the shadow
            g2d.setColor(SHADOW);
            g2d.fillRect(x + 2, y + 2, width - 4, 2);
            g2d.fillRect(x + 2, (y + height) - 3, width - 4, 2);
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

package net.ulrice.databinding.viewadapter.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JScrollPane;
import javax.swing.Painter;
import javax.swing.text.JTextComponent;

import net.ulrice.util.Colors;
import net.ulrice.util.Gradients;

public class UBorderPainter implements Painter<Component> {

    private static final int MAX_BORDER_RADIUS = 4;
    private static final int MAX_BORDER_RADIUS_WITH_CORNER = 2;

    private static final Color FOCUS = new Color(0x73a4d1);
    private static final Color BORDER = new Color(0xbfc5ce);

    private final boolean borderVisible;
    private final boolean clipLeft;
    private final boolean clipRight;
    private final Insets insets;

    private boolean focusVisible = true;
    private Insets margin;
    private boolean paintTopMargin = false;
    private boolean paintLeftMargin = false;
    private boolean paintBottomMargin = false;
    private boolean paintRightMargin = false;

    private Color highlight = null;

    public UBorderPainter(boolean borderVisible) {
        this(borderVisible, false, false);
    }

    public UBorderPainter(boolean borderVisible, boolean clipLeft, boolean clipRight) {
        super();

        this.borderVisible = borderVisible;
        this.clipLeft = clipLeft;
        this.clipRight = clipRight;

        insets = new Insets(3, (clipLeft) ? 0 : 3, 3, (clipRight) ? 0 : 3);
    }

    public Color getBackground(Component c) {
        return c.getBackground();
    }

    public int getBorderRadius(Component c) {
        if (c instanceof JScrollPane) {
            return MAX_BORDER_RADIUS_WITH_CORNER;
        }

        return MAX_BORDER_RADIUS;
    }

    public boolean isFocusVisible(Component c) {
        return focusVisible;
    }

    public UBorderPainter setFocusVisible(boolean focusVisible) {
        this.focusVisible = focusVisible;

        return this;
    }

    public Insets getMargin() {
        return margin;
    }

    public Insets getMargin(Component c) {
        if (c instanceof JTextComponent) {
            Insets componentMargin = ((JTextComponent) c).getMargin();

            if (margin == null) {
                return componentMargin;
            }

            return new Insets(componentMargin.top + margin.top, componentMargin.left + margin.left, componentMargin.bottom + margin.bottom, componentMargin.right + margin.right);
        }

        return margin;
    }

    public UBorderPainter setMargin(Insets margin) {
        this.margin = margin;

        return this;
    }

    public boolean isBorderVisible() {
        return borderVisible;
    }

    public UBorderPainter paintMargin() {
        paintTopMargin();
        paintLeftMargin();
        paintBottomMargin();
        paintRightMargin();

        return this;
    }

    public boolean isPaintTopMargin(Component c) {
        return paintTopMargin;
    }

    public UBorderPainter paintTopMargin() {
        paintTopMargin = true;

        return this;
    }

    public boolean isPaintLeftMargin(Component c) {
        return paintLeftMargin;
    }

    public UBorderPainter paintLeftMargin() {
        paintLeftMargin = true;

        return this;
    }

    public boolean isPaintBottomMargin(Component c) {
        return paintBottomMargin;
    }

    public UBorderPainter paintBottomMargin() {
        paintBottomMargin = true;

        return this;
    }

    public boolean isPaintRightMargin(Component c) {
        return paintRightMargin;
    }

    public UBorderPainter paintRightMargin() {
        paintRightMargin = true;

        return this;
    }

    public Insets getBorderInsets(Component c) {
        Insets margin = getMargin(c);

        if (margin != null) {
            return new Insets(margin.top + insets.top, margin.left + insets.left, margin.bottom + insets.bottom, margin.right + insets.right);
        }

        return insets;
    }

    public Color getHighlight() {
        return highlight;
    }

    public UBorderPainter setHighlight(Color highlight) {
        this.highlight = highlight;

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.Painter#paint(java.awt.Graphics2D, java.lang.Object, int, int)
     */
    @Override
    public void paint(Graphics2D g, Component c, int width, int height) {
        paintBorder(g, c, width, height);
    }

    protected void paintBorder(Graphics2D g, Component c, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();

        int borderRadius = getBorderRadius(c);
        g2d.clipRect(0, 0, width, height);

        Rectangle outerBounds = new Rectangle(0, 0, width, height);
        Rectangle innerBounds = new Rectangle(insets.left, insets.top, width - insets.left - insets.right, height - insets.top - insets.bottom);
        Rectangle border = new Rectangle(0, 0, width, height);

        if (clipLeft) {
            border.x -= borderRadius + 2;
            border.width += borderRadius + 2;
        }

        if (clipRight) {
            border.width += borderRadius + 2;
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawMargin((Graphics2D) g2d.create(), c, outerBounds, innerBounds, border, borderRadius);
        drawOverlay((Graphics2D) g2d.create(), c, outerBounds, innerBounds, border, borderRadius);
        drawBorder((Graphics2D) g2d.create(), c, outerBounds, innerBounds, border, borderRadius);
    }

    private void drawMargin(Graphics2D g, Component c, Rectangle outerBounds, Rectangle innerBounds, Rectangle border, int borderRadius) {
        Insets margin = getMargin(c);

        if (margin == null) {
            return;
        }

        boolean paintTopMargin = isPaintTopMargin(c);
        boolean paintLeftMargin = isPaintLeftMargin(c);
        boolean paintBottomMargin = isPaintBottomMargin(c);
        boolean paintRightMargin = isPaintRightMargin(c);

        if ((!paintTopMargin) && (!paintLeftMargin) && (!paintBottomMargin) && (!paintRightMargin)) {
            return;
        }

        g.setColor(getBackground(c));

        if ((paintTopMargin) && (margin.top > 0)) {
            Graphics inlayG = g.create();

            inlayG.clipRect(innerBounds.x, innerBounds.y, innerBounds.width, margin.top);
            inlayG.fillRoundRect(border.x + 2, border.y + 2, border.width - 4, border.height - 4, borderRadius, borderRadius);
        }

        if ((paintBottomMargin) && (margin.bottom > 0)) {
            Graphics inlayG = g.create();

            inlayG.clipRect(innerBounds.x, (innerBounds.y + innerBounds.height) - margin.bottom, innerBounds.width, margin.bottom);
            inlayG.fillRoundRect(border.x + 2, border.y + 2, border.width - 4, border.height - 4, borderRadius, borderRadius);
        }

        if ((paintLeftMargin) && (margin.left > 0)) {
            Graphics inlayG = g.create();

            inlayG.clipRect(innerBounds.x, innerBounds.y, margin.left, innerBounds.height);
            inlayG.fillRoundRect(border.x + 2, border.y + 2, border.width - 4, border.height - 4, borderRadius, borderRadius);
        }

        if ((paintRightMargin) && (margin.right > 0)) {
            Graphics inlayG = g.create();

            inlayG.clipRect((innerBounds.x + innerBounds.width) - margin.right, innerBounds.y, margin.right, innerBounds.height);
            inlayG.fillRoundRect(border.x + 2, border.y + 2, border.width - 4, border.height - 4, borderRadius, borderRadius);
        }
    }

    private void drawBorder(Graphics2D g, Component c, Rectangle outerBounds, Rectangle innerBounds, Rectangle border, int borderRadius) {
        // boolean enabled = c.isEnabled();
        boolean focus = isFocusVisible(c) && isFocusOwner(c);

        // if ((c instanceof JTextComponent) && (!((JTextComponent) c).isEditable())) {
        // enabled = false;
        // }

        Color borderColor = BORDER;

        if (highlight != null) {
            borderColor = Colors.blend(borderColor, highlight, 0.5);
        }
        else if (focus) {
            borderColor = Colors.blend(borderColor, FOCUS, 0.5);
        }

        g.setPaint(Gradients.shadow(borderColor, 0, border.y + 2, 0, border.height - 4, 0.33, borderRadius + 1));
        g.drawRoundRect(border.x + 2, border.y + 2, border.width - 5, border.height - 5, borderRadius, borderRadius);
    }

    private void drawOverlay(Graphics2D g, Component c, Rectangle outerBounds, Rectangle innerBounds, Rectangle border, int borderRadius) {
        boolean focus = isFocusVisible(c) && isFocusOwner(c);

        g.setColor(Color.WHITE);
        g.drawRoundRect(border.x + 1, border.y + 1, border.width - 3, border.height - 3, borderRadius + 2, borderRadius + 2);

        g.setStroke(new BasicStroke((focus) ? 1.66f : 1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (highlight != null) {
            g.setColor(Colors.transparent(highlight, (focus) ? 0.33 : 0.66));
            g.drawRoundRect(border.x + 1, border.y + 1, border.width - 3, border.height - 3, borderRadius + 2, borderRadius + 2);
        }
        else if (focus) {
            g.setColor(FOCUS);
            g.drawRoundRect(border.x + 1, border.y + 1, border.width - 3, border.height - 3, borderRadius + 2, borderRadius + 2);
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

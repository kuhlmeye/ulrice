package net.ulrice.databinding.viewadapter.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.ImageObserver;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

import net.ulrice.databinding.ui.BindingUIConstants;
import net.ulrice.databinding.viewadapter.IFStateMarker;

/**
 * Implementation of the state marker interface. Draws the border and an image depending on the current state of the
 * data in the attribute model.
 * 
 * @author christof
 */
public class BorderStateMarker implements Border, ImageObserver, IFStateMarker {

    private static final Color SHADOW = new Color(0x18000000, true);

    /** The attention image drawn, if the data is changed. */
    private final Icon changedIcon;

    /** The cross image draw, if the data is not valid. */
    private final Icon invalidIcon;

    private boolean valid = true;

    private boolean dirty = false;

    private final BorderStateMarkerStrategy strategy;

    private final Border normalBorder;

    private final Insets insets;

    /**
     * Creates a new border state marker.
     */
    public BorderStateMarker(Border normalBorder) {
        this(BorderStateMarkerStrategy.BORDER_ONLY, normalBorder);
    }

    public BorderStateMarker(BorderStateMarkerStrategy strategy, Border normalBorder) {
        this(strategy, normalBorder, UIManager.getInsets(BindingUIConstants.BORDER_STATE_MARKER_INSETS) != null ? UIManager
            .getInsets(BindingUIConstants.BORDER_STATE_MARKER_INSETS) : new Insets(2, 3, 2, 3));
    }

    /**
     * Creates a new border state marker.
     * 
     * @param iconOnly true, if only the icon should be shown.
     */
    public BorderStateMarker(BorderStateMarkerStrategy strategy, Border normalBorder, Insets insets) {
        changedIcon = UIManager.getIcon(BindingUIConstants.BORDER_STATE_MARKER_CHANGED_IMAGE);
        invalidIcon = UIManager.getIcon(BindingUIConstants.BORDER_STATE_MARKER_INVALID_IMAGE);
        this.strategy = strategy;
        this.normalBorder = normalBorder;
        this.insets = insets;
    }

    /**
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    /**
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (!valid) {
            drawInvalid(c, g, x, y, width, height);
        }
        else {
            if (dirty) {
                drawChanged(c, g, x, y, width, height);
            }
            else {
                drawNormal(c, g, x, y, width, height);
            }
        }
    }

    /**
     * Draws a normal border.
     * 
     * @param g The graphics.
     * @param x The x coordinate of the component.
     * @param y The y coordinate of the component.
     * @param width The width of the component.
     * @param height The height of the component.
     */
    private void drawNormal(Component c, Graphics g, int x, int y, int width, int height) {
        if (normalBorder != null) {
            g.setColor(c.getBackground());
            g.drawRect(x + 1, y + 1, width - 3, height - 3);
            g.setColor(SHADOW);
            g.drawRect(x + 1, y + 1, width - 2, 0);
            normalBorder.paintBorder(c, g, x, y, width, height);
        }
    }

    /**
     * Draws a changed border.
     * 
     * @param g The graphics.
     * @param x The x coordinate of the component.
     * @param y The y coordinate of the component.
     * @param width The width of the component.
     * @param height The height of the component.
     */
    private void drawChanged(Component c, Graphics g, int x, int y, int width, int height) {
        if (strategy != BorderStateMarkerStrategy.ICON_ONLY) {
            g.setColor(c.getBackground());
            g.drawRect(x + 1, y + 1, width - 3, height - 3);
            g.setColor(SHADOW);
            g.drawRect(x + 1, y + 1, width - 2, 0);
            g.setColor(UIManager.getColor(BindingUIConstants.BORDER_STATE_MARKER_CHANGED_BORDER));
            g.drawRect(x, y, width - 1, height - 1);
        }
        if ((strategy != BorderStateMarkerStrategy.BORDER_ONLY) && (changedIcon != null)) {
            changedIcon.paintIcon(c, g, (x + width) - 10, 0);
        }
    }

    /**
     * Draws an invalid border.
     * 
     * @param g The graphics.
     * @param x The x coordinate of the component.
     * @param y The y coordinate of the component.
     * @param width The width of the component.
     * @param height The height of the component.
     */
    private void drawInvalid(Component c, Graphics g, int x, int y, int width, int height) {
        if (strategy != BorderStateMarkerStrategy.ICON_ONLY) {
            g.setColor(c.getBackground());
            g.drawRect(x + 1, y + 1, width - 3, height - 3);
            g.setColor(SHADOW);
            g.drawRect(x + 1, y + 1, width - 2, 0);
            g.setColor(UIManager.getColor(BindingUIConstants.BORDER_STATE_MARKER_INVALID_BORDER));
            g.drawRect(x, y, width - 1, height - 1);
        }
        if ((strategy != BorderStateMarkerStrategy.BORDER_ONLY) && (invalidIcon != null)) {
            invalidIcon.paintIcon(c, g, (x + width) - 10, 0);
        }
    }

    /**
     * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }

    /**
     * @see net.ulrice.databinding.viewadapter.IFStateMarker#paintState(net.ulrice.databinding.DataState)
     */
    @Override
    public void updateState(Object value, boolean enabled, boolean dirty, boolean valid, JComponent c) {
        this.valid = valid;
        this.dirty = dirty;
        c.revalidate();
        c.repaint();
    }

    @Override
    public void initialize(JComponent component) {
        // component.setBorder(this);
    }

}

package net.ulrice.databinding.viewadapter.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.Border;

public class UBorder implements Border {

    private final UBorderPainter painter;

    public UBorder(boolean borderVisible) {
        this(borderVisible, false, false);
    }

    public UBorder(boolean borderVisible, boolean clipLeft, boolean clipRight) {
        super();

        painter = new UBorderPainter(borderVisible, clipLeft, clipRight);
    }

    @Deprecated
    public UBorder(boolean borderVisible, Insets baseInsets, boolean clipLeft, boolean clipRight) {
        this(borderVisible, clipLeft, clipRight);
    }

    public Insets getMargin() {
        return painter.getMargin();
    }

    public UBorder setMargin(Insets margin) {
        painter.setMargin(margin);

        return this;
    }

    public boolean isBorderVisible() {
        return painter.isBorderVisible();
    }

    public UBorder paintMargin() {
        painter.paintMargin();

        return this;
    }

    public UBorder paintTopMargin() {
        painter.paintTopMargin();

        return this;
    }

    public UBorder paintLeftMargin() {
        painter.paintLeftMargin();

        return this;
    }

    public UBorder paintBottomMargin() {
        painter.paintBottomMargin();

        return this;
    }

    public UBorder paintRightMargin() {
        painter.paintRightMargin();

        return this;
    }
    
    public Color getHighlight() {
        return painter.getHighlight();
    }

    public UBorder setHighlight(Color highlight) {
        painter.setHighlight(highlight);
        
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        painter.paint((Graphics2D) g.create(x, y, width, height), c, width, height);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return painter.getBorderInsets(c);
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

}

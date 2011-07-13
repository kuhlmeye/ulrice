package net.ulrice.databinding.viewadapter.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.Border;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.viewadapter.IFStateMarker;

/**
 * Implementation of the state marker interface. Draws the border and an image
 * depending on the current state of the data in the attribute model.
 * 
 * @author christof
 */
public class BorderStateMarker implements Border, ImageObserver, IFStateMarker {

    /** The attention image drawn, if the data is changed. */
    private ImageIcon attentionImage;
    
    /** The cross image draw, if the data is not valid. */
    private ImageIcon crossImage;
    
    /** The current state of the data. */
    private DataState state;

    /**
     * Creates a new border state marker.
     */
    public BorderStateMarker() {
        // TODO Add to uimanager
        attentionImage = new ImageIcon(getClass().getResource("attention.png"));
        // TODO Add to uimanager
        crossImage = new ImageIcon(getClass().getResource("cross.png"));
    }

    /**
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(Component c) {
        // TODO Add to uimanager
        return new Insets(2, 2, 2, 2);
    }

    /**
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    /**
     * @see javax.swing.border.Border#paintBorder(java.awt.Component,
     *      java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (state != null) {
            switch (state) {
                case Invalid:
                    drawInvalid(g, x, y, width, height);
                    break;
                case Changed:
                    drawChanged(g, x, y, width, height);
                    break;
                default:
                    drawNormal(g, x, y, width, height);
                    break;
            }
        } else {            
            drawNormal(g, x, y, width, height);
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
    private void drawNormal(Graphics g, int x, int y, int width, int height) {
        // TODO Add colors to uimanager
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(x, y, width - 1, height - 1);
        // TODO Add colors to uimanager
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x + 1, y + 1, width - 3, height - 3);
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
    private void drawChanged(Graphics g, int x, int y, int width, int height) {
        // TODO Add colors to uimanager
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(x, y, width - 1, height - 1);
        // TODO Add colors to uimanager
        g.setColor(new Color(130, 130, 30));
        g.drawRect(x + 1, y + 1, width - 3, height - 3);

        g.drawImage(attentionImage.getImage(), x + width - 10, 0, 10, 10, this);
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
    private void drawInvalid(Graphics g, int x, int y, int width, int height) {
        // TODO Add colors to uimanager
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(x, y, width - 1, height - 1);
        // TODO Add colors to uimanager
        g.setColor(new Color(100, 30, 30));
        g.drawRect(x + 1, y + 1, width - 3, height - 3);

        g.drawImage(crossImage.getImage(), x + width - 10, 0, 10, 10, this);
    }

    /**
     * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int,
     *      int, int, int)
     */
    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }

    /**
     * @see net.ulrice.databinding.viewadapter.IFStateMarker#paintState(net.ulrice.databinding.DataState)
     */
    @Override
    public void updateState(IFBinding binding, JComponent c) {
        this.state = binding.getState();
        c.revalidate();
    }
}

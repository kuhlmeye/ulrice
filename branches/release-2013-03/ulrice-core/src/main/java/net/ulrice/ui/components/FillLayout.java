package net.ulrice.ui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.Serializable;

public class FillLayout implements LayoutManager, Serializable {

    private static final long serialVersionUID = -3506503478664173278L;

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    @Override
    public void removeLayoutComponent(Component comp) {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int width = 0;
        int height = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getPreferredSize();

                width = Math.max(width, size.width);
                height = Math.max(height, size.height);
            }
        }

        return new Dimension(width, height);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        int width = 0;
        int height = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getMinimumSize();

                width = Math.max(width, size.width);
                height = Math.max(height, size.height);
            }
        }

        return new Dimension(width, height);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    @Override
    public void layoutContainer(Container parent) {
        Rectangle bounds = new Rectangle(0, 0, parent.getWidth(), parent.getHeight());

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                component.setBounds(bounds);
            }
        }
    }

}

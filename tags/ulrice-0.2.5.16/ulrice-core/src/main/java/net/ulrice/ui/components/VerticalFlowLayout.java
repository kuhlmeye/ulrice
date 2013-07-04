package net.ulrice.ui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.Serializable;

public class VerticalFlowLayout implements LayoutManager, Serializable {

    private static final long serialVersionUID = 8377737099056548957L;

    public VerticalFlowLayout() {
        super();
    }

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
                Dimension maximumSize = component.getMaximumSize();

                width = Math.max(width, Math.min(size.width, maximumSize.width));
                height += Math.min(size.height, maximumSize.height);
            }
        }

        Insets insets = parent.getInsets();

        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

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
                Dimension maximumSize = component.getMaximumSize();

                width = Math.max(width, Math.min(size.width, maximumSize.width));
                height += Math.min(size.height, maximumSize.height);
            }
        }

        Insets insets = parent.getInsets();

        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        Rectangle bounds = new Rectangle(insets.left, insets.top, parent.getWidth() - insets.left - insets.right, 0);

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getPreferredSize();
                Dimension maximumSize = component.getMaximumSize();

                bounds.height = Math.min(size.height, maximumSize.height);

                component.setBounds(bounds);

                bounds.y += bounds.height;
            }
        }
    }

}

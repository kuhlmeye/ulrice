package net.ulrice.ui.accordionpanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.Serializable;

public class AccordionContentPanelLayout implements LayoutManager2, Serializable {

    private static final long serialVersionUID = -4912497182205483391L;

    public AccordionContentPanelLayout() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
     */
    @Override
    public void addLayoutComponent(String name, Component component) {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, java.lang.Object)
     */
    @Override
    public void addLayoutComponent(Component component, Object constraints) {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    @Override
    public void removeLayoutComponent(Component component) {
        // intentionally left blank
    }

    /**
     * Returns the preferred size. If the {@link AccordionContentPanel} is standalone, the height is calculated using
     * the {@link AccordionContentPanel#getUnfoldedFactor()}, if it is not standalone, the height is the prefered
     * size. {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        AccordionContentPanel contentPanel = (AccordionContentPanel) parent;
        boolean standalone = contentPanel.isStandalone();
        int width = 0;
        int height = 0;

        for (Component component : contentPanel.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getPreferredSize();
                Dimension maximumSize = component.getMaximumSize();

                width = Math.max(width, size.width);

                if (contentPanel.isContent(component)) {
                    if (standalone) {
                        height += Math.min(size.height, maximumSize.height) * contentPanel.getUnfoldedFactor();
                    }
                    else {
                        height += Math.min(size.height /* component.getMinimumSize().height */, maximumSize.height);
                    }
                }
                else {
                    height += Math.min(size.height, maximumSize.height);
                }
            }
        }

        Insets insets = contentPanel.getInsets();

        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    /**
     * Returns the minimum size. If the {@link AccordionContentPanel} is standalone, the height is calculated using
     * the {@link AccordionContentPanel#getUnfoldedFactor()}, if it is not standalone, the height of the content is
     * ignored. {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        AccordionContentPanel contentPanel = (AccordionContentPanel) parent;
        boolean standalone = contentPanel.isStandalone();
        int width = 0;
        int height = 0;

        for (Component component : contentPanel.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getMinimumSize();
                Dimension maximumSize = component.getMaximumSize();

                width = Math.max(width, size.width);

                if (contentPanel.isContent(component)) {
                    if (standalone) {
                        height += Math.min(size.height, maximumSize.height) * contentPanel.getUnfoldedFactor();
                    }
                }
                else {
                    height += Math.min(size.height, maximumSize.height);
                }
            }
        }

        Insets insets = contentPanel.getInsets();

        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension maximumLayoutSize(Container parent) {
        AccordionContentPanel contentPanel = (AccordionContentPanel) parent;
        int width = 0;
        int height = 0;

        for (Component component : contentPanel.getComponents()) {
            Dimension size = component.getPreferredSize();
            Dimension maximumSize = component.getMaximumSize();

            width = Math.max(width, size.width);
            height += Math.min(size.height, maximumSize.height);
        }

        Insets insets = contentPanel.getInsets();

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
        AccordionContentPanel contentPanel = (AccordionContentPanel) parent;
        boolean standalone = contentPanel.isStandalone();
        Insets insets = contentPanel.getInsets();
        Rectangle bounds = new Rectangle(insets.left, insets.top, contentPanel.getWidth() - insets.left - insets.right, 0);
        int fixedHeight = 0;

        for (Component component : contentPanel.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getPreferredSize();
                Dimension maximumSize = component.getMaximumSize();

                if (!contentPanel.isContent(component)) {
                    fixedHeight += Math.min(size.height, maximumSize.height);
                }
            }
        }

        int availableHeight = parent.getHeight() - insets.top - insets.bottom;

        for (Component component : contentPanel.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getPreferredSize();
                Dimension maximumSize = component.getMaximumSize();

                if (contentPanel.isContent(component)) {
                    if (standalone) {
                        bounds.height = (int) (Math.min(size.height, maximumSize.height) * contentPanel.getUnfoldedFactor());
                    }
                    else {
                        bounds.height = availableHeight - fixedHeight;
                    }
                }
                else {
                    bounds.height = Math.min(size.height, maximumSize.height);
                }

                component.setBounds(bounds);

                bounds.y += bounds.height;
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
     */
    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0f;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
     */
    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0f;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
     */
    @Override
    public void invalidateLayout(Container target) {
        // intentionally left blank
    }

}

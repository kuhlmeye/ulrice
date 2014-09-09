package net.ulrice.ui.accordionpanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AccordionPanelLayout implements LayoutManager2, Serializable {

    private static final long serialVersionUID = 3995513185399214205L;

    private static class Entry implements Serializable {

        private static final long serialVersionUID = -2291117138956762182L;

        private final double weight;

        public Entry(double weight) {
            super();

            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }

    }

    private final Map<Component, Entry> entries = new HashMap<Component, Entry>();

    private AccordionPanel accordionPanel = null;

    public AccordionPanelLayout() {
        super();
    }

    public AccordionPanel getAccordionPanel(Container container) {
        if (accordionPanel != null) {
            return accordionPanel;
        }

        while (container != null) {
            if (container instanceof AccordionPanel) {
                break;
            }

            container = container.getParent();
        }

        accordionPanel = (AccordionPanel) container;

        return accordionPanel;
    }

    public double getWeight(Component component) {
        Entry entry = entries.get(component);
        double weight = (entry != null) ? entry.getWeight() : 0d;

        return weight;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
     */
    @Override
    public void addLayoutComponent(String name, Component component) {
        if (name == null) {
            addLayoutComponent(component, null);
        }
        else {
            try {
                addLayoutComponent(component, Double.parseDouble(name));
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Contraints must be Number (weight): " + name, e);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, java.lang.Object)
     */
    @Override
    public void addLayoutComponent(Component component, Object constraints) {
        if (constraints == null) {
            constraints = Double.valueOf(0);
        }
        else if (!(constraints instanceof Number)) {
            throw new IllegalArgumentException("Contraints must be Number (weight): " + constraints);
        }

        entries.put(component, new Entry(((Number) constraints).doubleValue()));
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    @Override
    public void removeLayoutComponent(Component component) {
        entries.remove(component);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        AccordionPanel accordionPanel = getAccordionPanel(parent);
        int width = 0;
        int height = 0;
        int count = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                Dimension preferredSize = getPreferredSize(component);

                width = Math.max(width, preferredSize.width);
                height += preferredSize.height;
                count += 1;
            }
        }

        if (count > 1) {
            height += (count - 1) * accordionPanel.getGap();
        }

        Insets insets = parent.getInsets();

        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    private Dimension getPreferredSize(Component component) {
        if (!component.isVisible()) {
            return new Dimension(0, 0);
        }

        Dimension maximumSize = component.getMaximumSize();
        Dimension preferredSize = component.getPreferredSize();
        int width = Math.min(preferredSize.width, maximumSize.height);
        int height = Math.min(preferredSize.height, maximumSize.height);

        if (component instanceof AccordionContentPanel) {
            height = Math.min(component.getMinimumSize().height, maximumSize.height);

            int foldingHeight = preferredSize.height - height;

            height += (int) (foldingHeight * ((AccordionContentPanel) component).getUnfoldedFactor());
        }
        else if (getWeight(component) > 0) {
            height = Math.min(component.getMinimumSize().height, maximumSize.height);
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
        AccordionPanel accordionPanel = getAccordionPanel(parent);
        int width = 0;
        int height = 0;
        int count = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                Dimension minimumSize = getMinimumSize(component);

                width = Math.max(width, minimumSize.width);
                height += minimumSize.height;
                count += 1;
            }
        }

        if (count > 1) {
            height += (count - 1) * accordionPanel.getGap();
        }

        Insets insets = parent.getInsets();

        width += insets.left + insets.right;
        height += insets.top + insets.bottom;

        return new Dimension(width, height);
    }

    /**
     * Returns the minimum size of the component. If the component is an {@link AccordionContentPanel} the minimum
     * height is computed using the unfolded factor
     *
     * @param component the component
     * @return the minimum size
     */
    private Dimension getMinimumSize(Component component) {
        if (!component.isVisible()) {
            return new Dimension(0, 0);
        }

        Dimension maximumSize = component.getMaximumSize();
        Dimension minimumSize = component.getMinimumSize();
        int width = Math.min(minimumSize.width, maximumSize.height);
        int height = Math.min(minimumSize.height, maximumSize.height);

        if (component instanceof AccordionContentPanel) {
            int foldingHeight = component.getPreferredSize().height - minimumSize.height;

            height += (int) (foldingHeight * ((AccordionContentPanel) component).getUnfoldedFactor());
        }

        return new Dimension(width, height);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension maximumLayoutSize(Container parent) {
        int width = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                Dimension maximumSize = component.getMaximumSize();

                width = Math.max(width, maximumSize.width);
            }
        }

        Insets insets = parent.getInsets();

        width += insets.left + insets.right;

        return new Dimension(width, Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    @Override
    public void layoutContainer(Container parent) {
        AccordionPanel accordionPanel = getAccordionPanel(parent);
        double totalWeight = 0;
        int minimumHeight = 0;
        int visibleComponentCount = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                double weight = getWeight(component);

                if (component instanceof AccordionContentPanel) {
                    weight *= ((AccordionContentPanel) component).getUnfoldedFactor();
                }

                totalWeight += weight;
                minimumHeight += getPreferredSize(component).height + accordionPanel.getGap();
                visibleComponentCount += 1;
            }
        }

        if (visibleComponentCount > 0) {
            minimumHeight -= accordionPanel.getGap();
        }

        int totalSpare = parent.getHeight() - minimumHeight;
        Insets insets = parent.getInsets();
        Rectangle bounds = new Rectangle(insets.left, insets.top, parent.getWidth() - insets.left - insets.right, 0);

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                double weight = getWeight(component);
                Dimension preferredSize = getPreferredSize(component);
                int height = preferredSize.height;

                if (component instanceof AccordionContentPanel) {
                    weight *= ((AccordionContentPanel) component).getUnfoldedFactor();
                }

                if (weight > 0) {
                    if (component instanceof AccordionContentPanel) {
                        weight *= ((AccordionContentPanel) component).getUnfoldedFactor();
                    }

                    if (totalSpare > 0) {
                        height += (totalSpare / totalWeight) * weight;
                    }
                }

                bounds.height = height;

                component.setBounds(bounds);

                bounds.y += bounds.height;
                bounds.y += accordionPanel.getGap();
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

package net.ulrice.ui.accordionpanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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

    public AccordionPanelLayout() {
        super();
    }

    public double getWeight(Component component) {
        Entry entry = entries.get(component);

        return (entry != null) ? entry.getWeight() : 0d;
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
        int width = 0;
        int height = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                Dimension size = component.getPreferredSize();

                width = Math.max(width, size.width);
                height += size.height;
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

                if (getWeight(component) > 0) {
                    height += size.height;
                }
                else {
                    height += component.getPreferredSize().height;
                }
            }
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
                Dimension size = component.getMaximumSize();

                width = Math.max(width, size.width);
            }
        }

        return new Dimension(width, Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    @Override
    public void layoutContainer(Container parent) {
        double totalWeight = 0;
        int minimumHeight = 0;

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                double weight = getWeight(component);

                totalWeight += weight;

                if (weight > 0) {
                    minimumHeight += component.getMinimumSize().height;
                }
                else {
                    minimumHeight += component.getPreferredSize().height;
                }
            }
        }

        int totalSpare = parent.getHeight() - minimumHeight;
        int restSpare = totalSpare;
        Rectangle bounds = new Rectangle();

        bounds.width = parent.getWidth();

        for (Component component : parent.getComponents()) {
            if (component.isVisible()) {
                double weight = getWeight(component);

                if (weight > 0) {
                    Dimension size = component.getMinimumSize();

                    if (restSpare > 0) {
                        int spare = (int) ((totalSpare / totalWeight) * weight);

                        if (spare > restSpare) {
                            spare = restSpare;
                        }

                        bounds.height = size.height + spare;
                        restSpare -= spare;
                    }
                    else {
                        bounds.height = size.height;
                    }
                }
                else {
                    Dimension size = component.getPreferredSize();

                    bounds.height = size.height;
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

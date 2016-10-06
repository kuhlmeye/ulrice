package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.ulrice.util.Colors;
import net.ulrice.util.Gradients;

/**
 * Panel with coloured background
 * 
 * @author Stefan Huber, EEXHANT
 */
public class ColoredPanel extends JPanel {

    private static final long serialVersionUID = 4952054987230225700L;

    public static interface PaintStrategy {

        void paint(Graphics2D g, int width, int height, Color foreground, Color background);

    }

    public static PaintStrategy RAISED_PAINT = new PaintStrategy() {

        /**
         * {@inheritDoc}
         * 
         * @see net.ulrice.ui.components.ColoredPanel.PaintStrategy#paint(java.awt.Graphics2D, int, int,
         *      java.awt.Color, java.awt.Color)
         */
        @Override
        public void paint(Graphics2D g, int width, int height, Color foreground, Color background) {
            g.setPaint(Gradients.shadow(background, 0, height, 0, -height, 0.33, 3));
            g.fillRect(0, 0, width, height);

            g.setColor(Color.WHITE);
            g.drawLine(0, height - 1, width, height - 1);
        }
    };

    public static PaintStrategy LOWERED_PAINT = new PaintStrategy() {

        /**
         * {@inheritDoc}
         * 
         * @see net.ulrice.ui.components.ColoredPanel.PaintStrategy#paint(java.awt.Graphics2D, int, int,
         *      java.awt.Color, java.awt.Color)
         */
        @Override
        public void paint(Graphics2D g, int width, int height, Color foreground, Color background) {
            g.setPaint(Gradients.shadow(background, 0, height, 0.33, 6));
            g.fillRect(0, 0, width, height);

            g.setColor(Colors.blend(background, Color.BLACK, 0.5));
            g.drawLine(0, 0, width, 0);
        }
    };

    public static ColoredPanel create(Color color, Component content) {
        ColoredPanel result = new ColoredPanel(new PaintStrategy() {
            
            @Override
            public void paint(Graphics2D g, int width, int height, Color foreground, Color background) {
                g.setColor(background);
                g.fillRect(0, 0, width, height);
            }
        }, content);
        
        if (color != null) {
            result.setBackground(color);
        }

        return result;
    }
    
    public static ColoredPanel createRaised(Component content) {
        return createRaised(new Color(0xe3e3e3), content);
    }

    public static ColoredPanel createRaised(Color color, Component content) {
        ColoredPanel result = new ColoredPanel(RAISED_PAINT, content);

        if (color != null) {
            result.setBackground(color);
        }

        return result;
    }

    public static ColoredPanel createLowered(Component content) {
        return createLowered(new Color(0xe3e3e3), content);
    }

    public static ColoredPanel createLowered(Color color, Component content) {
        ColoredPanel result = new ColoredPanel(LOWERED_PAINT, content);

        if (color != null) {
            result.setBackground(color);
        }

        return result;
    }

    private final PaintStrategy paintStrategy;

    public ColoredPanel(Component content) {
        this(RAISED_PAINT, content);
    }

    public ColoredPanel(PaintStrategy paintStrategy, Component content) {
        super(new BorderLayout());

        this.paintStrategy = paintStrategy;

        if (content instanceof JComponent) {
            ((JComponent) content).setOpaque(false);
        }

        add(content);
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        paintStrategy.paint(g2, getWidth(), getHeight(), getForeground(), getBackground());

        super.paintComponent(g);
    }

}

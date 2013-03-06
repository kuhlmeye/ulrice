package net.ulrice.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import net.ulrice.util.Colors;

/**
 * Panel with coloured background
 * 
 * @author EXSTHUB, EEXHANT
 */
public class ColoredPanel extends JPanel {

    private static final long serialVersionUID = 4952054987230225700L;

    public static interface PaintStrategy {

        Paint getPaint(Color foreground, Color background, int height);

    }

    public static PaintStrategy RAISED_PAINT = new PaintStrategy() {

        @Override
        public Paint getPaint(Color foreground, Color background, int height) {
            Color shadowColor = Colors.darker(background, 0.1);
            Color contentColor = background;
            Color darkerColor = Colors.darker(background, 0.08);

            if (height > 15) { // better looking effect
                return new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(0, height), //
                    new float[] { 0.0f, 0.01f, 1.0f - (12.0f / height), 1.0f }, //
                    new Color[] { shadowColor, contentColor, contentColor, darkerColor });
            }

            // alternative effect, only uses relative values
            return new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(0, height), //
                new float[] { 0.0f, 0.01f, 0.93f, 1.0f }, //
                new Color[] { shadowColor, contentColor, contentColor, darkerColor });
        }

    };

    public static PaintStrategy LOWERED_PAINT = new PaintStrategy() {

        @Override
        public Paint getPaint(Color foreground, Color background, int height) {
            Color lineColor = Colors.darker(background, 0.2);
            Color shadowColor = Colors.darker(background, 0.1);
            Color contentColor = background;
            float fractionA = (float) Math.min(1.0 / height, 0.25);
            float fractionB = (float) Math.min(8.0 / height, 0.5);

            return new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(0, height), new float[] { 0.0f, fractionA, fractionB, 1.0f }, new Color[] {
                lineColor, shadowColor, contentColor, contentColor });
        }

    };

    public static ColoredPanel createRaised(JPanel content) {
        return createRaised(null, content);
    }

    public static ColoredPanel createRaised(Color color, JPanel content) {
        ColoredPanel result = new ColoredPanel(RAISED_PAINT, content);

        if (color != null) {
            result.setBackground(color);
        }

        return result;
    }

    public static ColoredPanel createLowered(JPanel content) {
        return createLowered(new Color(0xe3e3e3), content);
    }

    public static ColoredPanel createLowered(Color color, JPanel content) {
        ColoredPanel result = new ColoredPanel(LOWERED_PAINT, content);

        if (color != null) {
            result.setBackground(color);
        }

        return result;
    }

    private final PaintStrategy paintStrategy;

    public ColoredPanel(JPanel content) {
        this(RAISED_PAINT, content);
    }

    public ColoredPanel(PaintStrategy paintStrategy, JPanel content) {
        super(new BorderLayout());

        this.paintStrategy = paintStrategy;

        content.setOpaque(false);

        add(content);
        setBackground(new Color(0xecf4fb));
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint(paintStrategy.getPaint(getForeground(), getBackground(), getHeight()));
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }

}

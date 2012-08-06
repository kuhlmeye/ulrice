package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Panel with coloured background
 * 
 * @author EXSTHUB, EEXHANT
 */
public class ColoredPanel extends JPanel {
    private static final long serialVersionUID = 4952054987230225700L;
    private static final Color WHITE_BLUE = new Color(0xecf4fb);
    public static final Color LIGHT_BLUE = new Color(0xbed3e4);

    public ColoredPanel(JPanel content) {
        super(new BorderLayout());

        content.setOpaque(false);

        add(content);

        setOpaque(false);
        setBackground(WHITE_BLUE);
        add(new JSeparator(), BorderLayout.SOUTH);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        LinearGradientPaint paint = null;

        //TODO maybe refactor
        
        if (getHeight() > 15) { //better looking effect
            paint = new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(0, getHeight()), //
                new float[] { 0.0f, 0.01f, 1.0f - 12.0f / getHeight(), 1.0f }, //
                new Color[] { LIGHT_BLUE, WHITE_BLUE, WHITE_BLUE, Colors.darker(getBackground(), 0.08) });
        }
        else { //alternative effect, only uses relative values
            paint = new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(0, getHeight()), //
                new float[] { 0.0f, 0.01f, 0.93f, 1.0f }, //
                new Color[] { LIGHT_BLUE, WHITE_BLUE, WHITE_BLUE, Colors.darker(getBackground(), 0.08) });
        }

        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);

    }

}

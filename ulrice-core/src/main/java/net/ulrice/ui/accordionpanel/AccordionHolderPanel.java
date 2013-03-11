package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import net.ulrice.util.Colors;

public class AccordionHolderPanel extends JPanel {

    private static final long serialVersionUID = 7528047902693951355L;

    public AccordionHolderPanel(Color backgroundColor) {
        super(new BorderLayout());

        setOpaque(false);
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(10, 0));
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();

        Graphics2D g2 = (Graphics2D) g;
        LinearGradientPaint paint =
                new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(width, 0), new float[] { 0.0f, 1.0f / width, 0.5f, 1.0f - (2.0f / width), 1.0f }, new Color[] {
                    Colors.brighter(getBackground(), 0.01), getBackground(), getBackground(), Colors.darker(getBackground(), 0.1), Colors.darker(getBackground(), 0.5) });

        g2.setPaint(paint);
        g2.fillRect(0, 0, width, getHeight());

        super.paintComponent(g);
    }

}

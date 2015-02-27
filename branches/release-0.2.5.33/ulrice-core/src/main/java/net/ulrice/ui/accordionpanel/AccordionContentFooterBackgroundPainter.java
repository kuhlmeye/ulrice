package net.ulrice.ui.accordionpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.Painter;

import net.ulrice.util.Gradients;

public class AccordionContentFooterBackgroundPainter implements Painter<Component> {

    public AccordionContentFooterBackgroundPainter() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.Painter#paint(java.awt.Graphics2D, java.lang.Object, int, int)
     */
    @Override
    public void paint(Graphics2D g, Component c, int width, int height) {
        Color color = c.getBackground();
        LinearGradientPaint paint = Gradients.pressed(color, 0, height, 0.5, 0);

        g.setPaint(paint);
        g.fillRect(0, 0, width, height);
    }

}

package net.ulrice.ui.accordionpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.JComponent;

import net.ulrice.util.Gradients;

public class AccordionContentFooter extends JComponent {

    private static final long serialVersionUID = 1153838793613240710L;

    public AccordionContentFooter() {
        super();

        setOpaque(false);

        setPreferredSize(new Dimension(0, 6));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        setMinimumSize(new Dimension(0, 6));
    }

    @Override
    public void paintComponent(Graphics g) {
        AccordionContentPanel accordionContentPanel = (AccordionContentPanel) getParent();
        Color color = accordionContentPanel.getContent().getBackground();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        LinearGradientPaint paint = Gradients.pressed(color, 0, height, 1, 1);
        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), height);

        super.paintComponent(g);
    }
}

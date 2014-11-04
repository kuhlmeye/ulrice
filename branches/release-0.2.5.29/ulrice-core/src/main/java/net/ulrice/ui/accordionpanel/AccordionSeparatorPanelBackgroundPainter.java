package net.ulrice.ui.accordionpanel;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.Painter;

import net.ulrice.util.Colors;
import net.ulrice.util.Gradients;

public class AccordionSeparatorPanelBackgroundPainter implements Painter<AccordionSeparatorPanel> {

    private static final Color FOCUS_COLOR = new Color(0x73a4d1);

    public AccordionSeparatorPanelBackgroundPainter() {
        super();
    }

    @Override
    public void paint(Graphics2D g, AccordionSeparatorPanel component, int width, int height) {
        Color background = component.getBackground();
        boolean mouseOver = component.isMouseOver();
        boolean pressed = component.isPressed();

        // draw background
        if ((mouseOver) || (pressed)) {
            background = Colors.blend(background, FOCUS_COLOR, 0.25);
        }

        if ((pressed) && (mouseOver)) {
            g.setPaint(Gradients.curved(background, 0, 2, 0, height + 2, 0.5, 0.66));
        }
        else {
            g.setPaint(Gradients.curved(background, 0, 0, 0, height, 0.5, 0.66));
        }

        g.fillRect(0, 0, width, height);

        if ((pressed) && (mouseOver)) {
            g.setPaint(Gradients.shadow(new Color(0x00000000, true), 0, 0, 0, height, 0.25, 4));
            g.fillRect(0, 0, width, height);
        }

        // draw inner border
        if ((pressed) && (mouseOver)) {
            g.setPaint(Gradients.shadow(Colors.transparent(Color.WHITE, 0.66), 0, 1, 0, height - 3, 0.125, 16));
        }
        else {
            g.setPaint(Colors.transparent(Color.WHITE, 0.33));
        }

        g.drawRect(0, 0, width - 1, height - 2);

        // draw outer border
        g.setPaint(Colors.transparent(Color.BLACK, 0.75));
        g.drawLine(0, height - 1, width, height - 1);

        if (pressed) {
            g.translate(0, 1);
        }
    }

}

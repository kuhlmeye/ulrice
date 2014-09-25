package net.ulrice.ui.accordionpanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIManager;

public class AccordionContentFooter extends JComponent {

    private static final long serialVersionUID = 1153838793613240710L;

    public static final String UI_NAME = AccordionContentFooter.class.getSimpleName();
    public static final String UI_HEIGHT = UI_NAME + ".height";
    public static final String UI_BACKGROUND_PAINTER = UI_NAME + ".backgroundPainter";

    private static final Painter<Component> UI_BACKGROUND_PAINTER_DEFAULT = new AccordionContentFooterBackgroundPainter();

    private Painter<Component> painter;

    public AccordionContentFooter() {
        super();

        updateUI();
        setOpaque(false);
    }

    @Override
    public void updateUI() {
        super.updateUI();

        Object value = UIManager.get(UI_HEIGHT);
        int height = (value instanceof Integer) ? ((Integer) value).intValue() : 6;

        setPreferredSize(new Dimension(0, height));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        setMinimumSize(new Dimension(0, height));

        @SuppressWarnings("unchecked")
        Painter<Component> painter = (Painter<Component>) UIManager.get(UI_BACKGROUND_PAINTER);

        this.painter = (painter != null) ? painter : UI_BACKGROUND_PAINTER_DEFAULT;
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        painter.paint((Graphics2D) g.create(0, 0, width, height), this, width, height);

        super.paintComponent(g);
    }
}

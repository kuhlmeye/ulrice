package net.ulrice.ui.accordionpanel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Painter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class AccordionSeparatorPanel extends JButton implements MouseListener {

    private static final long serialVersionUID = 7528047902693951355L;

    public static final String UI_NAME = AccordionSeparatorPanel.class.getSimpleName();
    public static final String UI_FONT = UI_NAME + ".font";
    public static final String UI_FOREGROUND = UI_NAME + ".foreground";
    public static final String UI_CONTENT_MARGINS = UI_NAME + ".contentMargins";
    public static final String UI_BACKGROUND_PAINTER = UI_NAME + ".backgroundPainter";

    private static final Insets UI_CONTENT_MARGINS_DEFAULT = new Insets(0, 0, 0, 0);
    private static final AccordionSeparatorPanelBackgroundPainter UI_BACKGROUND_PAINTER_DEFAULT = new AccordionSeparatorPanelBackgroundPainter();

    private boolean mouseOver = false;
    private boolean pressed = false;

    private Painter<AccordionSeparatorPanel> painter;

    public AccordionSeparatorPanel(String title, Color backgroundColor) {
        super();

        setOpaque(false);
        setBackground(backgroundColor);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(this);

        setBorderPainted(false);
        setContentAreaFilled(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setText(title);
    }

    @Override
    public void updateUI() {
        super.updateUI();

        Font font = UIManager.getFont(UI_FONT);

        setFont((font != null) ? font : UIManager.getFont("Button.font"));

        Color foreground = UIManager.getColor(UI_FOREGROUND);

        setForeground((foreground != null) ? foreground : UIManager.getColor("Button.foreground"));

        Insets insets = UIManager.getInsets(UI_CONTENT_MARGINS);

        // setMargin((insets != null) ? insets : UIManager.getInsets("Button.contentMargins"));
        if (insets == null) {
            insets = UI_CONTENT_MARGINS_DEFAULT;
        }

        setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));

        @SuppressWarnings("unchecked")
        Painter<AccordionSeparatorPanel> painter = (Painter<AccordionSeparatorPanel>) UIManager.get(UI_BACKGROUND_PAINTER);

        this.painter = (painter != null) ? painter : UI_BACKGROUND_PAINTER_DEFAULT;
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();

        painter.paint(g2, this, width, height);

        super.paintComponent(g2);
    }

    public void setOpened(boolean b) {
        if (b) {
            setIcon(new ImageIcon(AccordionSeparatorPanel.class.getResource("opened.gif")));
        }
        else {
            setIcon(new ImageIcon(AccordionSeparatorPanel.class.getResource("closed.gif")));
        }
    }

    public void setTitle(String title) {
        setText(title);
    }

    public String getTitle() {
        return getText();
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // intentionally left blank
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        repaint();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        repaint();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        mouseOver = true;
        repaint();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        mouseOver = false;
        repaint();
    }

}

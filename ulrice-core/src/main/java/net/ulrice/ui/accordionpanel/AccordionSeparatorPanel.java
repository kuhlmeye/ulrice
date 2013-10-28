package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ulrice.util.Colors;
import net.ulrice.util.Gradients;

public class AccordionSeparatorPanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = 7528047902693951355L;

    private static final Color FOCUS_COLOR = new Color(0x73a4d1);

    private String actionCommand;
    private final JLabel foldLabel;
    private final JLabel titleLabel;

    private boolean mouseOver = false;
    private boolean pressed = false;

    public AccordionSeparatorPanel(String title, Color backgroundColor) {
        super(new BorderLayout());

        setOpaque(false);
        setBackground(backgroundColor);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(this);

        foldLabel = new JLabel();

        add(foldLabel, BorderLayout.WEST);
        titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        add(titleLabel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume();

                fireActionPerformed(e.getWhen(), e.getModifiers());
            }

        });
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed(long when, int modifiers) {
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand(), when, modifiers);
            }
            ((ActionListener) listeners[i + 1]).actionPerformed(e);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        Color background = getBackground();

        // draw background
        if ((mouseOver) || (pressed)) {
            background = Colors.blend(background, FOCUS_COLOR, 0.25);
        }

        if ((pressed) && (mouseOver)) {
            g2.setPaint(Gradients.curved(background, 0, 2, 0, height + 2, 0.5, 0.66));
        }
        else {
            g2.setPaint(Gradients.curved(background, 0, 0, 0, height, 0.5, 0.66));
        }

        g2.fillRect(0, 0, width, height);

        if ((pressed) && (mouseOver)) {
            g2.setPaint(Gradients.shadow(new Color(0x00000000, true), 0, 0, 0, height, 0.25, 4));
            g2.fillRect(0, 0, width, height);
        }

        // draw inner border
        if ((pressed) && (mouseOver)) {
            g2.setPaint(Gradients.shadow(Colors.transparent(Color.WHITE, 0.66), 0, 1, 0, height - 3, 0.125, 16));
        }
        else {
            g2.setPaint(Colors.transparent(Color.WHITE, 0.33));
        }

        g2.drawRect(0, 0, width - 1, height - 2);

        // draw outer border
        g2.setPaint(Colors.transparent(Color.BLACK, 0.75));
        g2.drawLine(0, height - 1, width, height - 1);

        if (pressed) {
            g.translate(0, 1);
        }

        super.paintComponent(g);
    }

    public void setOpened(boolean b) {
        if (b) {
            foldLabel.setIcon(new ImageIcon(AccordionSeparatorPanel.class.getResource("opened.gif")));
        }
        else {
            foldLabel.setIcon(new ImageIcon(AccordionSeparatorPanel.class.getResource("closed.gif")));
        }
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public String getTitle() {
        return titleLabel.getText();
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

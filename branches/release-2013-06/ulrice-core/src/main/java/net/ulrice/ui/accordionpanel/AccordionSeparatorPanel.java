package net.ulrice.ui.accordionpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ulrice.util.Colors;

public class AccordionSeparatorPanel extends JPanel {

    private static final long serialVersionUID = 7528047902693951355L;

    private String actionCommand;
    private final JLabel foldLabel;
    private final JLabel titleLabel;

    public AccordionSeparatorPanel(String title, Color backgroundColor) {
        super(new BorderLayout());

        setOpaque(false);
        setBackground(backgroundColor);

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
        int height = getHeight();

        Graphics2D g2 = (Graphics2D) g;
        LinearGradientPaint paint =
                new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(0, height), new float[] { 0.0f, 1.0f / height, 0.5f, 1.0f - (2.0f / height), 1.0f },
                    new Color[] {
                        Colors.brighter(getBackground(), 0.01), getBackground(), getBackground(), Colors.darker(getBackground(), 0.1), Colors.darker(getBackground(), 0.5) });

        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), height);

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
}

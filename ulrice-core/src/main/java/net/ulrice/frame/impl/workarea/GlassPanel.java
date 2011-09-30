package net.ulrice.frame.impl.workarea;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.ulrice.ui.UI;

public class GlassPanel extends JLayeredPane implements AWTEventListener, MouseWheelListener {

    /** Default generated serial version uid. */
    private static final long serialVersionUID = 382926138153043911L;
       
    private boolean blocked = false;
   

    private final JPanel overlayPanel = new JPanel() {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setOpaque(false);
            g.setColor(UIManager.getColor(UI.GLASSPANEL_COLOR));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
        }
    };

    private JComponent view;
    
    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
        if (blocked) {
            add(overlayPanel, new Integer(1));
            overlayPanel.requestFocus();
            overlayPanel.addMouseWheelListener(this);
            Toolkit.getDefaultToolkit().addAWTEventListener(this, 0xFFF);
        }
        else {
            remove(overlayPanel);
            overlayPanel.removeMouseWheelListener(this);
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        }

        doLayout();
        revalidate();
        repaint();
    }

    public void addModuleView(JComponent view) {
        this.view = view;
        add(view, new Integer(0));
    }


    @Override
    public void eventDispatched(AWTEvent event) {
        if (event.getSource() instanceof Component) {
            Component c = ((Component) event.getSource());

            while (c.getParent() != null && c != this) {
                c = c.getParent();
            }

            if ((event instanceof KeyEvent) && (event.getSource() instanceof Component)) {
                if (c == this) {
                    ((KeyEvent) event).consume();
                }
            }
            else if ((event instanceof MouseEvent) && (event.getSource() instanceof Component)) {
                if (c == this) {
                    ((MouseEvent) event).consume();
                }
            }
            else if ((event instanceof MouseWheelEvent) && (event.getSource() instanceof Component)) {
                if (c == this) {
                    ((MouseWheelEvent) event).consume();
                }
            }
            
        }

    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        view.setBounds(0, 0, width, height);
        overlayPanel.setBounds(0, 0, width, height);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        e.consume();
    }
}

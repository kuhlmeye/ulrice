package net.ulrice.frame.impl.workarea;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.UIManager;

import net.ulrice.ui.UI;

public class GlassPanel extends JPanel implements AWTEventListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 382926138153043911L;

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(GlassPanel.class.getName());

	private boolean blocked = false;

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
		if (blocked) {
			Toolkit.getDefaultToolkit().addAWTEventListener(this, 0xFFF);
		} else {
			Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		}

		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (blocked) {
			setOpaque(false);
			super.paint(g);
			g.setColor(UIManager.getColor(UI.GLASSPANEL_COLOR));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.dispose();
			//TODO Wie wird der Cursor zurückgesetzt?
			//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			super.paint(g);
		}
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
					LOG.fine("Key event consumed by blocked workarea");
				}
			} else if ((event instanceof MouseEvent) && (event.getSource() instanceof Component)) {
				if (c == this) {
					((MouseEvent) event).consume();
				}
			}
		}

	}
}

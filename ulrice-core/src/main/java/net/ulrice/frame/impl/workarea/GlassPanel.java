package net.ulrice.frame.impl.workarea;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.ulrice.ui.UI;

public class GlassPanel extends JLayeredPane implements AWTEventListener, MouseWheelListener {

	private static final long serialVersionUID = 382926138153043911L;
	private boolean blocked = false;
	private OverlayPanel overlayPanel = new OverlayPanel();
	private JComponent view;

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		if (this.blocked != blocked) {
			if (blocked) {
				add(overlayPanel, Integer.valueOf(1));
				overlayPanel.requestFocus();
				overlayPanel.addMouseWheelListener(this);
				Toolkit.getDefaultToolkit().addAWTEventListener(this, 0xFFF);
			} else {
				remove(overlayPanel);
				overlayPanel.removeMouseWheelListener(this);
				Toolkit.getDefaultToolkit().removeAWTEventListener(this);
			}
		}
		this.blocked = blocked;

		doLayout();
		revalidate();
		repaint();
	}

	public void addModuleView(JComponent view) {
		this.view = view;
		add(view, Integer.valueOf(0));
		repaint();
	}

	@Override
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		if (event.getSource() instanceof Component) {
			Component c = ((Component) event.getSource());

			while (c.getParent() != null && c != this) {
				c = c.getParent();
			}

			if ((event instanceof KeyEvent) && (event.getSource() instanceof Component) && c == this) {
				((KeyEvent) event).consume();

			} else if ((event instanceof MouseEvent) && (event.getSource() instanceof Component) && c == this) {
				((MouseEvent) event).consume();

			} else if ((event instanceof MouseWheelEvent) && (event.getSource() instanceof Component) && c == this) {
				((MouseWheelEvent) event).consume();
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

	private class OverlayPanel extends JPanel {

		private final Color defaultColor = new Color(100, 100, 100, 100);

		public OverlayPanel() {
			setOpaque(false);
			Color color = UIManager.getColor(UI.GLASSPANEL_COLOR);
			setBackground(color == null ? defaultColor : color);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}

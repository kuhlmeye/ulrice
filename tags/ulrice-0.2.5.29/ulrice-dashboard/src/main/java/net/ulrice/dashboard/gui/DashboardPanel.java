/**
 * 
 */
package net.ulrice.dashboard.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

/**
 * This class provides an <code>JPanel</code> with an rounded border
 * 
 * @author ekaveto
 * 
 */
public class DashboardPanel extends JPanel {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = 1L;

	/** The border */
	private RoundedBorder roundedBorder;
	/** Roundness of the panel */
	private int arc;
	/** Color 1 for gradient */
	private Color gradientColor1;
	/** Color 2 for gradient */
	private Color gradientColor2;

	/**
	 * Construct <code>JPanel</code> with an default border
	 */
	public DashboardPanel() {
		this(20, 1.8f, new Color(100, 100, 100), Color.WHITE, Color.LIGHT_GRAY);
	}

	/**
	 * Construct a <code>JPanel</code> with an default border
	 * 
	 * @param arc
	 *            Roundness of the border
	 */
	public DashboardPanel(int arc) {
		this(arc, 1.8f, new Color(100, 100, 100), Color.LIGHT_GRAY, Color.GRAY);
	}

	/**
	 * Construct a panel
	 * 
	 * @param arc
	 *            The roundness of the edges
	 * @param strokeWidth
	 *            Width of the outline
	 */
	public DashboardPanel(int arc, float strokeWidth) {
		this(arc, strokeWidth, new Color(100, 100, 100), Color.LIGHT_GRAY,
				Color.GRAY);
	}

	/**
	 * Construct a panel
	 * 
	 * @param arc
	 *            The roundness of the edges
	 * @param strokeWidth
	 *            Width of the outline
	 * @param color
	 *            The Color
	 * @param gradientColor1
	 *            Color 1 for gradient
	 * @param gradientColor2
	 *            Color 2 for gradient
	 */
	public DashboardPanel(int arc, float strokeWidth, Color color,
			Color gradientColor1, Color gradientColor2) {
		super();
		this.arc = arc;
		this.gradientColor1 = gradientColor1;
		this.gradientColor2 = gradientColor2;

		roundedBorder = new RoundedBorder(arc, strokeWidth, color);
		setBorder(roundedBorder);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		Point pt1 = new Point(0, 0);
		Point pt2 = new Point(0, getHeight());

		g2.setPaint(new GradientPaint(pt1, gradientColor1, pt2, gradientColor2));
		g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 2,
				getHeight() - 2, arc, arc));

		super.paintComponent(g);

	}

}

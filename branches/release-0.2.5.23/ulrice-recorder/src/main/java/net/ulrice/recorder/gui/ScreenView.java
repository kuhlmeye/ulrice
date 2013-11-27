package net.ulrice.recorder.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class ScreenView extends JComponent {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Rectangle clipRect = new Rectangle();
	private Point startPoint = new Point();

	public ScreenView() {
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				startPoint = e.getPoint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Point endPoint = e.getPoint();

				double scaleX = 1.0/getWidth() * (double)image.getWidth();
				double scaleY = 1.0/getHeight() * (double)image.getHeight();
				
				clipRect.x = (int)(Math.max(0, Math.min(startPoint.x, endPoint.x)) * scaleX);
				clipRect.y = (int)(Math.max(0, Math.min(startPoint.y, endPoint.y)) * scaleY);
				clipRect.width = (int)(Math.min(getWidth(), Math.abs(startPoint.x - endPoint.x)) * scaleX);
				clipRect.height = (int)(Math.min(getWidth(), Math.abs(startPoint.y - endPoint.y)) * scaleY);
				
				repaint();
			}
			
		});
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public Rectangle getClipRect() {
		return clipRect;
	}
	
	public void setClipRect(Rectangle clipRect) {
		this.clipRect = clipRect;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		
		if(clipRect != null && clipRect.width > 0 && clipRect.height > 0) {
			double scaleX = 1.0/getWidth() * (double)image.getWidth();
			double scaleY = 1.0/getHeight() * (double)image.getHeight();
			
			
			g2.setColor(Color.red);
			g2.drawRect((int)(clipRect.x / scaleX), (int)(clipRect.y / scaleY), (int)(clipRect.width / scaleX), (int)(clipRect.height / scaleY));
		}
	}
}

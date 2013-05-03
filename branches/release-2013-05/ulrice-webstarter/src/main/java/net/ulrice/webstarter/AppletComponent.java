package net.ulrice.webstarter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;

/**
 * Ulrice ui component for the applet..
 * 
 * @author christof
 */
public class AppletComponent extends Panel {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 7441995340585975668L;

	private Image dbImage;

	private Insets insets = new Insets(2, 2, 2, 2);

	private int globalProgress = 0;
	private int taskProgress = 0;

	private String applString = "";
	private String globalString = "";
	
	private String taskString = "";

	private String errorMessage;

	public void paint2(Graphics g) {

		Dimension size = getSize();

		g.setColor(Color.white);
		g.fillRect(0, 0, size.width, size.height);

		int globalBarPixelWidth = (size.width * globalProgress) / 100;
		int taskBarPixelWidth = (size.width * taskProgress) / 100;

		
		g.setColor(Color.green.brighter());
		g.fillRect(insets.left, insets.top + 17, size.width - insets.left - insets.right, 15);
		g.fillRect(insets.left, insets.top + 34, size.width - insets.left - insets.right, 15);
		
		if(errorMessage == null || "".equals(errorMessage.trim())) {
			g.setColor(Color.green.darker());
		} else {
			globalBarPixelWidth = size.width;
			taskBarPixelWidth = size.width;
			taskString = errorMessage;
			g.setColor(Color.red.darker());
		}
		g.fillRect(insets.left + size.width / 2 - globalBarPixelWidth / 2, insets.top + 17, globalBarPixelWidth, 15);
		g.fillRect(insets.left + size.width / 2 - taskBarPixelWidth / 2, insets.top + 34, taskBarPixelWidth, 15);

		g.setColor(Color.black);
		g.drawRect(insets.left, insets.top + 17, size.width - insets.left - insets.right, 15);
		g.drawRect(insets.left, insets.top + 34, size.width - insets.left - insets.right, 15);

		g.setColor(Color.black);

		FontMetrics fm = g.getFontMetrics(g.getFont());
		int applStringWidth = fm.stringWidth(applString);
		int globalStringWidth = fm.stringWidth(globalString);
		int taskStringWidth = fm.stringWidth(taskString);

		g.drawString(applString, (size.width - applStringWidth) / 2, 15);
		g.drawString(globalString, (size.width - globalStringWidth) / 2, 32);
		g.drawString(taskString, (size.width - taskStringWidth) / 2, 47);
	}

	@Override
	public void update(Graphics g) {

		if (dbImage == null) {
			dbImage = createImage(this.getSize().width, this.getSize().height);
		}
		
		Graphics imgGraphics = dbImage.getGraphics();
		paint2(imgGraphics);
		imgGraphics.dispose();

		g.drawImage(dbImage, 0, 0, this);

	}

	public void setGlobalProgress(int globalProgress) {
		this.globalProgress = globalProgress;
	}

	public void setTaskProgress(int taskProgress) {
		this.taskProgress = taskProgress;
	}

	public void setGlobalString(String globalString) {
		this.globalString = globalString;
	}

	public void setTaskString(String taskString) {
		this.taskString = taskString;
	}

	public void setApplString(String applString) {
		this.applString = applString;
	}

	public void setError(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}

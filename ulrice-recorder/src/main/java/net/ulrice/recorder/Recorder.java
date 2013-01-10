package net.ulrice.recorder;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import net.ulrice.Ulrice;

public class Recorder implements AWTEventListener {
	
	private String title = "Recording";
	private String description;
	private Component component;
	private LinkedList<RecordedScreen> screens;
	private File outputDirectory;

	public Recorder(Component component, File outputDirectory) {
		this.component = component;
		this.outputDirectory = outputDirectory;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void start() {
		screens = new LinkedList<RecordedScreen>();
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
	}

	public void stop() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}
	
	public void setTitleToLastScreen(String title) {
		if(screens.size() > 0) {
			screens.get(screens.size() - 1).setTitle(title);
		}
	}
	
	public void setDescriptionToLastScreen(String description) {
		if(screens.size() > 0) {
			screens.get(screens.size() - 1).setTitle(description);
		}
	}

	public void save() throws IOException {
		PrintWriter pw = new PrintWriter(new File(outputDirectory, title + ".xml"));
		pw.println("<RecordedScreens>");
		pw.println(String.format("  <Title>%s</Title>", title));
		pw.println(String.format("  <Description>%s</Description>", description));
		pw.println("  <Screens>");
		try {
			for(int i = 0; i < screens.size(); i++) {
				RecordedScreen screen = screens.get(i);
				File screenshot = new File(outputDirectory, "screen-" + i + ".jpg");
				ImageIO.write(screen.getFullImage(), "JPG", screenshot);
				pw.println("    <Screen>");
				pw.println(String.format("      <ScreenTitle>%s</ScreenTitle>", screen.getTitle()));
				pw.println(String.format("      <ScreenDescription>%s</ScreenDescription>", screen.getDescription()));
				pw.println(String.format("      <Screenshot>%s</Screenshot>", screenshot.getName()));
				pw.println("    </Screen>");
			}
			pw.println("  </Screens>");
			pw.println("</RecordedScreens>");
		} finally {
			pw.close();
		}		
	}
	
	@Override
	public void eventDispatched(AWTEvent e) {
		KeyEvent keyEvent = (KeyEvent) e;
		
		if (keyEvent.getID() == KeyEvent.KEY_RELEASED && KeyEvent.VK_F12 == keyEvent.getKeyCode()) {
			try {
				screens.add(recordScreen());
			} catch (AWTException e1) {
				Ulrice.getMessageHandler().handleException(e1);
			} catch (IOException e1) {
				Ulrice.getMessageHandler().handleException(e1);
			}
		}
	}

	public RecordedScreen recordScreen() throws AWTException, IOException {
		System.out.println("Record!");
		BufferedImage screenCapture = getScreenShotFromComponent(component);

		Image cursor = ImageIO.read(Recorder.class.getResourceAsStream("pointer.png"));
		Point mousePointer = MouseInfo.getPointerInfo().getLocation(); 
		SwingUtilities.convertPointFromScreen(mousePointer, component);
		
		int width = screenCapture.getWidth();
		int height = screenCapture.getHeight();
		int scaledWidth = 320;
		int scaledHeight = 200;

		double scaleW = 100.0d / width * scaledWidth;
		double scaleH = 100.0d / height * scaledHeight;

		Graphics2D graphics2D = screenCapture.createGraphics();
		graphics2D.drawImage(cursor, mousePointer.x, mousePointer.y, 25, 39, null);

		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(Math.min(scaleW, scaleH), Math.min(scaleW, scaleH));
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		scaledImage = scaleOp.filter(screenCapture, scaledImage);

		RecordedScreen screen = new RecordedScreen();
		screen.setFullImage(screenCapture);
		screen.setSmallImage(scaledImage);

		return screen;
	}

	private BufferedImage getScreenShotFromComponent(Component component) {		
		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
		component.paint(image.getGraphics());
		return image;
	}
}

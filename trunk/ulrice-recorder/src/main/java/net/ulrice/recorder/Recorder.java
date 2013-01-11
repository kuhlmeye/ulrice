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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.ulrice.Ulrice;

public class Recorder implements AWTEventListener {

	private Component component;
	private LinkedList<RecordedScreen> screens;
	private File outputDirectory;
	private RecorderDialog dialog = new RecorderDialog();
	private String title;

	public Recorder(Component component, File outputDirectory) {
		this.component = component;
		this.outputDirectory = outputDirectory;

		dialog.getStopButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}

		});

		dialog.getRecordButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}

		});

		dialog.getSaveButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stop();
					save();
				} catch (IOException e1) {
					Ulrice.getMessageHandler().handleException(e1);
				}
			}

		});

		dialog.pack();
		dialog.setVisible(true);

	}

	public void start() {
		screens = new LinkedList<RecordedScreen>();

		dialog.getRecordButton().setSelected(true);

		dialog.getRecordButton().setSelected(false);
		dialog.getScreenTitle().setEnabled(false);
		dialog.getScreenDescription().setEnabled(false);
		
		dialog.getTitleField().setText("");
		dialog.getCategoryField().setText("");
		dialog.getDescriptionArea().setText("");
		dialog.getScreenshot().setIcon(null);
		dialog.getScreenTitle().setText("");
		dialog.getScreenDescription().setText("");
		
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
	}

	public void stop() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		updateScreenTexts();
		
		title = dialog.getTitleField().getText().replace(" ", "_");
		
		
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitleToLastScreen(String title) {
		if (screens.size() > 0) {
			screens.get(screens.size() - 1).setTitle(title);
		}
	}

	public void setDescriptionToLastScreen(String description) {
		if (screens.size() > 0) {
			screens.get(screens.size() - 1).setTitle(description);
		}
	}

	public void save() throws IOException {
		if(screens.isEmpty()) {
			return;
		}
		
		List<File> files = new ArrayList<File>();
		File xmlFile = new File(outputDirectory, getTitle() + ".xml");
		files.add(xmlFile);
		
		PrintWriter pw = new PrintWriter(xmlFile);				
		pw.println("<RecordedScreens>");
		pw.println(String.format("  <Title>%s</Title>", dialog.getTitleField().getText()));
		pw.println(String.format("  <Category>%s</Category>", dialog.getCategoryField().getText()));
		pw.println(String.format("  <Description>%s</Description>", dialog.getDescriptionArea().getText()));
		pw.println("  <Screens>");
		try {
			for (int i = 0; i < screens.size(); i++) {
				RecordedScreen screen = screens.get(i);
				File screenshot = new File(outputDirectory, getTitle() + "-screen-" + i + ".jpg");
				files.add(screenshot);
				File screenshot_small = new File(outputDirectory, getTitle() + "-screen-" + i + "_small.jpg");
				files.add(screenshot_small);
				
				ImageIO.write(screen.getFullImage(), "JPG", screenshot);
				ImageIO.write(screen.getSmallImage(), "JPG", screenshot_small);
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
		
		compressOutput(files);
	}
	
	public void compressOutput(List<File> files) {
	    
	    byte[] buf = new byte[1024];
	    
	    try {
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(getTitle() + ".xml.zip"));
	    
	        for (int i=0; i<files.size(); i++) {
	            FileInputStream in = new FileInputStream(files.get(i));
	            out.putNextEntry(new ZipEntry(files.get(i).getName()));
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.closeEntry();
	            in.close();
	        }
	    
	        out.close();
	        
	        for(File file : files) {
	        	file.delete();
	        }
	    } catch (IOException e) {
	    	Ulrice.getMessageHandler().handleException(e);
	    }
	}

	@Override
	public void eventDispatched(AWTEvent e) {
		KeyEvent keyEvent = (KeyEvent) e;

		if (keyEvent.getID() == KeyEvent.KEY_RELEASED && KeyEvent.VK_F12 == keyEvent.getKeyCode()) {
			try {
				
				
				updateScreenTexts();

				RecordedScreen screen = recordScreen();
				screens.add(screen);
				dialog.getScreenTitle().setEnabled(true);
				dialog.getScreenDescription().setEnabled(true);

				dialog.getScreenshot().setIcon(new ImageIcon(screen.getSmallImage()));
				dialog.getScreenTitle().setText("");
				dialog.getScreenDescription().setText("");
				dialog.getContentPane().invalidate();
				dialog.getContentPane().repaint();

			} catch (AWTException e1) {
				Ulrice.getMessageHandler().handleException(e1);
			} catch (IOException e1) {
				Ulrice.getMessageHandler().handleException(e1);
			}
		}
	}

	private void updateScreenTexts() {
		if (screens.size() > 0) {
			RecordedScreen lastScreen = screens.get(screens.size() - 1);
			lastScreen.setTitle(dialog.getScreenTitle().getText());
			lastScreen.setDescription(dialog.getScreenDescription().getText());
		}
	}

	public RecordedScreen recordScreen() throws AWTException, IOException {
		BufferedImage screenCapture = getScreenShotFromComponent(component);

		Image cursor = ImageIO.read(Recorder.class.getResourceAsStream("pointer.png"));
		Point mousePointer = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePointer, component);

		Graphics2D graphics2D = screenCapture.createGraphics();
		graphics2D.drawImage(cursor, mousePointer.x, mousePointer.y, 25, 39, null);

		int width = screenCapture.getWidth();
		int height = screenCapture.getHeight();
		int scaledWidth = 320;
		int scaledHeight = 200;

		double scaleW = 1.0d / width * scaledWidth;
		double scaleH = 1.0d / height * scaledHeight;
		double scale = Math.min(scaleW, scaleH);

		BufferedImage image = screenCapture;
		Image smallerImg = image.getScaledInstance(Double.valueOf(width * scale).intValue(), Double.valueOf(height * scale).intValue(), Image.SCALE_SMOOTH);

		RecordedScreen screen = new RecordedScreen();
		screen.setFullImage(screenCapture);
		screen.setSmallImage(toBufferedImage(smallerImg, BufferedImage.TYPE_BYTE_INDEXED));

		return screen;
	}

	public BufferedImage toBufferedImage(final Image image, final int type) {
		if (image instanceof BufferedImage)
			return (BufferedImage) image;
		if (image instanceof VolatileImage)
			return ((VolatileImage) image).getSnapshot();
		loadImage(image);
		final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		final Graphics2D g2 = buffImg.createGraphics();
		g2.drawImage(image, null, null);
		g2.dispose();
		return buffImg;
	}

	private void loadImage(final Image image) {
		class StatusObserver implements ImageObserver {
			boolean imageLoaded = false;

			public boolean imageUpdate(final Image img, final int infoflags, final int x, final int y, final int width, final int height) {
				if (infoflags == ALLBITS) {
					synchronized (this) {
						imageLoaded = true;
						notify();
					}
					return true;
				}
				return false;
			}
		}
		final StatusObserver imageStatus = new StatusObserver();
		synchronized (imageStatus) {
			if (image.getWidth(imageStatus) == -1 || image.getHeight(imageStatus) == -1) {
				while (!imageStatus.imageLoaded) {
					try {
						imageStatus.wait();
					} catch (InterruptedException ex) {
					}
				}
			}
		}
	}

	private BufferedImage getScreenShotFromComponent(Component component) {
		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
		component.paint(image.getGraphics());
		return image;
	}
}

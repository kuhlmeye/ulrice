package net.ulrice.recorder.api;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;

class ImageUtils {
	
	static BufferedImage clipImage(BufferedImage image, int clipX, int clipY, int clipW, int clipH) {
		if(clipW == 0|| clipH == 0) {
			return image;
		}
		
		Rectangle clipRect = new Rectangle(clipX, clipY, clipW, clipH);
		clipRect = clipRect.intersection(new Rectangle(image.getWidth(), image.getHeight()));
		
		BufferedImage clippedImg = image.getSubimage(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
		
		return clippedImg;		
	}

	static BufferedImage scaleImage(BufferedImage screenCapture, int scaledWidth, int scaledHeight) {

		int width = screenCapture.getWidth();
		int height = screenCapture.getHeight();

		double scaleW = 1.0d / width * scaledWidth;
		double scaleH = 1.0d / height * scaledHeight;
		double scale = Math.min(scaleW, scaleH);

		BufferedImage image = screenCapture;
		Image smallerImg = image.getScaledInstance(Double.valueOf(width * scale).intValue(), Double.valueOf(height * scale).intValue(), Image.SCALE_SMOOTH);
		return toBufferedImage(smallerImg, BufferedImage.TYPE_BYTE_INDEXED);
	}
	
	static BufferedImage toBufferedImage(final Image image, final int type) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		if (image instanceof VolatileImage) {
			return ((VolatileImage) image).getSnapshot();
		}
		loadImage(image);
		final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		final Graphics2D g2 = buffImg.createGraphics();
		g2.drawImage(image, null, null);
		g2.dispose();
		return buffImg;
	}

	private static void loadImage(final Image image) {
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
}

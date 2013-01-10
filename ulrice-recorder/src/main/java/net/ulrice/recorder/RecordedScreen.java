package net.ulrice.recorder;

import java.awt.image.BufferedImage;

public class RecordedScreen {

	private BufferedImage fullImage;
	private BufferedImage smallImage;
	private String title;
	private String description;

	public BufferedImage getFullImage() {
		return fullImage;
	}

	public void setFullImage(BufferedImage fullImage) {
		this.fullImage = fullImage;
	}

	public BufferedImage getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(BufferedImage smallImage) {
		this.smallImage = smallImage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "RecordedScreen [title=" + title + ", description="
				+ description + "]";
	}
}

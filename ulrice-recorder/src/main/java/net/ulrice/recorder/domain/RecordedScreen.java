package net.ulrice.recorder.domain;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordedScreen other = (RecordedScreen) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RecordedScreen [title=" + title + ", description=" + description + "]";
	}
}

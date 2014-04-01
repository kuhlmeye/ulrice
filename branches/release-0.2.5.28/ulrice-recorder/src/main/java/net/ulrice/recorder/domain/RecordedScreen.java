package net.ulrice.recorder.domain;

import java.awt.image.BufferedImage;

public class RecordedScreen {

	private BufferedImage fullImage;
	private String title;
	private String description;
	private int clipX;
	private int clipY;
	private int clipW;
	private int clipH;

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

	public BufferedImage getFullImage() {
		return fullImage;
	}

	public void setFullImage(BufferedImage fullImage) {
		this.fullImage = fullImage;
	}
	
	
	
	public int getClipX() {
		return clipX;
	}

	public void setClipX(int clipX) {
		this.clipX = clipX;
	}

	public int getClipY() {
		return clipY;
	}

	public void setClipY(int clipY) {
		this.clipY = clipY;
	}

	public int getClipW() {
		return clipW;
	}

	public void setClipW(int clipW) {
		this.clipW = clipW;
	}

	public int getClipH() {
		return clipH;
	}

	public void setClipH(int clipH) {
		this.clipH = clipH;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + clipH;
		result = prime * result + clipW;
		result = prime * result + clipX;
		result = prime * result + clipY;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((fullImage == null) ? 0 : fullImage.hashCode());
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
		if (clipH != other.clipH)
			return false;
		if (clipW != other.clipW)
			return false;
		if (clipX != other.clipX)
			return false;
		if (clipY != other.clipY)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (fullImage == null) {
			if (other.fullImage != null)
				return false;
		} else if (!fullImage.equals(other.fullImage))
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

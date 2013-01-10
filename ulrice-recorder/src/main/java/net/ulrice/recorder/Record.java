package net.ulrice.recorder;

import java.util.List;

public class Record {

	private String title;
	private String description;
	private List<RecordedScreen> screens;

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

	public List<RecordedScreen> getScreens() {
		return screens;
	}

	public void setScreens(List<RecordedScreen> screens) {
		this.screens = screens;
	}

	@Override
	public String toString() {
		return "Record [title=" + title + ", description=" + description
				+ ", screens=" + screens + "]";
	}
}
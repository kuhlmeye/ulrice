package net.ulrice.webstarter;

import net.ulrice.webstarter.tasks.DownloadFile;

public class ProvidedJRE {

	private String os;
	private String baseUrl;
	private String filename;
	private DownloadFile downloadTask;

	public String getFilename() {
		return filename;
	}

	public String getOs() {
		return os;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setOs(String os) {
		this.os = os;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public DownloadFile getDownloadTask() {
		return downloadTask;
	}
	
	public void setDownloadTask(DownloadFile downloadTask) {
		this.downloadTask = downloadTask;
	}
}

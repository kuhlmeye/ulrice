package net.ulrice.webstarter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class storing the global settings. 
 * 
 * @author christof
 */
public class ProcessContext {

	/** Stores the cookie-values. */
	private Map<String, String> cookieMap = new HashMap<String, String>();
	
	/** Stores all classpath entries. */
	private List<String> classPath= new LinkedList<String>();
	
	private String userId;
	
	private String password;
	
	public Map<String, String> getCookieMap() {
		return cookieMap;
	}

	public String getCookieAsString() {
		StringBuffer buffer = new StringBuffer();
		for(Entry<String, String> entry : cookieMap.entrySet()) {
			buffer.append(entry.getKey());
			if(entry.getValue() != null) {
				buffer.append("=").append(entry.getValue());
			}
			buffer.append(";");
		}
		return buffer.toString();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getClassPath() {
		return classPath;
	}

	public void setClassPath(List<String> classPath) {
		this.classPath = classPath;
	}

	public void setCookieMap(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}
}

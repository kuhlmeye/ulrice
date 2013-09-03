package net.ulrice.webstarter;

/**
 * Class representing a placeholder (simple key/value-class)
 * 
 * @author christof
 */
public class Placeholder {

	private String key;

	private String value;
	
	public Placeholder(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}

package net.ulrice.security;

import java.util.HashMap;
import java.util.Map;

/**
 * Default authorization object.
 */
public class Authorization {

	
	private Map<String, String> authMap = new HashMap<String, String>();
	
	/** The type of the authorization. */
	private String type;

	/** The value of the authorizatiton */
	private String value;

	/**
	 * Creates a new authorization object
	 * 
	 * @param type
	 *            The type of the authorization.
	 * @param value
	 *            The value of the authorization.
	 */
	public Authorization(String type, String value) {
		addAuthorization(type, value);
	}

	/**
	 * Adds an authorization.
	 */
	public void addAuthorization(String type, String value) {
		authMap.put(type, value);		
	}
	
	
	/**
	 * Returns and authorization
	 */
	public String getAuthorization(String type) {
		return authMap.get(type);
	}
}

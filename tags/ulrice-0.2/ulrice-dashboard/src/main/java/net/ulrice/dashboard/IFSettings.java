/**
 * 
 */
package net.ulrice.dashboard;

/**
 * Provides methods to store and retrieve key-value pairs.
 * 
 * @author ekaveto
 *
 */
public interface IFSettings {
	
	/**
	 * Add a property to the Settings.
	 * 
	 * @param key - String identifier.
	 * @param value - String value.
	 */
	void putProperty(String key, String value);
	
	/**
	 * Returns the value of the corresponding key.
	 * 
	 * @param key - String identifier.
	 * @return value - String value.
	 */
	String getValue(String key);
	
	/**
	 * Removes a key/entry from the Settings.
	 * 
	 * @param key - String identifier.
	 */
	void removeKey(String key);
	
	/**
	 * Save all properties in a e.g. file/db.
	 */
	void saveProperties();
	
	/**
	 * Save the property.
	 * 
	 * @param key - String identifier.
	 * @param value - String value.
	 */
	void saveProperties(String key, String value);
	
	/**
	 * Load all properties from a e.g. file/db.
	 */
	void loadProperties();

}

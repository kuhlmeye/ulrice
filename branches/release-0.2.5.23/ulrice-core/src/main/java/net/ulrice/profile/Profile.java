package net.ulrice.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A profile that represents a set of data used for initializing objects (e.g. controllers)
 * This object is just the container for the data with different access methods
 * 
 * @author christof
 */
public class Profile {

	private String groupId;
	private String profileId;
	private boolean isReadOnly;
	

	private Map<String, String> valueMap = new HashMap<String, String>();


	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getProfileId() {
		return profileId;
	}
	
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	
	public void putBoolean(String key, boolean value) {
		valueMap.put(key, String.valueOf(value));
	}

	public boolean getBoolean(String key) {
		return Boolean.valueOf(valueMap.get(key));
	}
	
	public void putString(String key, String value) {
		valueMap.put(key, value);
	}

	public String getString(String key) {
		return valueMap.get(key);
	}
	
	public void putInt(String key, int value) {
		valueMap.put(key, String.valueOf(value));
	}

	public int getInt(String key) {
		return Integer.valueOf(valueMap.get(key));
	}
	
	public Set<String> getKeys() {
		return valueMap.keySet();
	}
}

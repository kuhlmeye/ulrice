package net.ulrice.webstarter;

import java.util.HashMap;
import java.util.Map;

public class ProcessContext {

	private Map<String, Object> context = new HashMap<String, Object>();

	public static final String COOKIE = "COOKIE_CONTENT";

	public static final String USERID = "USERID";

	public static final String PASSWORD = "PASSWORD";

	public static final String LOGIN_TYPE = "LOGINTYPE";


	public static final String EVENT_LISTENERS = "EVENT_LISTENERS";

	public static final String CLASSPATH = "CLASSPATH";

	public String getValueAsString(String key) {
		return getValueAsString(key, null);
	}
	
	public String getValueAsString(String key, String defaultValue) {
		Object value = getValue(key, defaultValue);
		return value == null ? null : value.toString();
	}
	
	public Object getValue(String key) {
		return getValue(key, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key, T defaultValue) {
		if (context.containsKey(key)) {
			return (T) context.get(key);
		} else {
			return defaultValue;
		}
	}

	public void setValue(String key, Object value) {
		context.put(key, value);
	}
}

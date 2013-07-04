package net.ulrice.appprefs;


public interface IFAppPrefs {
	
    String getConfiguration(Object requestingObject, String key, String defaultValue);
    void putConfiguration(Object requestingObject, String key, String value);
    void removeConfiguration(Object requestingObject, String key);
	void shutdown();
}

package net.ulrice.appprefs;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class DefaultAppPrefs implements IFAppPrefs {

	private Preferences preferences = Preferences.userNodeForPackage(DefaultAppPrefs.class);

    /**
     * Returns a configuration value.
     * 
     * @param requestingObject The class name of the requestingObject is used as key prefix.
     * @param key The parameter key.
     * @param defaultValue The default value returned, if the value was not found.
     * @return The configuration parameter value.
     */
    @Override
    public String getConfiguration(Object requestingObject, String key, String defaultValue) {
        String cfgKey = buildConfigurationKey(requestingObject, key);

        try {
			if(preferences.nodeExists(cfgKey)) {
				return preferences.get(cfgKey, defaultValue);
			}
		} catch (BackingStoreException e) {
			// Nothing to do..
		}
        return defaultValue;
    }
    
    @Override
    public void putConfiguration(Object requestingObject, String key, String value) {
        String cfgKey = buildConfigurationKey(requestingObject, key);
        preferences.put(cfgKey, value);
    }

    @Override
    public void removeConfiguration(Object requestingObject, String key) {
    	String cfgKey = buildConfigurationKey(requestingObject, key);
    	preferences.remove(cfgKey);
    }
    
	private String buildConfigurationKey(Object requestingObject, String key) {
		StringBuilder builder = new StringBuilder();
        builder.append(requestingObject.getClass().getName());
        builder.append('.');
        builder.append(key);
		return builder.toString();
	}

	@Override
	public void shutdown() {
	}
}

package net.ulrice.profile.persister;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.ulrice.Ulrice;
import net.ulrice.profile.Profile;

/**
 * Default profile persister using the java preferences object as persister
 * 
 * @author christof
 */
public class DefaultProfilePersister implements ProfilePersister {

	private Preferences profileRoot;

	public DefaultProfilePersister() {
		String pathName = Ulrice.getAppPrefs().getConfiguration(this, "rootPath", "ulrice.profiles");

		Preferences userRoot = Preferences.userRoot();
		profileRoot = userRoot.node(pathName);
	}

	@Override
	public Profile createProfile(String groupId, String profileId, Profile profile) {
		return saveProfile(groupId, profileId, profile);
	}

	@Override
	public Profile updateProfile(String groupId, String profileId, Profile profile) {
		return saveProfile(groupId, profileId, profile);
	}

	private Profile saveProfile(String groupId, String profileId, Profile profile) {

		Preferences node = profileRoot.node(groupId + "." + profileId);

		Set<String> keySet = profile.getKeys();
		if (keySet != null) {
			for (String key : keySet) {
				String value = profile.getString(key);
				node.put(key, value);
			}
		}

		return profile;
	}

	@Override
	public Profile loadProfile(String groupId, String profileId) {

		String nodeName = groupId + "." + profileId;
		try {
			if (profileRoot.nodeExists(nodeName)) {
				Preferences profileNode = profileRoot.node(nodeName);
				Profile profile = new Profile();

				for (String name : profileNode.childrenNames()) {
					profile.putString(name, profileNode.get(name, ""));
				}

				return profile;
			}
		} catch (BackingStoreException e) {
			Ulrice.getMessageHandler().handleException(e);
		}

		return null;
	}

	@Override
	public List<Profile> loadProfiles(String groupId) {

		try {
			if (profileRoot.nodeExists(groupId)) {
				Preferences moduleNode = profileRoot.node(groupId);

				List<Profile> result = new ArrayList<Profile>();
				for (String name : moduleNode.childrenNames()) {
					result.add(loadProfile(groupId, name));
				}
				return result;
			}
		} catch (BackingStoreException e) {
			Ulrice.getMessageHandler().handleException(e);
		}

		return null;
	}

	@Override
	public void deleteProfile(String groupId, String profileId) {
		String nodeName = groupId + "." + profileId;
		try {
			if (profileRoot.nodeExists(nodeName)) {
				Preferences profileNode = profileRoot.node(nodeName);
				profileNode.removeNode();
			}
		} catch (BackingStoreException e) {
			Ulrice.getMessageHandler().handleException(e);
		}
	}

	@Override
	public List<String> loadProfileIds(String groupId) {

		try {
			if (profileRoot.nodeExists(groupId)) {
				Preferences moduleNode = profileRoot.node(groupId);

				List<String> result = new ArrayList<String>();
				for (String name : moduleNode.childrenNames()) {
					result.add(name);
				}
				return result;
			}
		} catch (BackingStoreException e) {
			Ulrice.getMessageHandler().handleException(e);
		}
		return null;
	}

}

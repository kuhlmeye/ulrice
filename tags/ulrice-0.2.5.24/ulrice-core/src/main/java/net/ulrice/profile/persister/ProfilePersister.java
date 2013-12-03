package net.ulrice.profile.persister;

import java.util.List;

import net.ulrice.profile.Profile;

/**
 * Interface for classes that persist profiles.
 * 
 * @author christof
 */
public interface ProfilePersister {
	
	/**
	 * Load all profiles for a group.
	 */
	List<Profile> loadProfiles(String groupId);

	/**
	 * Load all profile ids for a given group.
	 */
	List<String> loadProfileIds(String groupId);

	/**
	 * Load a profile by a group and profile id.
	 */
	Profile loadProfile(String groupId, String profileId);

	/**
	 * Create a new profile by group and profile id
	 */
	Profile createProfile(String groupId, String profileId, Profile profile);

	/**
	 * Update a profile identified by group and profile id.
	 */
	Profile updateProfile(String groupId, String profileId, Profile profile);

	/**
	 * Delete a profile identified by group and profile id.
	 */
	void deleteProfile(String groupId, String profileId);
}

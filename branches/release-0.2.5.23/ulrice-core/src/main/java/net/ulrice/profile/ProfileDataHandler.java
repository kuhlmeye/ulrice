package net.ulrice.profile;

import net.ulrice.module.IFController;

/**
 * Data handler able to read / write the values from / to an object.
 * 
 * @author christof
 */
public interface ProfileDataHandler<T extends IFController> {

	/**
	 * Returns the id of this profile handler used for grouping the profiles. 
	 */
	String getProfileGroupId();
	
	/**
	 * Writes the data from the source object into the profile 
	 */
	Profile updateProfileData(Profile profile, T source);
	
	/**
	 * Reads the data from the profile and sets them into the source object.
	 */
	void readFromProfileData(Profile profile, T source);	
}

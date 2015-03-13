package net.ulrice.profile;

import net.ulrice.module.IFController;

/**
 * A specific profile bound to a module. 
 * 
 * @author christof
 */
public class ProfiledModule {

	private ProfilableModule<? extends IFController> profileHandlerModule;
	private String profileId;

	/**
	 * Creates a specific profile which is bound to a module.
	 */
	public ProfiledModule(ProfilableModule<? extends IFController> profileHandlerModule, String profileId) {
		this.profileHandlerModule = profileHandlerModule;
		this.profileId = profileId;
	}

	/**
	 * Returns the handler needed for loading, saving the profile and also the link to the module.
	 */
	public ProfilableModule<? extends IFController> getProfileHandlerModule() {
		return profileHandlerModule;
	}

	/**
	 * Returns the id of the profile.
	 */
	public String getProfileId() {
		return profileId;
	}
}

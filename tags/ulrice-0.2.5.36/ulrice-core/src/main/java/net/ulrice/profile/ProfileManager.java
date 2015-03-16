package net.ulrice.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.ModuleParam;
import net.ulrice.module.exception.ModuleInstantiationException;
import net.ulrice.profile.persister.ProfilePersister;

/**
 * Class maintaining the profiles.
 * 
 * @author christof
 */
public class ProfileManager {

	private static final String SETTINGS_GROUP = "#settings";
	private ProfilePersister profilePersister;
	private Map<String, Map<String, Profile>> nonPersistentProfiles = new HashMap<String, Map<String, Profile>>();
	private EventListenerList listenerList = new EventListenerList();
	
	/**
	 * Called during the initialization of ulrice. Instantiated with a persister
	 */
	public ProfileManager(ProfilePersister profilePersister) {
		this.profilePersister = profilePersister;		
	}
	
	/**
	 * Open a profiled module. The module is opened using the module manager and the profile is loaded during initialization.
	 * 
	 * @param profiledModule The profiled module.
	 * @param controllerProviderCallback The user callback
	 */
	public void openProfiledModule(final ProfiledModule profiledModule, final ControllerProviderCallback controllerProviderCallback) {
		Ulrice.getModuleManager().openModule(profiledModule.getProfileHandlerModule().getUniqueId(), new ControllerProviderCallback() {
			
			@Override
			public void onFailure(ModuleInstantiationException exc) {
				controllerProviderCallback.onFailure(exc);
			}

			@Override
			public void onControllerInitialization(IFController controller, Map paramMap) {
				loadProfileInternal(profiledModule.getProfileHandlerModule(), profiledModule.getProfileId(), controller);
			}

			@SuppressWarnings("unchecked")
			private <T extends IFController> void loadProfileInternal(ProfilableModule<T> handler, String profileId, IFController source) {
				loadProfile(handler, profileId, (T)source);
			}
			
			@Override
			public void onControllerReady(IFController controller) {
				controllerProviderCallback.onControllerReady(controller);
			}
		});
	}
	
	/**
	 * Adds a profile to the profile manager which is not saved/loaded by the persistent manager.
	 * This kind of profiles could be added statically by a module for example (e.g. for create a 
	 * quick access to important data)
	 */
	public void addNonPersistentProfile(Profile profile) {
		profile.setReadOnly(true);
		Map<String, Profile> profiles = nonPersistentProfiles.get(profile.getGroupId());
		if(profiles == null) {
			profiles = new HashMap<String, Profile>();
			nonPersistentProfiles.put(profile.getGroupId(), profiles);
		}
		profiles.put(profile.getProfileId(), profile);				
	}
	
	/**
	 * Returns all existing profiles for a module 
	 */
	public List<ProfiledModule> loadProfiledModules(ProfilableModule<? extends IFController> module) {
		List<ProfiledModule> result = new ArrayList<ProfiledModule>();

		List<String> loadProfileIds = profilePersister.loadProfileIds(module.getUniqueId());
		if(loadProfileIds != null) {
			for(String profileId : loadProfileIds) {
				ProfiledModule profiledModule = new ProfiledModule(module, profileId);
				result.add(profiledModule);
			}						
		}
		Map<String, Profile> profiles = nonPersistentProfiles.get(module.getProfileGroupId());
		if(profiles != null) {
			for(Profile profile : profiles.values()) {
				ProfiledModule profiledModule = new ProfiledModule(module, profile.getProfileId());
				result.add(profiledModule);
			}
		}
		
		return result;
	}
	
	/**
	 * Load the settings of a controller
	 */
	public Profile loadSettings(IFController controller) {
		Profile profile = profilePersister.loadProfile(SETTINGS_GROUP, controller.getClass().getName());
		profile.setGroupId(SETTINGS_GROUP);
		return profile;
	}
	
	/**
	 * Save the settings of a controller
	 */
	public void saveSettings(IFController controller, Profile profile) {
		profile.setGroupId(SETTINGS_GROUP);
		profilePersister.createProfile(SETTINGS_GROUP, controller.getClass().getName(), profile);
	}

	/**
	 * Load a profile by profile id. 
	 */
	public <T extends IFController> void loadProfile(ProfilableModule<T> handler, String profileId, T source) {
		Profile profile = profilePersister.loadProfile(handler.getProfileGroupId(), profileId);
		if(profile == null) {
			Map<String, Profile> profileMap = nonPersistentProfiles.get(handler.getProfileGroupId());
			if(profileMap != null) {
				profile = profileMap.get(profileId);
			}
		}
		if(profile != null) {
			profile.setReadOnly(false);
			handler.readFromProfileData(profile, source);
		}
	}
	
	/**
	 * Create a new profile. 
	 */
	public <T extends IFController> void createProfile(ProfileDataHandler<T> handler, String profileId, T source) {
		Profile profile = handler.updateProfileData(new Profile(), source);
		profile.setReadOnly(false);
		if(profile != null) {
			profilePersister.createProfile(handler.getProfileGroupId(), profileId, profile);
			ProfileListener[] listeners = listenerList.getListeners(ProfileListener.class);
			if(listeners != null) {
				for(ProfileListener listener : listeners) {
					listener.profileCreated(profile);
				}
			}
		}
	}
	
	/**
	 * Update an existing profile. 
	 */
	public <T extends IFController> void updateProfile(ProfileDataHandler<T> handler, String profileId, T source) {
		Profile profile = handler.updateProfileData(new Profile(), source);
		profile.setReadOnly(false);
		if(profile != null) {
			profilePersister.updateProfile(handler.getProfileGroupId(), profileId, profile);
			ProfileListener[] listeners = listenerList.getListeners(ProfileListener.class);
			if(listeners != null) {
				for(ProfileListener listener : listeners) {
					listener.profileUpdated(profile);
				}
			}
		}
	}	
	
	/**
	 * Delete an existing profile.
	 */
	public <T extends IFController> void removeProfile(ProfileDataHandler<T> handler, String profileId) {
		Profile profile = profilePersister.loadProfile(handler.getProfileGroupId(), profileId);
		if(profile != null) {
			profilePersister.deleteProfile(handler.getProfileGroupId(), profileId);
			ProfileListener[] listeners = listenerList.getListeners(ProfileListener.class);
			if(listeners != null) {
				for(ProfileListener listener : listeners) {
					listener.profileDeleted(profile);
				}
			}
		}
	}
	
	/**
	 * Adds a profile listener to the list of listeners
	 */
	void addProfileListener(ProfileListener listener) {
		listenerList.add(ProfileListener.class, listener);
	}

	/**
	 * Removes a profile listener from the list of listeners
	 * @param listener
	 */
	void removeProfileListener(ProfileListener listener) {
		listenerList.remove(ProfileListener.class, listener);
	}
}
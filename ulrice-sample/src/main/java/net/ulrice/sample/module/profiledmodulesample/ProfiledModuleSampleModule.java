package net.ulrice.sample.module.profiledmodulesample;

import net.ulrice.Ulrice;
import net.ulrice.module.ModuleType;
import net.ulrice.module.impl.AuthReflectionModule;
import net.ulrice.profile.Profile;
import net.ulrice.profile.ProfilableModule;

public class ProfiledModuleSampleModule extends AuthReflectionModule implements ProfilableModule<ProfiledModuleSampleController> {

	public ProfiledModuleSampleModule() {
		super("Profiled Module", ModuleType.NormalModule, ProfiledModuleSampleController.class.getName(), null);
		
		Profile profile = new Profile();
		profile.setGroupId(getProfileGroupId());
		profile.setProfileId("Test1");
		profile.setReadOnly(true);
		profile.putString("Text", "Testvalue 1");
		Ulrice.getProfileManager().addNonPersistentProfile(profile);
		
		profile = new Profile();
		profile.setGroupId(getProfileGroupId());
		profile.setProfileId("Test2");
		profile.setReadOnly(true);
		profile.putString("Text", "Testvalue 2");
		Ulrice.getProfileManager().addNonPersistentProfile(profile);
	}

	@Override
	public String getProfileGroupId() {
		return getUniqueId();
	}
	
	@Override
	public Profile updateProfileData(Profile profile, ProfiledModuleSampleController source) {
		return profile;
	}

	@Override
	public void readFromProfileData(Profile profile, ProfiledModuleSampleController source) {
		source.setTextValue(profile.getString("Text"));
	}
}

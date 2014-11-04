package net.ulrice.sample.module.masktextfield;

import net.ulrice.module.ModuleType;
import net.ulrice.module.impl.AuthReflectionModule;

public class MaskTextFieldModule extends AuthReflectionModule {

	public MaskTextFieldModule() {
		super("Mask Textfield", ModuleType.NormalModule, MaskTextFieldController.class.getName(), null);
	}
}

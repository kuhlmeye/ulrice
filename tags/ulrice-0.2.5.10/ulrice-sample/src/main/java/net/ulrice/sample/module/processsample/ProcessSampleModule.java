package net.ulrice.sample.module.processsample;

import net.ulrice.module.ModuleType;
import net.ulrice.module.impl.AuthReflectionModule;

public class ProcessSampleModule extends AuthReflectionModule {

	public ProcessSampleModule() {
		super("Background Processes", ModuleType.NormalModule, ProcessSampleController.class.getName(), null);
	}
}

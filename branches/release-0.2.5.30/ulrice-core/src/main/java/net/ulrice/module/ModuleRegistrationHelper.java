package net.ulrice.module;

import javax.swing.KeyStroke;

public class ModuleRegistrationHelper {

	private IFModuleStructureManager structureManager;
	private IFModuleManager moduleManager;

	public ModuleRegistrationHelper(IFModuleStructureManager structureManager, IFModuleManager moduleManager) {
		this.structureManager = structureManager;
		this.moduleManager = moduleManager;
	}
	
	public void addTopModule(IFModule<?> module) {
		structureManager.addModule(module);
		moduleManager.registerModule(module);
	}
	
	public void addGroup(IFModuleGroup group) {
		structureManager.addModuleGroup(group);
	}
	
	public void addModuleToGroup(IFModuleGroup group, IFModule<?> module) {
		group.addModule(module);
		moduleManager.registerModule(module);
	}
	
	public void addGroupToGroup(IFModuleGroup parent, IFModuleGroup child) {
		parent.addModuleGroup(child);
	}
	
	public void addHiddenModule(IFModule<?> module) {
		moduleManager.registerModule(module);
	}
	
	public void addModuleHotkey(IFModule<?> module, KeyStroke keyStroke) {
		moduleManager.registerHotkey(keyStroke, module.getUniqueId());
	}
	
	public void addModuleFavorite(IFModule<?> module) {
		structureManager.addModuleFavorite(module);
	}
	
	public IFModuleManager getModuleManager() {
		return moduleManager;
	}
	
	public IFModuleStructureManager getStructureManager() {
		return structureManager;
	}
}

package net.ulrice.module;

import net.ulrice.module.event.IFModuleStructureEventListener;

public interface IFModuleStructureManager {
	
	/**
	 * Returns the root group of this module structure.
	 * 
	 * @return The root group.
	 */
	public IFModuleGroup getRootGroup();
	
	/**
	 * Add a group of modules to this module group.
	 * 
	 * @param group The group of modules that should be added to this module.
	 */
	public void addModuleGroup(IFModuleGroup group);

	/**
	 * Add a module to this module group
	 * 
	 * @param module The module that should be added to this group.
	 */
	public void addModule(IFModule module);
	
	/**
	 * Adds a class listening to the module structure events.
	 * 
	 * @param listener The listener that should be added.
	 */
	public void addModuleStructureEventListener(IFModuleStructureEventListener listener);
	
	/**
	 * Removes a class listening to the module structure events.
	 * 
	 * @param listener The listener that should be removed.
	 */
	public void removeModuleStructureEventListener(IFModuleStructureEventListener listener);
}

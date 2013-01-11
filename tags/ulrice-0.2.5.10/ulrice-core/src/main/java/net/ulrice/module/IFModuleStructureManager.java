package net.ulrice.module;

import net.ulrice.module.event.IFModuleStructureEventListener;

public interface IFModuleStructureManager {
	
	/**
	 * Returns the root group of this module structure.
	 * 
	 * @return The root group.
	 */
	IFModuleGroup getRootGroup();
	
	/**
	 * Add a group of modules to this module group.
	 * 
	 * @param group The group of modules that should be added to this module.
	 */
	void addModuleGroup(IFModuleGroup group);

    /**
     * Add a module to the root group
     * 
     * @param module The module that should be added to this group.
     */
    void addModule(IFModule module);
	
	/**
	 * Adds a class listening to the module structure events.
	 * 
	 * @param listener The listener that should be added.
	 */
	void addModuleStructureEventListener(IFModuleStructureEventListener listener);
	
	/**
	 * Removes a class listening to the module structure events.
	 * 
	 * @param listener The listener that should be removed.
	 */
	void removeModuleStructureEventListener(IFModuleStructureEventListener listener);

    void fireModuleStructureChanged();
}

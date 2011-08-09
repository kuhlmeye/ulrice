package net.ulrice.module;

import java.util.List;

/**
 * Interface for a group of modules.
 * 
 * @author ckuhlmeyer
 */
public interface IFModuleGroup {

	/**
	 * Returns the list of members of this group.
	 * 
	 * @return The list of modules.
	 */
	List<IFModule> getModules();
	
	/**
	 * Returns the list of groups contained in this group.
	 * 
	 * @return The list of module groups.
	 */
	List<IFModuleGroup> getModuleGroups();
	
	void addModuleGroup(IFModuleGroup group);
	
	void addModule(IFModule module);
}

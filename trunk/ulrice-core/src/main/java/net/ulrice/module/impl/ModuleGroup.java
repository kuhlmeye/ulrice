package net.ulrice.module.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleGroup;

public class ModuleGroup implements IFModuleGroup {

	/** The list of contained module groups. */
	private List<IFModuleGroup> moduleGroups = new LinkedList<IFModuleGroup>();

	/** The list of modules directly assigned to the root group. */
	private List<IFModule> modules = new LinkedList<IFModule>();

    private int orderNumber;

    private String id;

    private String translatedModuleName;

    private String icon;

    private boolean enabled;

	
	
	public ModuleGroup(int orderNumber, String id, String translatedModuleName, String icon, boolean enabled) {
	    this.orderNumber = orderNumber;
	    this.id = id;
	    this.translatedModuleName = translatedModuleName;
	    this.icon = icon;
	    this.enabled = enabled;
    }

    /**
	 * @see net.ulrice.module.IFModuleGroup#getModules()
	 */
	public List<IFModule> getModules() {
		return Collections.unmodifiableList(modules);
	}

	/**
	 * @see net.ulrice.module.IFModuleGroup#getModuleGroups()
	 */
	public List<IFModuleGroup> getModuleGroups() {
		return Collections.unmodifiableList(moduleGroups);
	}

	/**
	 * Add a group of modules to this module group.
	 * 
	 * @param group
	 *            The group of modules that should be added to this module.
	 */
    @Override
	public void addModuleGroup(IFModuleGroup group) {
		moduleGroups.add(group);
	}

	/**
	 * Add a module to this module group
	 * 
	 * @param module
	 *            The module that should be added to this group.
	 */
	public void addModule(IFModule module) {
		modules.add(module);
	}

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getTitle() {
        return translatedModuleName;
    }
}
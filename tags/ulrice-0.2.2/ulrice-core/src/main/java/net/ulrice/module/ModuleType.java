package net.ulrice.module;

/**
 * Type of the module.
 * 
 * @author ckuhlmeyer
 */
public enum ModuleType {

	/** A module for which only one instance could exist. */
	SingleModule,
	
	/** A module for which multiple instances could exist. */
	NormalModule;	
}

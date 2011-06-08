package net.ulrice.module;

/**
 * Interface for a class that renders the module title.
 * 
 * @author ckuhlmeyer
 */
public interface IFModuleTitleRenderer {

	/**
	 * Enumeration defining the default usages of the module titles.
	 * 
	 * @author ckuhlmeyer
	 */
	enum Usage {
		/** Usage in the changeover dialog. */
		ChangeOverDialog,

		/** Usage in the module tree. */
		ModuleTree,
		
		/** The tabbed workarea. */
		TabbedWorkarea,
		
		/** The default module title. */
		Default;		
	}

	/**
	 * Returns the title of the module depending on the usage.
	 * 
	 * @param usage The situation in which the module title is used.
	 * @return The module title as string.
	 */
	String getModuleTitle(Usage usage);
}

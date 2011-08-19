package net.ulrice.module;

/**
 * Interface for a class that provides the module title.
 * 
 * @author ckuhlmeyer
 */
public interface IFModuleTitleProvider {

	/**
	 * Enumeration defining the default usages of the module titles.
	 */
	enum Usage {
		/** Usage in the changeover dialog. */
		ChangeOverDialog,

		/** Usage in the module tree. */
		ModuleTree,
		
		/** The tabbed workarea. */
		TabbedWorkarea,
		
		DetailedTitle,
		
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

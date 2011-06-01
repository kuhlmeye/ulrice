package net.ulrice.module.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleGroup;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.IFModuleTitleRenderer.Usage;
import net.ulrice.module.ModuleType;
import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.event.IFModuleStructureEventListener;
import net.ulrice.module.exception.ModuleInstanciationException;

/**
 * The default module manager.
 * 
 * @author ckuhlmeyer
 */
public class ModuleManager implements IFModuleManager, IFModuleStructureManager {

	/** The logger used by the module manager. */
	private static final Logger LOG = Logger.getLogger(ModuleManager.class.getName());

	/** The mapping between module and module id. */
	private Map<String, IFModule> moduleMap = new HashMap<String, IFModule>();

	/** The set of all controller instances. */
	private List<IFController> activeInstances = new LinkedList<IFController>();

	/** The set of the instanciated single modules. */
	private Map<IFModule, IFController> singleModules = new HashMap<IFModule, IFController>();

	/** The controller of the current active module. */
	private IFController activeController = null;

	/** List of all listeners listening on module manager events. */
	private EventListenerList listenerList = new EventListenerList();

	/** The root group of this module manager. */
	private UlriceRootModule rootGroup = new UlriceRootModule();

	/**
	 * @see net.ulrice.module.IFModuleManager#openModule(net.ulrice.module.IFModule)
	 */
	public IFController openModule(String moduleId) throws ModuleInstanciationException {

		IFModule module = moduleMap.get(moduleId);
		if (module == null) {
			throw new ModuleInstanciationException("Module with (" + moduleId + ") could not be found.", null);
		}

		boolean isSingleModule = ModuleType.SingleModule.equals(module.getModuleInstanceType());

		IFController ctrlInstance = null;
		if (isSingleModule && singleModules.containsKey(module)) {
			// If it's a single module and if it's already open than return the
			// instance.
			ctrlInstance = singleModules.get(module);
			activateModule(ctrlInstance);

		} else {
			// Create a new instance
			ctrlInstance = module.instanciateModule();
			ctrlInstance.preCreationEvent(module);

			if (!Ulrice.getSecurityManager().allowOpenModule(module, ctrlInstance)) {
				LOG.info("Module [Id: " + module.getUniqueId() + ", Name: " + module.getModuleTitle(Usage.Default)
						+ "] will not be created. Not authorized by ulrice security manager.");
				throw new ModuleInstanciationException("Not allowed by security manager", null);
			}

			activeInstances.add(0, ctrlInstance);

			if (isSingleModule) {
				singleModules.put(module, ctrlInstance);
			}

			// Inform event listeners.
			IFModuleEventListener[] listeners = listenerList.getListeners(IFModuleEventListener.class);
			if (listeners != null) {
				for (IFModuleEventListener listener : listeners) {
					listener.openModule(ctrlInstance);
				}
			}

			ctrlInstance.postCreationEvent(module);

			// Activate the controller.
			activateModule(ctrlInstance);
		}
		// Return the instance of the listener
		return ctrlInstance;
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#activateModule(net.ulrice.module.IFController)
	 */
	public void activateModule(IFController controller) {
		IFModuleEventListener[] listeners = listenerList.getListeners(IFModuleEventListener.class);

		// Inform event listeners.
		if (activeController != null) {
			if (listeners != null) {
				for (IFModuleEventListener listener : listeners) {
					listener.deactivateModule(activeController);
				}
			}
		}

		this.activeController = controller;

		// Move the controller to the top of the list.
		activeInstances.remove(controller);
		activeInstances.add(0, controller);

		// Inform event listeners.
		if (listeners != null) {
			for (IFModuleEventListener listener : listeners) {
				listener.activateModule(activeController);
			}
		}
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#closeAllModules()
	 */
	@Override
	public void closeAllModules() {
		List<IFController> activeModules = getActiveModules();

		if (activeModules != null) {
			for (IFController controller : activeModules) {
				closeModule(controller);
			}
		}
	}

	@Override
	public void closeOtherModules(IFController controller) {
		List<IFController> activeModules = getActiveModules();

		if (activeModules != null) {
			for (IFController closeController : activeModules) {
				if (!closeController.equals(controller)) {
					closeModule(closeController);
				}
			}
		}
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#closeModule(net.ulrice.module.IFController)
	 */
	public void closeModule(IFController controller) {
		if (controller == null) {
			return;
		}

		boolean isSingleModule = ModuleType.SingleModule.equals(controller.getModule().getModuleInstanceType());

		if (isSingleModule) {
			singleModules.remove(controller.getModel());
		}
		activeInstances.remove(controller);

		// Inform event listeners.
		IFModuleEventListener[] listeners = listenerList.getListeners(IFModuleEventListener.class);
		if (listeners != null) {
			for (IFModuleEventListener listener : listeners) {
				listener.closeModule(controller);
			}
		}

		activeController = null;
		if (activeInstances.size() > 0) {
			activateModule(activeInstances.get(0));
		}

	}

	/**
	 * @see net.ulrice.module.IFModuleManager#getCurrentModule()
	 */
	public IFController getCurrentModule() {
		return activeController;
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#getActiveModules()
	 */
	public List<IFController> getActiveModules() {
		List<IFController> activeModules = new ArrayList<IFController>(activeInstances == null ? 0 : activeInstances.size());
		activeModules.addAll(activeInstances);
		return activeModules;
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#registerModule(net.ulrice.module.IFModule)
	 */
	public void registerModule(IFModule module) {
		if (module == null) {
			throw new IllegalArgumentException("Module must not be null.");
		}

		if (!Ulrice.getSecurityManager().allowRegisterModule(module)) {
			LOG.info("Module [Id: " + module.getUniqueId() + ", Name: " + module.getModuleTitle(Usage.Default)
					+ "] will not be registered. Not authorized by ulrice security manager.");
			return;
		}

		moduleMap.put(module.getUniqueId(), module);
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#unregisterModule(net.ulrice.module.IFModule)
	 */
	public void unregisterModule(IFModule module) {
		if (module == null) {
			throw new IllegalArgumentException("Module must not be null.");
		}
		moduleMap.remove(module.getUniqueId());
	}

	/**
	 * Add a group of modules to this module group.
	 * 
	 * @param group
	 *            The group of modules that should be added to this module.
	 */
	public void addModuleGroup(IFModuleGroup group) {
		rootGroup.addModuleGroup(group);
		fireModuleStructureChanged();
	}

	/**
	 * Add a module to this module group
	 * 
	 * @param module
	 *            The module that should be added to this group.
	 */
	public void addModule(IFModule module) {
		if (!Ulrice.getSecurityManager().allowRegisterModule(module)) {
			LOG.info("Module [Id: " + module.getUniqueId() + ", Name: " + module.getModuleTitle(Usage.Default)
					+ "] will not be added. Not authorized by ulrice security manager.");
			return;
		}

		rootGroup.addModule(module);
		fireModuleStructureChanged();
	}

	/**
	 * @return the rootGroup
	 */
	public IFModuleGroup getRootGroup() {
		return rootGroup;
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#addModuleEventListener(net.ulrice.module.event.IFModuleEventListener)
	 */
	public void addModuleEventListener(IFModuleEventListener listener) {
		listenerList.add(IFModuleEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.module.IFModuleManager#removeModuleEventListener(net.ulrice.module.event.IFModuleEventListener)
	 */
	public void removeModuleEventListener(IFModuleEventListener listener) {
		listenerList.remove(IFModuleEventListener.class, listener);
	}

	/**
	 * The class representing the root module.
	 * 
	 * @author ckuhlmeyer
	 */
	private class UlriceRootModule implements IFModuleGroup {

		/** The list of contained module groups. */
		private List<IFModuleGroup> moduleGroups = new LinkedList<IFModuleGroup>();

		/** The list of modules directly assigned to the root group. */
		private List<IFModule> modules = new LinkedList<IFModule>();

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
	}

	/**
	 * Inform the event listeners after the structure of the module tree has
	 * changed (e.g. adding/removing a module(group))
	 */
	private void fireModuleStructureChanged() {
		// Inform event listeners.
		IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
		if (listeners != null) {
			for (IFModuleStructureEventListener listener : listeners) {
				listener.moduleStructureChanged();
			}
		}
	}

	/**
	 * @see net.ulrice.module.IFModuleStructureManager#addModuleStructureEventListener(net.ulrice.module.event.IFModuleStructureEventListener)
	 */
	@Override
	public void addModuleStructureEventListener(IFModuleStructureEventListener listener) {
		listenerList.add(IFModuleStructureEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.module.IFModuleStructureManager#removeModuleStructureEventListener(net.ulrice.module.event.IFModuleStructureEventListener)
	 */
	@Override
	public void removeModuleStructureEventListener(IFModuleStructureEventListener listener) {
		listenerList.remove(IFModuleStructureEventListener.class, listener);
	}

	@Override
	public void fireControllerBlocked(IFController abstractController) {
		// Inform event listeners.
		IFModuleEventListener[] listeners = listenerList.getListeners(IFModuleEventListener.class);
		if (activeController != null) {
			if (listeners != null) {
				for (IFModuleEventListener listener : listeners) {
					listener.moduleBlocked(activeController);
				}
			}
		}
	}

	@Override
	public void fireControllerUnblocked(IFController abstractController) {
		// Inform event listeners.
		IFModuleEventListener[] listeners = listenerList.getListeners(IFModuleEventListener.class);
		if (activeController != null) {
			if (listeners != null) {
				for (IFModuleEventListener listener : listeners) {
					listener.moduleUnblocked(activeController);
				}
			}
		}
	}

}

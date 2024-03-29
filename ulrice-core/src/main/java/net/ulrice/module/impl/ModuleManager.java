package net.ulrice.module.impl;

import net.ulrice.ConfigurationListener;
import net.ulrice.Ulrice;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleGroup;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.ModuleParam;
import net.ulrice.module.ModuleType;
import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.event.IFModuleStructureEventListener;
import net.ulrice.module.exception.ModuleInstantiationException;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The default module manager.
 *
 * @author ckuhlmeyer
 */
public class ModuleManager implements IFModuleManager, IFModuleStructureManager, KeyEventDispatcher {

    /** The logger used by the module manager. */
    private static final Logger LOG = Logger.getLogger(ModuleManager.class.getName());

    /** The mapping between module and module id. */
    private final Map<String, IFModule<?>> moduleMap = new HashMap<String, IFModule<?>>();

    /** The set of all controller instances. */
    private final OpenControllerPool openControllers = new OpenControllerPool();

    /** List of all listeners listening on module manager events. */
    private final EventListenerList listenerList = new EventListenerList();

    /** The root group of this module manager. */
    private final UlriceRootModule rootGroup = new UlriceRootModule();

    private HashMap<KeyStroke, String> hotkeyModuleIdMap = new HashMap<KeyStroke, String>();
    private HashMap<KeyStroke, IFController> openHotkeyControllerMap = new HashMap<KeyStroke, IFController>();

    private final IdentityHashMap<IFController, IdentityHashMap<Object, Object>> blockers =
            new IdentityHashMap<IFController, IdentityHashMap<Object, Object>>();

	private List<String> favorites = new ArrayList<String>();

    public ModuleManager() {
    	Ulrice.addConfigurationListener(new ConfigurationListener() {

			@Override
			public void initializationFinished() {
				loadHotkeys();
				loadFavorites();
			}

		});
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public void openModule(final String moduleId, final ControllerProviderCallback callback, ModuleParam... params) {
        openModule(moduleId, null, callback, params);
    }

    @Override
    public void openModule(final String moduleId, final ControllerProviderCallback callback, final IFCloseCallback closeCallback, ModuleParam... params) {
        openModule(moduleId, null, callback, closeCallback, params);
    }

    @Override
    public void openModule(final String moduleId, final IFController parent, final ControllerProviderCallback callback, ModuleParam... params) {
        openModule(moduleId, parent, callback, null, params);
    }

    @Override
    public void openModule(final String moduleId, final IFController parent, final ControllerProviderCallback callback, final IFCloseCallback closeCallback, final ModuleParam... params) {
        final IFModule module = moduleMap.get(moduleId);

        final Map<String, ModuleParam> paramMap = new HashMap<>();
        if(params != null) {
            for(ModuleParam param : params) {
                paramMap.put(param.getKey(), param);
            }
        }

        if (module == null) {
            LOG.warning("Module with id (" + moduleId + ") could not be found.");
            callback.onFailure(new ModuleInstantiationException("Module with id (" + moduleId + ") could not be found.", null));
            return;
        }

        try {
            Ulrice.getMainFrame().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            final boolean isSingleModule = ModuleType.SingleModule.equals(module.getModuleInstanceType());

            if (isSingleModule && (openControllers.getControllers(module).size() > 0)) {
                // If it's a single module and if it's already open than return the instance.
                final IFController ctrlInstance = openControllers.getControllers(module).iterator().next();

                activateModule(ctrlInstance);

                if (callback != null) {
                    callback.onControllerReady(ctrlInstance);
                }

            }
            else {
                module.instantiateModule(new ControllerProviderCallback() {

                    @Override
                    public void onControllerReady(IFController controller) {
                        addBlocker(controller, ModuleManager.this);

                        if (!Ulrice.getSecurityManager().allowOpenModule(module, controller)) {
                            LOG.info("Module [Id: " + module.getUniqueId() + ", Name: "
                                + module.getModuleTitle(Usage.Default)
                                + "] will not be created. Not authorized by ulrice security manager.");
                            if (callback != null) {
                                callback.onFailure(new ModuleInstantiationException(
                                    "Not allowed by security manager", null));
                            }
                            return;
                        }

                        if (callback != null) {
                            callback.onControllerInitialization(controller, paramMap);
                        }

                        // Inform event listeners.
                        if (openControllers.getActive() != null) {
                            for (IFModuleEventListener listener : listenerList
                                .getListeners(IFModuleEventListener.class)) {
                                listener.deactivateModule(openControllers.getActive());
                            }
                        }

                        openControllers.addController(controller, parent, module, closeCallback);

                        for (IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
                            listener.openModule(controller);
                        }

                        controller.postCreate();

                        activateModule(controller);

                        if (callback != null) {
                            callback.onControllerReady(controller);
                        }

                        removeBlocker(controller, ModuleManager.this);
                    }

                    @Override
                    public void onFailure(ModuleInstantiationException exc) {
                        if (callback != null) {
                            callback.onFailure(exc);
                        }
                    }
                }, parent, paramMap);
            }
        }
        finally {
            Ulrice.getMainFrame().getFrame().setCursor(Cursor.getDefaultCursor());
        }
	}

    @Override
    public IFModule getModule(IFController controller) {
        return openControllers.getModule(controller);
    }

    @Override
    public IFModuleTitleProvider getTitleProvider(IFController controller) {
        if (controller == null) {
            return null;
        }
        final IFModuleTitleProvider fromController = controller.getTitleProvider();
        if (fromController != null) {
            return fromController;
        }
        return getModule(controller);
    }

    @Override
    public String getModuleTitle(String moduleId, Usage usage) {
    	final IFModule module = moduleMap.get(moduleId);
        return module.getModuleTitle(usage);
    }

    @Override
    public String getModuleTitle(IFController controller, Usage usage) {
        IFModuleTitleProvider titleProvider = getTitleProvider(controller);
        if (titleProvider != null) {
            return titleProvider.getModuleTitle(usage);
        }
        return null;
    }

    @Override
    public void activateModule(IFController controller) {
        IFModuleEventListener[] listeners = listenerList.getListeners(IFModuleEventListener.class);

        // Inform event listeners.
        if (openControllers.getActive() != null) {
            if (listeners != null) {
                for (IFModuleEventListener listener : listeners) {
                    listener.deactivateModule(openControllers.getActive());
                }
            }
        }

        openControllers.makeActive(controller);

        // Inform event listeners.
        if (listeners != null) {
            for (IFModuleEventListener listener : listeners) {
                listener.activateModule(openControllers.getActive());
            }
        }
    }

    @Override
    public void closeAllControllers(final Runnable afterClosingAllModules) {

        final IFController controller = getCurrentController();
        if (controller == null) {
            if (afterClosingAllModules != null) {
                afterClosingAllModules.run();
            }
            return;
        }

        closeController(controller, new IFCloseHandler() {

            @Override
            public void closeSuccess() {

                final Iterator<IFController> iterator = getActiveControllers().iterator();
                if (iterator.hasNext()) {
                    closeAllControllers(afterClosingAllModules);
                }
                else {
                    if (afterClosingAllModules != null) {
                        afterClosingAllModules.run();
                    }
                }
            }

            @Override
            public void closeFailure() {

            }
        });
    }

    @Override
    public void forceCloseAllControllers(final Runnable afterClosingAllModules) {
        final IFController controller = getCurrentController();
        if (controller == null) {
            if (afterClosingAllModules != null) {
                afterClosingAllModules.run();
            }
            return;
        }

        forceCloseController(controller, controller, null, new IFCloseHandler() {

            @Override
            public void closeSuccess() {

                final Iterator<IFController> iterator = getActiveControllers().iterator();
                if (iterator.hasNext()) {
                    forceCloseAllControllers(afterClosingAllModules);
                }
                else {
                    if (afterClosingAllModules != null) {
                        afterClosingAllModules.run();
                    }
                }
            }

            @Override
            public void closeFailure() {

            }
        });
    }

    @Override
    public void closeOtherControllers(final IFController controller, final IFCloseHandler closeHandler) {

        final List<IFController> notCurrentControllerList = getNotCurrentControllers(controller);
        if (notCurrentControllerList.isEmpty()) {

            if (closeHandler == null) {
                return;
            }

            closeHandler.closeSuccess();
        }

        final IFController firstNotCurrentController = notCurrentControllerList.get(0);
        activateModule(firstNotCurrentController);

        // TODO ehaasec handle 'controller does nothing'
        closeController(firstNotCurrentController, new IFCloseHandler() {

            @Override
            public void closeSuccess() {
                closeOtherControllers(controller, closeHandler);
            }

            @Override
            public void closeFailure() {
                if (closeHandler != null) {
                    closeHandler.closeFailure();
                }
            }
        });
    }

    @Override
    public void closeController(final IFController controller, final IFCloseHandler closeHandler) {
        closeController(controller, controller, null, closeHandler);
    }

    private void closeController(final IFController rootControllerToClose, final IFController controllerToClose,
        final IFController controllersParent, final IFCloseHandler closeHandler) {

        if (openControllers.getChildren(controllerToClose).size() == 0) {

            activateModule(controllerToClose);
            controllerToClose.onClose(new IFClosing() {

                @Override
                public void doClose() {

                    internalCloseController(controllerToClose);

                    if (controllersParent == null) {
                        if (closeHandler != null) {
                            closeHandler.closeSuccess();
                        }
                    }
                    else {
                        if (openControllers.getChildren(controllersParent).iterator().hasNext()) {
                            closeController(rootControllerToClose, openControllers.getChildren(controllersParent)
                                .iterator().next(), controllersParent, closeHandler);
                        }
                        else {
                            if (openControllers.getChildren(rootControllerToClose).iterator().hasNext()) {
                                closeController(rootControllerToClose,
                                    openControllers.getChildren(rootControllerToClose).iterator().next(),
                                    rootControllerToClose, closeHandler);
                            }
                            else {
                                closeController(rootControllerToClose, rootControllerToClose, null, closeHandler);
                            }
                        }
                    }
                }

                @Override
                public void doCancelClose() {

                    if (closeHandler != null) {
                        closeHandler.closeFailure();
                    }
                }
            });
        }
        else {
            closeController(rootControllerToClose, openControllers.getChildren(controllerToClose).iterator().next(),
                controllerToClose, closeHandler);
        }
    }

    private void forceCloseController(final IFController rootControllerToClose, final IFController controllerToClose,
                                      final IFController controllersParent, final IFCloseHandler closeHandler) {

        if (openControllers.getChildren(controllerToClose).size() == 0) {

            activateModule(controllerToClose);

            internalCloseController(controllerToClose);

            if (controllersParent == null) {
                if (closeHandler != null) {
                    closeHandler.closeSuccess();
                }
            } else {
                if (openControllers.getChildren(controllersParent).iterator().hasNext()) {
                    closeController(rootControllerToClose, openControllers.getChildren(controllersParent)
                            .iterator().next(), controllersParent, closeHandler);
                } else {
                    if (openControllers.getChildren(rootControllerToClose).iterator().hasNext()) {
                        closeController(rootControllerToClose,
                                openControllers.getChildren(rootControllerToClose).iterator().next(),
                                rootControllerToClose, closeHandler);
                    } else {
                        closeController(rootControllerToClose, rootControllerToClose, null, closeHandler);
                    }
                }
            }
        } else {
            forceCloseController(rootControllerToClose, openControllers.getChildren(controllerToClose).iterator().next(),
                    controllerToClose, closeHandler);
        }
    }

    private void internalCloseController(final IFController controller) {

        IFCloseCallback closeCallback = openControllers.getCloseCallback(controller);

        for (IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
            listener.closeController(controller);
        }

        openControllers.removeController(controller);

        if (openControllers.getActive() != null) {
            activateModule(openControllers.getActive());
        }

        if(closeCallback != null) {
            closeCallback.wasClosed(controller);
        }
    }

    /**
     * @return A list with all opened, but currently not selected controllers.
     */
    private List<IFController> getNotCurrentControllers(final IFController currentController) {

        final List<IFController> notCurrentControllers = new ArrayList<IFController>();

        for (IFController controller : getActiveControllers()) {
            if (controller != currentController) {
                notCurrentControllers.add(controller);
            }
        }

        return notCurrentControllers;
    }

    @Override
    public IFController getCurrentController() {
        return openControllers.getActive();
    }

    @Override
    public List<IFController> getActiveControllers() {
        return openControllers.getAll();
    }

    @Override
    public IFController getParentController(IFController controller) {
        return openControllers.getParent(controller);
    }

    @Override
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
    @Override
    public void unregisterModule(IFModule module) {
        if (module == null) {
            throw new IllegalArgumentException("Module must not be null.");
        }
        moduleMap.remove(module.getUniqueId());
    }

    /**
     * Add a group of modules to this module group.
     *
     * @param group The group of modules that should be added to this module.
     */
    @Override
    public void addModuleGroup(IFModuleGroup group) {
        rootGroup.addModuleGroup(group);
    }

    /**
     * Add a module to this module group
     *
     * @param module The module that should be added to this group.
     */
    @Override
    public void addModule(IFModule module) {
        rootGroup.addModule(module);
    }

    /**
     * @return the rootGroup
     */
    @Override
    public IFModuleGroup getRootGroup() {
        return rootGroup;
    }

    /**
     * @see net.ulrice.module.IFModuleManager#addModuleEventListener(net.ulrice.module.event.IFModuleEventListener)
     */
    @Override
    public void addModuleEventListener(IFModuleEventListener listener) {
        listenerList.add(IFModuleEventListener.class, listener);
    }

    /**
     * @see net.ulrice.module.IFModuleManager#removeModuleEventListener(net.ulrice.module.event.IFModuleEventListener)
     */
    @Override
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
        private final List<IFModuleGroup> moduleGroups = new LinkedList<IFModuleGroup>();

        /** The list of modules directly assigned to the root group. */
        private final List<IFModule<?>> modules = new LinkedList<IFModule<?>>();

        /**
         * @see net.ulrice.module.IFModuleGroup#getModules()
         */
        @Override
        public List<IFModule<?>> getModules() {
            return Collections.unmodifiableList(modules);
        }

        /**
         * @see net.ulrice.module.IFModuleGroup#getModuleGroups()
         */
        @Override
        public List<IFModuleGroup> getModuleGroups() {
            return Collections.unmodifiableList(moduleGroups);
        }

        /**
         * Add a group of modules to this module group.
         *
         * @param group The group of modules that should be added to this module.
         */
        @Override
        public void addModuleGroup(IFModuleGroup group) {
            moduleGroups.add(group);
        }

        /**
         * Add a module to this module group
         *
         * @param module The module that should be added to this group.
         */
        @Override
        public void addModule(IFModule module) {

            if (!Ulrice.getSecurityManager().allowRegisterModule(module)) {
                LOG.info("Module [Id: " + module.getUniqueId() + ", Name: " + module.getModuleTitle(Usage.Default)
                    + "] will not be added. Not authorized by ulrice security manager.");
                return;
            }

            modules.add(module);
        }

        @Override
        public String getTitle() {
            return "ROOT";
        }
    }

    /**
     * Inform the event listeners after the structure of the module tree has changed (e.g. adding/removing a
     * module(group))
     */
    @Override
    public void fireModuleStructureChanged() {
        // Inform event listeners.
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    uncheckedFireModuleStructureChanged();
                }
            });
        }
        else {
            uncheckedFireModuleStructureChanged();
        }
    }

    private void uncheckedFireModuleStructureChanged() {
        IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
        if (listeners != null) {
            for (IFModuleStructureEventListener listener : listeners) {
                listener.moduleStructureChanged();
            }
        }
    }

    protected void fireModuleFavoriteAdded(final IFModule<?> module) {
        // Inform event listeners.
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
                    if (listeners != null) {
                        for (IFModuleStructureEventListener listener : listeners) {
                            listener.moduleFavoriteAdded(module);
                        }
                    }
                }
            });
        }
        else {
            IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
            if (listeners != null) {
                for (IFModuleStructureEventListener listener : listeners) {
                    listener.moduleFavoriteAdded(module);
                }
            }
        }
    }

    protected void fireModuleFavoriteOrderChanged() {
        // Inform event listeners.
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
                    if (listeners != null) {
                        for (IFModuleStructureEventListener listener : listeners) {
                            listener.moduleFavoriteOrderChanged();
                        }
                    }
                }
            });
        }
        else {
            IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
            if (listeners != null) {
                for (IFModuleStructureEventListener listener : listeners) {
                    listener.moduleFavoriteOrderChanged();
                }
            }
        }
    }

    protected void fireModuleFavoriteRemoved(final IFModule<?> module) {
        // Inform event listeners.
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
                    if (listeners != null) {
                        for (IFModuleStructureEventListener listener : listeners) {
                            listener.moduleFavoriteRemoved(module);
                        }
                    }
                }
            });
        }
        else {
            IFModuleStructureEventListener[] listeners = listenerList.getListeners(IFModuleStructureEventListener.class);
            if (listeners != null) {
                for (IFModuleStructureEventListener listener : listeners) {
                    listener.moduleFavoriteRemoved(module);
                }
            }
        }
    }

    @Override
    public void fireModuleNameChanged(final IFController controller) {
        // Inform event listeners.
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    uncheckedFireModuleNameChanged(controller);
                }
            });
        }
        else {
            uncheckedFireModuleNameChanged(controller);
        }
    }

    private void uncheckedFireModuleNameChanged(IFController controller) {
        IFModuleEventListener[] listeners = listenerList.getListeners(IFModuleEventListener.class);
        if (listeners != null) {
            for (IFModuleEventListener listener : listeners) {
                listener.nameChanged(controller);
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

    private void fireControllerBlocked(IFController controller, Object blocker) {
        for (final IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
            listener.moduleBlocked(controller, blocker);
        }
    }

    private void fireControllerUnblocked(IFController controller, Object blocker) {
        for (final IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
            listener.moduleUnblocked(controller, blocker);
        }
    }

    private void fireBlockerRemoved(IFController controller, Object blocker) {
        for (final IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
            listener.moduleBlockerRemoved(controller, blocker);
        }
    }

    @Override
    public void addBlocker(IFController controller, Object blocker) {
        if (blockers.get(controller) == null) {
            final IdentityHashMap<Object, Object> m = new IdentityHashMap<Object, Object>();
            m.put(blocker, blocker);
            blockers.put(controller, m);
            // TODO KUH Check why this was fireControllerBlocked(controller, this)
            fireControllerBlocked(controller, blocker);
        }
        else {
            final boolean wasBlocked = isBlocked(controller);
            final IdentityHashMap<Object, Object> m = blockers.get(controller);
            if (m.containsKey(blocker)) {
                throw new IllegalStateException("BUG: attempting to block twice with the same object: " + blocker);
            }
            m.put(blocker, blocker);
            if (!wasBlocked) {
                fireControllerBlocked(controller, blocker);
            }
        }
    }

    @Override
    public void removeBlocker(final IFController controller, final Object blocker) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean wasBlocked = isBlocked(controller);
                final IdentityHashMap<Object, Object> m = blockers.get(controller);
                if ((m == null) || (m.remove(blocker) == null)) {
                    throw new IllegalStateException(
                        "BUG: attempting to unblock with a blocker for which there was no block: " + blocker);
                }
                if (wasBlocked && !isBlocked(controller)) {
                    fireControllerUnblocked(controller, blocker);
                }
                else if (wasBlocked && !isBlockedByBlocker(controller, blocker)) {
                    fireBlockerRemoved(controller, blocker);
                }

                if(m.size() == 0){
                    blockers.remove(controller);
                }
            }
        });
    }

    @Override
    public boolean isBlockedByBlocker(IFController controller, Object blocker) {
        final IdentityHashMap<Object, Object> m = blockers.get(controller);
        return (m != null && !m.isEmpty() && m.containsKey(blocker));
    }

    @Override
    public boolean isBlocked(IFController controller) {
        final IdentityHashMap<Object, Object> m = blockers.get(controller);
        return (m != null) && !m.isEmpty();
    }

    @Override
    public List<IFModule<?>> getAllModules() {
        return new ArrayList<IFModule<?>>(moduleMap.values());
    }

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(e.getID() == KeyEvent.KEY_LAST) {
			final KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
			if(openHotkeyControllerMap.containsKey(keyStroke)) {
				activateModule(openHotkeyControllerMap.get(keyStroke));
				e.consume();
				return true;
			} else if(hotkeyModuleIdMap.containsKey(keyStroke)) {
				String moduleId = hotkeyModuleIdMap.get(keyStroke);
				openModule(moduleId, null, new ControllerProviderCallback() {
                    @Override
                    public void onControllerReady(IFController controller) {
                        super.onControllerReady(controller);
                        openHotkeyControllerMap.put(keyStroke, controller);
                    }

                }, new IFCloseCallback() {
                    @Override
                    public void wasClosed(IFController controller) {
                        if (openHotkeyControllerMap.containsKey(keyStroke)) {
                            openHotkeyControllerMap.remove(keyStroke);
                        }
                    }
                });
				e.consume();
				return true;
			}
		}
		return false;
	}

	@Override
	public void clearHotkeys() {
		hotkeyModuleIdMap.clear();
		openHotkeyControllerMap.clear();
	}

	@Override
	public void registerHotkey(KeyStroke keyStroke, String moduleId) {
		hotkeyModuleIdMap.put(keyStroke, moduleId);
	}

	private void loadHotkeys() {
		loadKeyAndAddToList("1");
		loadKeyAndAddToList("F2");
		loadKeyAndAddToList("F3");
		loadKeyAndAddToList("F4");
		loadKeyAndAddToList("F5");
		loadKeyAndAddToList("F6");
		loadKeyAndAddToList("F7");
		loadKeyAndAddToList("F8");
		loadKeyAndAddToList("F9");
		loadKeyAndAddToList("F10");
		loadKeyAndAddToList("F11");
		loadKeyAndAddToList("F12");
	}

	private void loadKeyAndAddToList(String key) {
		String moduleId = Ulrice.getAppPrefs().getConfiguration(this, key, null);
		if(moduleId != null) {
			registerHotkey(KeyStroke.getKeyStroke("ctrl " + key), moduleId);
		}
	}



	@Override
	public void moveFavoriteDown(IFModule<?> module) {
		int idx = favorites.indexOf(module.getUniqueId());
		if(idx >= 0 && idx <= favorites.size() - 2) {
			favorites.remove(module.getUniqueId());
			favorites.add(idx + 1, module.getUniqueId());
			fireModuleFavoriteOrderChanged();
		}
	}

	@Override
	public void moveFavoriteUp(IFModule<?> module) {
		int idx = favorites.indexOf(module.getUniqueId());
		if(idx > 0) {
			favorites.remove(module.getUniqueId());
			favorites.add(idx - 1, module.getUniqueId());
			fireModuleFavoriteOrderChanged();
		}
	}

	@Override
	public void addModuleFavorite(IFModule<?> module) {
		favorites.add(module.getUniqueId());
		fireModuleFavoriteAdded(module);
	}

	@Override
	public void removeModuleFavorite(IFModule<?> module) {
		favorites.remove(module.getUniqueId());
		fireModuleFavoriteRemoved(module);
	}

	@Override
	public List<IFModule<?>> getFavoriteModules() {
		List<IFModule<?>> result = new ArrayList<IFModule<?>>();
		for(String moduleId : Collections.unmodifiableCollection(favorites)) {
			if(moduleMap.containsKey(moduleId)) {
				result.add(moduleMap.get(moduleId));
			}
		}
		return result;
	}

	@Override
	public boolean isModuleAFavorite(IFModule<?> module) {
		return favorites.contains(module.getUniqueId());
	}


	private void loadFavorites() {
		String favoritesString = Ulrice.getAppPrefs().getConfiguration(this, "ModuleFavorites", "");
		String[] moduleIds = favoritesString.split(";");
		for(String moduleId : moduleIds) {
			favorites.add(moduleId);
		}
		fireModuleFavoriteOrderChanged();
	}

	@Override
	public void shutdown() {
		StringBuilder value = new StringBuilder();
		for(String module : favorites) {
			value.append(module).append(';');
		}

		Ulrice.getAppPrefs().putConfiguration(this, "ModuleFavorites", value.toString());
	}
}

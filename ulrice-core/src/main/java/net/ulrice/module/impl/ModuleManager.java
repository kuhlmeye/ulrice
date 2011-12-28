package net.ulrice.module.impl;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.ulrice.Ulrice;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleGroup;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.ModuleType;
import net.ulrice.module.event.IFModuleEventListener;
import net.ulrice.module.event.IFModuleStructureEventListener;
import net.ulrice.module.exception.ModuleInstantiationException;

/**
 * The default module manager.
 * 
 * @author ckuhlmeyer
 */
public class ModuleManager implements IFModuleManager, IFModuleStructureManager {

    /** The logger used by the module manager. */
    private static final Logger LOG = Logger.getLogger(ModuleManager.class.getName());

    /** The mapping between module and module id. */
    private final Map<String, IFModule> moduleMap = new HashMap<String, IFModule>();

    /** The set of all controller instances. */
    private final OpenControllerPool openControllers = new OpenControllerPool();

    /** List of all listeners listening on module manager events. */
    private final EventListenerList listenerList = new EventListenerList();

    /** The root group of this module manager. */
    private final UlriceRootModule rootGroup = new UlriceRootModule();

    private final IdentityHashMap<IFController, IdentityHashMap<Object, Object>> blockers =
            new IdentityHashMap<IFController, IdentityHashMap<Object, Object>>();

    public void openModule(final String moduleId, final ControllerProviderCallback callback) {
        openModule(moduleId, null, callback);
    }

    public void openModule(final String moduleId, final IFController parent, final ControllerProviderCallback callback) {
        final IFModule module = moduleMap.get(moduleId);

        try {
            Ulrice.getMainFrame().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (module == null) {
                callback.onFailure(new ModuleInstantiationException("Module with id (" + moduleId
                    + ") could not be found.", null));
                return;
            }

            final boolean isSingleModule = ModuleType.SingleModule.equals(module.getModuleInstanceType());

            if (isSingleModule && openControllers.getControllers(module).size() > 0) {
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

                        if(callback != null) {
                        	callback.onControllerInitialization(controller);
                        }
                        
                        // Inform event listeners.
                        if (openControllers.getActive() != null) {
                            for (IFModuleEventListener listener : listenerList
                                .getListeners(IFModuleEventListener.class)) {
                                listener.deactivateModule(openControllers.getActive());
                            }
                        }

                        openControllers.addController(controller, parent, module);

                        for (IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
                            listener.openModule(controller);
                        }

                        controller.postCreate();

                        activateModule(controller);

                        if (callback != null) {
                            callback.onControllerReady(controller);
                        }
                    }

                    @Override
                    public void onFailure(ModuleInstantiationException exc) {
                        if (callback != null) {
                            callback.onFailure(exc);
                        }
                    }
                });
            }
        }
        finally {
            Ulrice.getMainFrame().getFrame().setCursor(Cursor.getDefaultCursor());
        }
    }

    public IFModule getModule(IFController controller) {
        return openControllers.getModule(controller);
    }

    public IFModuleTitleProvider getTitleProvider(IFController controller) {
        final IFModuleTitleProvider fromController = controller.getTitleProvider();
        if (fromController != null) {
            return fromController;
        }
        return getModule(controller);
    }

    @Override
    public String getModuleTitle(IFController controller, Usage usage) {
        IFModuleTitleProvider titleProvider = getTitleProvider(controller);
        if (titleProvider != null) {
            return titleProvider.getModuleTitle(usage);
        }
        return null;
    }

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

    private void internalCloseController(final IFController controller) {

        for (IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
            listener.closeController(controller);
        }

        openControllers.removeController(controller);

        if (openControllers.getActive() != null) {
            activateModule(openControllers.getActive());
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

    public IFController getCurrentController() {
        return openControllers.getActive();
    }

    public List<IFController> getActiveControllers() {
        return openControllers.getAll();
    }

    public IFController getParentController(IFController controller) {
        return openControllers.getParent(controller);
    }

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
     * @param group The group of modules that should be added to this module.
     */
    public void addModuleGroup(IFModuleGroup group) {
        rootGroup.addModuleGroup(group);
    }

    /**
     * Add a module to this module group
     * 
     * @param module The module that should be added to this group.
     */
    public void addModule(IFModule module) {
        if (!Ulrice.getSecurityManager().allowRegisterModule(module)) {
            LOG.info("Module [Id: " + module.getUniqueId() + ", Name: " + module.getModuleTitle(Usage.Default)
                + "] will not be added. Not authorized by ulrice security manager.");
            return;
        }

        rootGroup.addModule(module);
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
            listener.moduleBlocked(openControllers.getActive(), blocker);
        }
    }

    private void fireControllerUnblocked(IFController controller, Object blocker) {
        for (final IFModuleEventListener listener : listenerList.getListeners(IFModuleEventListener.class)) {
            listener.moduleUnblocked(openControllers.getActive(), blocker);
        }
    }

    @Override
    public void addBlocker(IFController controller, Object blocker) {
        if (blockers.get(controller) == null) {
            final IdentityHashMap<Object, Object> m = new IdentityHashMap<Object, Object>();
            m.put(blocker, blocker);
            blockers.put(controller, m);
            fireControllerBlocked(controller, this);
        }
        else {
            final boolean wasBlocked = isBlocked(controller);
            final IdentityHashMap<Object, Object> m = blockers.get(controller);
            if (m.containsKey(blocker)) {
                throw new IllegalStateException("BUG: attempting to block twice with the same object: " + blocker);
            }
            m.put(blocker, blocker);
            if (!wasBlocked) {
                fireControllerBlocked(controller, this);
            }
        }
    }

    @Override
    public void removeBlocker(IFController controller, Object blocker) {
        boolean wasBlocked = isBlocked(controller);
        final IdentityHashMap<Object, Object> m = blockers.get(controller);
        if (m == null || m.remove(blocker) == null) {
            throw new IllegalStateException(
                "BUG: attempting to unblock with a blocker for which there was no block: " + blocker);
        }
        if (wasBlocked && !isBlocked(controller)) {
            fireControllerUnblocked(controller, this);
        }
    }

    @Override
    public boolean isBlocked(IFController controller) {
        final IdentityHashMap<Object, Object> m = blockers.get(controller);
        return m != null && !m.isEmpty();
    }

    @Override
    public List<IFModule> getAllModules() {
        return new ArrayList<IFModule>(moduleMap.values());
    }

}

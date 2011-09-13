package net.ulrice.module.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;


/**
 * This class manages all currently open non-single-module controllers
 * 
 * @author arno
 */
class OpenControllerPool {
    private final List<IFController> ordered = new ArrayList<IFController>();
    private final Map<IFController, IFController> withParent = new IdentityHashMap<IFController, IFController>();
    private final Map<IFModule, List<IFController>> byModule = new IdentityHashMap<IFModule, List<IFController>>();
    private final Map<IFController, IFModule> moduleByController = new IdentityHashMap<IFController, IFModule>();
    
    public void addController (IFController c, IFController parent, IFModule module) {
        ordered.add (0, c);
        withParent.put (c, parent);
        getControllers (module).add(c);
        moduleByController.put(c, module);
    }

    public List<IFController> getControllers (IFModule module) {
        if (! byModule.containsKey (module)) {
            final List<IFController> l = new ArrayList<IFController>();
            byModule.put(module, l);
        }
        return byModule.get(module);
    }
    
    public IFModule getModule (IFController c) {
        return moduleByController.get(c);
    }
    
    public IFController getParent(IFController c) {
    	if (withParent.containsKey(c)) {
    		return withParent.get(c);
    	}
    	return null;
    }
    
    public Collection<IFController> getChildren (IFController parent) {
        final List<IFController> result = new ArrayList<IFController>();
        for (IFController child: withParent.keySet()) {
            if (withParent.get (child) == parent) {
                result.add (child);
            }
        }
        return result;
    }
    
    public void removeController (IFController c) {
        ordered.remove(c);
        withParent.remove(c);
        for (List<IFController> l: byModule.values()) {
            l.remove(c);
        }
        moduleByController.remove(c);
    }
    
    public List<IFController> getAll () {
        return new ArrayList<IFController> (ordered);
    }
    
    public IFController getActive () {
        if (ordered.isEmpty()) {
            return null;
        }
        return ordered.get(0);
    }
    
    public void makeActive (IFController controller) {
        ordered.remove(controller);
        ordered.add(0, controller);
    }
}

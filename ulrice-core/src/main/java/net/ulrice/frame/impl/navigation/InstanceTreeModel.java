/**
 * 
 */
package net.ulrice.frame.impl.navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.event.IFModuleEventListener;

/**
 * @author christof
 * 
 */
public class InstanceTreeModel implements TreeModel, IFModuleEventListener {

	/** The root of the tree. */
	private final Object ROOT_OBJECT = new String("ROOT");

	/** The module manager. */
	private IFModuleManager moduleManager;

	/** The list holding the event listeners. */
	private EventListenerList listenerList = new EventListenerList();

	/** List of instances per controller. */
	Map<IFModule, List<IFController>> instanceMap = new HashMap<IFModule, List<IFController>>();

	List<IFModule> moduleList = new ArrayList<IFModule>();

	public InstanceTreeModel() {
		moduleManager = Ulrice.getModuleManager();
		moduleManager.addModuleEventListener(this);

		// Build up the basic list.
		if (moduleManager != null) {
			List<IFController> activeModules = moduleManager.getActiveControllers();
			if (activeModules != null) {
				for (IFController controller : activeModules) {
					addController(controller);
				}
			}
		}
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(Object node, int index) {
		// If the root of the tree is given, the number of modules is the
		// child-count
		if (ROOT_OBJECT.equals(node)) {
			return moduleList == null ? -1 : moduleList.get(index);
		}

		// If a module is given, the number of instances of this module is the
		// child-count.
		if (node instanceof IFModule) {
			List<IFController> instanceList = instanceMap.get((IFModule) node);
			return instanceList == null ? -1 : instanceList.get(index);
		}

		// If a controller is given, there are no childs.
		if (node instanceof IFController) {
			return null;
		}

		return null;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(Object node) {

		// If the root of the tree is given, the number of modules is the
		// child-count
		if (ROOT_OBJECT.equals(node)) {
			return instanceMap.size();
		}

		// If a module is given, the number of instances of this module is the
		// child-count.
		if (node instanceof IFModule) {
			List<IFController> instanceList = instanceMap.get((IFModule) node);
			return instanceList == null ? 0 : instanceList.size();
		}

		// If a controller is given, there are no childs.
		if (node instanceof IFController) {
			return 0;
		}

		return 0;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(Object node, Object object) {

		// If the root of the tree is given, the number of modules is the
		// child-count
		if (ROOT_OBJECT.equals(node)) {
			return moduleList == null ? -1 : moduleList.indexOf(object);
		}

		// If a module is given, the number of instances of this module is the
		// child-count.
		if (node instanceof IFModule) {
			List<IFController> instanceList = instanceMap.get((IFModule) node);
			return instanceList == null ? -1 : instanceList.indexOf(object);
		}

		// If a controller is given, there are no childs.
		if (node instanceof IFController) {
			return -1;
		}

		return -1;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return ROOT_OBJECT;
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	/**
	 * Adds an instance of a module to the datastructure of this tree.
	 * 
	 * @param controller
	 *            The controller that should be added to the datastructure.
	 */
	private void addController(IFController controller) {
		if (controller == null) {
			return;
		}

		List<IFController> controllerList = instanceMap.get(Ulrice.getModuleManager().getModule(controller));
		if (controllerList == null) {
			controllerList = new ArrayList<IFController>();
			instanceMap.put(Ulrice.getModuleManager().getModule(controller), controllerList);
			moduleList.add(Ulrice.getModuleManager().getModule(controller));
		}
		controllerList.add(controller);

		fireTreeStructureChanged(new TreeModelEvent(getRoot(), new TreePath(getRoot())));
	}

	/**
	 * Removes an instance of a module from the datastructure of this tree.
	 * 
	 * @param controller
	 *            The controller that should be removed from the datastructure.
	 */
	private void removeController(IFController controller) {
		if (controller == null) {
			return;
		}

		// Get the module
		final IFModule module = Ulrice.getModuleManager().getModule(controller);
		// Get the list of module instances.
		final List<IFController> controllerList = instanceMap.get(module);
		if (controllerList != null) {
		    // Remove the controller from the list of instances.
		    controllerList.remove(controller);

		    // Remove the list from the instance map, if size is null
		    if (controllerList.size() == 0) {
		        instanceMap.remove(module);
		    }
		}

		fireTreeStructureChanged(new TreeModelEvent(getRoot(), new TreePath(getRoot())));
	}

	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void addTreeModelListener(TreeModelListener e) {
		listenerList.add(TreeModelListener.class, e);
	}

	/**
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener e) {
		listenerList.remove(TreeModelListener.class, e);
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// Not needed because the value is not editable.
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	@Override
	public void openModule(IFController activeController) {
		addController(activeController);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeController(net.ulrice.module.IFController)
	 */
	@Override
	public void closeController(IFController activeController) {
		removeController(activeController);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void activateModule(IFController activeController) {
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	@Override
	public void deactivateModule(IFController activeController) {
	}

	/**
	 * Inform the listeners that the tree has changed.
	 * 
	 * @param e
	 *            The tree model event
	 */
	private void fireTreeStructureChanged(TreeModelEvent e) {
		// Inform event listeners.
		TreeModelListener[] listeners = listenerList.getListeners(TreeModelListener.class);
		if (listeners != null) {
			for (TreeModelListener listener : listeners) {
				listener.treeStructureChanged(e);
			}
		}
	}

	@Override
	public void moduleBlocked(IFController controller, Object blocker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleUnblocked(IFController controller, Object blocker) {
		// TODO Auto-generated method stub
		
	}
}

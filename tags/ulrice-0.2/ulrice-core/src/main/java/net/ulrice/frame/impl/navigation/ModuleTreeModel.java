package net.ulrice.frame.impl.navigation;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.ulrice.Ulrice;
import net.ulrice.module.IFModuleGroup;
import net.ulrice.module.IFModuleStructureManager;
import net.ulrice.module.event.IFModuleStructureEventListener;

/**
 * Model for the Tree for displaying all the models available in the application
 * 
 * @author christof
 */
public class ModuleTreeModel implements TreeModel, IFModuleStructureEventListener {

	/** The list of listeners listening to this tree model. */
	private EventListenerList listenerList = new EventListenerList();

	/** The reference to the used structure manager of ulrice. */
	private IFModuleStructureManager structureManager;

	/**
	 * Creates a new model of the module tree.
	 */
	public ModuleTreeModel() {
		structureManager = Ulrice.getModuleStructureManager();
		structureManager.addModuleStructureEventListener(this);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof IFModuleGroup) {
			IFModuleGroup group = (IFModuleGroup) parent;
			int groups = group.getModuleGroups() == null ? 0 : group.getModuleGroups().size();

			return index < groups ? group.getModuleGroups().get(index) : group.getModules().get(index - groups);
		}

		return null;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof IFModuleGroup) {
			IFModuleGroup group = (IFModuleGroup) parent;
			int groups = group.getModuleGroups() == null ? 0 : group.getModuleGroups().size();
			int modules = group.getModules() == null ? 0 : group.getModules().size();

			return modules + groups;
		}

		return 0;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof IFModuleGroup) {
			IFModuleGroup group = (IFModuleGroup) parent;

			int groupIdx = group.getModuleGroups().indexOf(child);
			if (groupIdx >= 0) {
				return groupIdx;
			}
			int moduleIdx = group.getModules().indexOf(child);
			if (moduleIdx >= 0) {
				return groupIdx + moduleIdx;
			}
		}
		return -1;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return structureManager.getRootGroup();
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(Object node) {
		return !(node instanceof IFModuleGroup);
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// Not needed, because tree nodes could not be altered.
	}

	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		listenerList.add(TreeModelListener.class, listener);
	}

	/**
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		listenerList.remove(TreeModelListener.class, listener);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleStructureEventListener#moduleStructureChanged()
	 */
	@Override
	public void moduleStructureChanged() {
		fireTreeStructureChanged(new TreeModelEvent(getRoot(), new TreePath(getRoot())));
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
}

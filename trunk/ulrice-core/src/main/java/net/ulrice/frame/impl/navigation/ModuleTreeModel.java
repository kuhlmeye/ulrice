package net.ulrice.frame.impl.navigation;

import java.util.Comparator;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.ulrice.Ulrice;
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

	private ModuleTreeNode root;

    private ModuleTreeNodeFilter moduleTreeFilter;

    private Comparator<ModuleTreeNode> moduleNodeComparator;
	
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
		if (parent instanceof ModuleTreeNode) {
			return ((ModuleTreeNode)parent).getChild(index);
		}

		return null;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof ModuleTreeNode) {
			return ((ModuleTreeNode)parent).getChildCount();
		}

		return 0;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof ModuleTreeNode && child instanceof ModuleTreeNode) {
		    ((ModuleTreeNode)parent).getIndex((ModuleTreeNode)child);
		}
		return -1;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return root;
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(Object node) {
		return !((ModuleTreeNode)node).hasChilds();
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
        root = new ModuleTreeNode(structureManager.getRootGroup(), moduleNodeComparator, moduleTreeFilter);        
		fireTreeStructureChanged(new TreeModelEvent(getRoot(), new TreePath(getRoot())));	
	}
	
	public void setModuleTreeFilter(ModuleTreeNodeFilter moduleTreeFilter) {
	    this.moduleTreeFilter = moduleTreeFilter;
	}
	
	public void setNodeComparator(Comparator<ModuleTreeNode> moduleNodeComparator) {
	    this.moduleNodeComparator = moduleNodeComparator;
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

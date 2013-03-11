package net.ulrice.frame.impl.navigation;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFMainFrameComponent;
import net.ulrice.module.ControllerProviderCallback;
import net.ulrice.module.IFController;
import net.ulrice.module.exception.ModuleInstantiationException;

/**
 * Tree component which displays all available modules.
 * 
 * @author ckuhlmeyer
 */
public class ModuleTree extends JTree implements IFMainFrameComponent, MouseListener {

    /** Default generated serial version uid. */
    private static final long serialVersionUID = -4770717871966866067L;

    /**
     * Creates a new module tree
     */
    public ModuleTree() {
        super(new ModuleTreeModel());
        setRootVisible(false);
        setShowsRootHandles(true);
        setCellRenderer(new ModuleTreeCellRenderer());
        // this seems to be the only way to alter the height
        // the height of the renderer gets ignored all the time 
        setRowHeight(18);
        addMouseListener(this);
        
        addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                if(!isSelectionEmpty() && e.getKeyChar() == '\n' && e.isControlDown()) {
                    TreePath path = getSelectionPath();
                    Object pathComponent = path.getLastPathComponent();
                
                    if (pathComponent instanceof ModuleTreeNode) {
                        ModuleTreeNode node = (ModuleTreeNode) pathComponent;
                        handleNodeActivated(node);
                    }
                }
            }
        });
    }

    /**
     * @see net.ulrice.frame.IFMainFrameComponent#getComponentId()
     */
    @Override
    public String getComponentId() {
        return getClass().getName();
    }

    /**
     * @see net.ulrice.frame.IFMainFrameComponent#getView()
     */
    @Override
    public JComponent getView() {
        return this;
    }

    public void updateModel() {
        ((ModuleTreeModel) getModel()).moduleStructureChanged();
    }

    public void setModuleTreeFilter(ModuleTreeNodeFilter moduleTreeFilter) {
        ((ModuleTreeModel) getModel()).setModuleTreeFilter(moduleTreeFilter);
    }

    public void setNodeComparator(Comparator<ModuleTreeNode> moduleNodeComparator) {
        ((ModuleTreeModel) getModel()).setNodeComparator(moduleNodeComparator);
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && getSelectionPath() != null) {
            TreePath path = getClosestPathForLocation(e.getX(), e.getY());
            Object pathComponent = path.getLastPathComponent();
            Object selComponent = getSelectionPath().getLastPathComponent();
            
            // Compare, if selected component is equal to clicked component.
            // If this is not done, you could run in trouble with nodes that will expand
            if(!pathComponent.equals(selComponent)) {                
                return;
            }
            
            if (pathComponent instanceof ModuleTreeNode) {
                ModuleTreeNode node = (ModuleTreeNode) pathComponent;
                handleNodeActivated(node);
            }
        }
    }

    /**
     * Handles the activation events on nodes. Opens the module or profiled module.
     */
	private void handleNodeActivated(ModuleTreeNode node) {
		switch(node.getNodeType()) {
			case Module:
		        Ulrice.getModuleManager().openModule(node.getModule().getUniqueId(), new ControllerProviderCallback<IFController>() {
		            @Override
		            public void onFailure(ModuleInstantiationException exc) {
		                Ulrice.getMessageHandler().handleException(exc);
		            }

		            @Override
		            public void onControllerReady(IFController controller) {
		            }                                
		        });
		        break;
			case ProfiledModule:
				Ulrice.getProfileManager().openProfiledModule(node.getProfiledModule(), new ControllerProviderCallback<IFController>() {
					
					@Override
					public void onFailure(ModuleInstantiationException exc) {
		                Ulrice.getMessageHandler().handleException(exc);
					}
					
					@Override
					public void onControllerReady(IFController controller) {
					}
				});
				break;
		}
	}

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // Not needed. Empty.
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // Not needed. Empty.
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Not needed. Empty.
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Not needed. Empty.
    }

}

package net.ulrice.frame.impl.navigation;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider.Usage;

/**
 * Renders a node in the instance tree 
 * 
 * @author christof
 */
public class InstanceTreeCellRenderer extends DefaultTreeCellRenderer {
	
	/** Default generated serial version uid. */
	private static final long serialVersionUID = 8026158255162845838L;

	/**
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		Object renderValue = value;
		
		if(value instanceof IFModule) {						
			IFModule module = (IFModule) renderValue;
			renderValue = module.getModuleTitle(Usage.ModuleTree);
		}

		if(value instanceof IFController) {
			IFController controller = (IFController) value;			
			renderValue = Ulrice.getModuleManager().getTitleProvider(controller).getModuleTitle(Usage.ModuleTree);
		}
		
		return super.getTreeCellRendererComponent(tree, renderValue, sel, expanded, leaf, row, hasFocus);
	}

}

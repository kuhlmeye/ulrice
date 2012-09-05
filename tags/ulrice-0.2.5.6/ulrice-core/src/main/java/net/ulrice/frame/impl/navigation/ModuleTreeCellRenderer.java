package net.ulrice.frame.impl.navigation;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.ulrice.module.IFModuleTitleProvider.Usage;

public class ModuleTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		Object renderValue = value;
		
		if(value instanceof ModuleTreeNode) {
		    ModuleTreeNode node = (ModuleTreeNode) renderValue;
			StringBuffer buffer = new StringBuffer();
		    switch(node.getNodeType()) {
		    	case ModuleGroup:
	                buffer.append(node.getModuleGroup().getTitle());
		    		break;
		    	case Module:
	    			buffer.append(node.getModule().getModuleTitle(Usage.ModuleTree));
		    		break;
		    	case ProfiledModule:
	    			buffer.append(node.getProfiledModule().getProfileId());
		    		break;
	    		default:
	    			break;
		    }
			renderValue = buffer.toString();
		}
	
		
		return super.getTreeCellRendererComponent(tree, renderValue, sel, expanded, leaf, row, hasFocus);
	}

}

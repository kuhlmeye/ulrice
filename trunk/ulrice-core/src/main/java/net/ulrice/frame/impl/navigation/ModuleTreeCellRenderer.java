package net.ulrice.frame.impl.navigation;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider.Usage;

public class ModuleTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		Object renderValue = value;
		
		if(value instanceof IFModule) {
			IFModule module = (IFModule) renderValue;
			StringBuffer buffer = new StringBuffer();
			buffer.append(module.getModuleTitle(Usage.ModuleTree));
			renderValue = buffer.toString();
		}
	
		
		return super.getTreeCellRendererComponent(tree, renderValue, sel, expanded, leaf, row, hasFocus);
	}

}

package net.ulrice.frame.impl.navigation;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.ModuleIconSize;

public class ModuleTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 8052377035977724216L;

    public ModuleTreeCellRenderer() {
        super();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
        boolean leaf, int row, boolean hasFocus) {

        Object renderValue = value;
        
        ImageIcon icon = null;

        if (value instanceof ModuleTreeNode) {
            ModuleTreeNode node = (ModuleTreeNode) renderValue;
            StringBuffer buffer = new StringBuffer();
            switch (node.getNodeType()) {
                case ModuleGroup:
                    buffer.append(node.getModuleGroup().getTitle());
                    break;
                case Module:
                    buffer.append(node.getModule().getModuleTitle(Usage.ModuleTree));
                    icon = node.getModule().getIcon(ModuleIconSize.Size_16x16);
                    break;
                case ProfiledModule:
                    buffer.append(node.getProfiledModule().getProfileId());
                    icon = node.getProfiledModule().getProfileHandlerModule().getIcon(ModuleIconSize.Size_16x16);
                    break;
                default:
                    break;
            }
            renderValue = buffer.toString();
        }

        Component rendererComponent = super.getTreeCellRendererComponent(tree, renderValue, sel, expanded, leaf, row, hasFocus);
        if(icon != null) {
	        if(rendererComponent instanceof JLabel) {
	        	JLabel renderLabel = (JLabel) rendererComponent;
	        	renderLabel.setIcon(icon);
	        }
        }
		return rendererComponent;
    }

}

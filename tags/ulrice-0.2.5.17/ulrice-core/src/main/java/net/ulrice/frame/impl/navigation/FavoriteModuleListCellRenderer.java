package net.ulrice.frame.impl.navigation;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.ModuleIconSize;

/**
 * Renders a node in the favorite module list
 * 
 * @author christof
 */
public class FavoriteModuleListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int idx, boolean sel, boolean hasFocus) {
		Object renderValue = value;
		
		Icon icon = null;
		if(value instanceof IFModule) {						
			@SuppressWarnings("rawtypes")
			IFModule<?> module = (IFModule) renderValue;
			icon = module.getIcon(ModuleIconSize.Size_16x16);
			renderValue = module.getModuleTitle(Usage.ModuleTree);
		}

		if(value instanceof IFController) {
			IFController controller = (IFController) value;			
			renderValue = Ulrice.getModuleManager().getTitleProvider(controller).getModuleTitle(Usage.ModuleTree);
		}
		JLabel label = (JLabel) super.getListCellRendererComponent(list, renderValue, idx, sel, hasFocus);
		if(icon != null) {
			label.setIcon(icon);
		}
		return label;
	}
}

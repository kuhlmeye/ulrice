package net.ulrice.frame.impl.navigation;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFModuleTitleProvider.Usage;

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
		
		if(value instanceof IFModule) {						
			@SuppressWarnings("rawtypes")
			IFModule<?> module = (IFModule) renderValue;
			renderValue = module.getModuleTitle(Usage.ModuleTree);
		}

		if(value instanceof IFController) {
			IFController controller = (IFController) value;			
			renderValue = Ulrice.getModuleManager().getTitleProvider(controller).getModuleTitle(Usage.ModuleTree);
		}
		
		return super.getListCellRendererComponent(list, renderValue, idx, sel, hasFocus);
	}
}

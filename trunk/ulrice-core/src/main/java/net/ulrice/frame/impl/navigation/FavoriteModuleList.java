package net.ulrice.frame.impl.navigation;

import javax.swing.JComponent;
import javax.swing.JList;

import net.ulrice.frame.IFMainFrameComponent;
import net.ulrice.module.IFModule;

public class FavoriteModuleList extends JList<IFModule<?>> implements IFMainFrameComponent {

	private static final long serialVersionUID = 1L;

	public FavoriteModuleList() {
		super(new FavoriteModuleListModel());
	}
	
	@Override
	public String getComponentId() {
		return FavoriteModuleList.class.getName();
	}

	@Override
	public JComponent getView() {
		return this;
	}
}

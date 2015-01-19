package net.ulrice.frame.impl.navigation;

import javax.swing.JComponent;
import javax.swing.JList;

import net.ulrice.frame.IFMainFrameComponent;

public class FavoriteModuleList extends JList implements IFMainFrameComponent {

	private static final long serialVersionUID = 1L;

	public FavoriteModuleList() {
		super(new FavoriteModuleListModel());
		setCellRenderer(new FavoriteModuleListCellRenderer());
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

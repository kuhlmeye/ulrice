package net.ulrice.frame.impl.navigation;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import net.ulrice.ConfigurationListener;
import net.ulrice.Ulrice;
import net.ulrice.module.IFModule;
import net.ulrice.module.event.IFModuleStructureEventListener;

public class FavoriteModuleListModel extends AbstractListModel implements IFModuleStructureEventListener {

	private static final long serialVersionUID = -1937493714377974393L;
	private ArrayList<IFModule<?>> data;
	
	public FavoriteModuleListModel() {
		Ulrice.addConfigurationListener(new ConfigurationListener() {
			
			@Override
			public void initializationFinished() {
				Ulrice.getModuleStructureManager().addModuleStructureEventListener(FavoriteModuleListModel.this);
				rebuildModel();
			}
		});
	}

	private void rebuildModel() {
		data = new ArrayList<IFModule<?>>(Ulrice.getModuleStructureManager().getFavoriteModules());
		fireContentsChanged(this, 0, getSize());
	}

	@Override
	public IFModule<?> getElementAt(int index) {
		return data.get(index);
	}

	@Override
	public int getSize() {
		return data.size();
	}

	@Override
	public void moduleStructureChanged() {
	}

	@Override
	public void moduleFavoriteAdded(IFModule<?> module) {
		rebuildModel();
	}

	@Override
	public void moduleFavoriteRemoved(IFModule<?> module) {
		rebuildModel();
	}
	
	@Override
	public void moduleFavoriteOrderChanged() {
		rebuildModel();
	}
}

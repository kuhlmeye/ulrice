package net.ulrice.module.event;

import net.ulrice.module.IFModule;

public abstract class AbstractModuleStructureEventListener implements IFModuleStructureEventListener {

	@Override
	public void moduleStructureChanged() {
	}

	@Override
	public void moduleFavoriteAdded(IFModule<?> module) {
	}

	@Override
	public void moduleFavoriteRemoved(IFModule<?> module) {
	}
	
	@Override
	public void moduleFavoriteOrderChanged() {
	}
}

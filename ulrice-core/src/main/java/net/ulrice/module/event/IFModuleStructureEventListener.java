package net.ulrice.module.event;

import java.util.EventListener;

import net.ulrice.module.IFModule;

public interface IFModuleStructureEventListener extends EventListener {

	void moduleStructureChanged();
	
	void moduleFavoriteAdded(IFModule<?> module);
	void moduleFavoriteRemoved(IFModule<?> module);
	void moduleFavoriteOrderChanged();
}

package net.ulrice.databinding.bufferedbinding.impl;

import java.util.EventListener;

public interface ElementLifecycleListener extends EventListener {

	void elementChanged(TableAM table, Element element, String columnId);

	void elementAdded(TableAM table, Element element);
	
	void elementRemoved(TableAM table, Element element);

	void tableCleared(TableAM table);
}

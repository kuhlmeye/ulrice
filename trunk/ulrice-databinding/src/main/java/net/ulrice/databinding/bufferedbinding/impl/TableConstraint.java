package net.ulrice.databinding.bufferedbinding.impl;

public interface TableConstraint {

	void elementChanged(TableAM table, Element element);

	void elementAdded(TableAM table, Element element);
	
	void elementRemoved(TableAM table, Element element);

	void tableCleared(TableAM table);
}

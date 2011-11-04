package net.ulrice.databinding.bufferedbinding.impl;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.bufferedbinding.IFBindingGroup;
import net.ulrice.databinding.bufferedbinding.IFBindingGroupEventListener;
import net.ulrice.databinding.viewadapter.IFViewAdapter;


public abstract class AbstractBindingGroup<T> implements IFBindingGroup,
		IFAttributeModelEventListener<T> {
	
	private final EventListenerList listenerList = new EventListenerList();

	@Override
	public void addBindingGroupChangeListener(IFBindingGroupEventListener l) {
		listenerList.add(IFBindingGroupEventListener.class, l);
	}

	@Override
	public void removeBindingGroupChangeListener(IFBindingGroupEventListener l) {
		listenerList.remove(IFBindingGroupEventListener.class, l);
	}
	
	protected void fireBindingGroupChanged() {
    	IFBindingGroupEventListener[] listeners = listenerList.getListeners(IFBindingGroupEventListener.class);
    	if (listeners != null) {
    		for (IFBindingGroupEventListener l : listeners) {
    			l.bindingGroupChanged(this);
    		}
    	}
    }

	@Override
	public void stateChanged(IFViewAdapter viewAdapter,
			IFAttributeModel<T> amSource) {

		stateChangedInternal(viewAdapter, amSource);
		
		fireBindingGroupChanged();		
	}
	
	abstract protected void stateChangedInternal(IFViewAdapter viewAdapter, IFAttributeModel<T> amSource);

}

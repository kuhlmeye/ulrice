package net.ulrice.databinding.viewadapter.impl;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.ListAM;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

public class JListViewAdapter extends AbstractViewAdapter implements ListModel {

    private EventListenerList listenerList = new EventListenerList();
    private ListAM<? extends List<?>, ?> attributeModel;
    private JList list;
    
    public JListViewAdapter(JList list) {
    	super(List.class);
    	this.list = list;
    }
    
	@Override
	public void updateBinding(IFBinding binding) {
		if(binding instanceof ListAM) {
			attributeModel = (ListAM)binding;									
		}
		if(!isInNotification()) {
			fireListChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, attributeModel.getSize()));
		}		
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, list);
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, list);
		}
	}
	
    private void fireListChanged(ListDataEvent e) {
    	ListDataListener[] listeners = listenerList.getListeners(ListDataListener.class);
        if (listeners != null) {
            for (ListDataListener listener : listeners) {
                listener.contentsChanged(e);
            }
        }
    }

	@Override
	public Object getValue() {
		return attributeModel.getCurrentValue();
	}

	@Override
	public JComponent getComponent() {
		return list;
	}

	@Override
	public void setEnabled(boolean enabled) {
		list.setEnabled(true);
	}

	@Override
	public boolean isEnabled() {
		return list.isEnabled();
	}

	@Override
	public int getSize() {
        if (attributeModel != null) {
            return attributeModel.getRowCount();
        }
        return 0;
	}

	@Override
	public Object getElementAt(int index) {
        if (attributeModel != null) {
            return attributeModel.getElementAt(index);
        }
        return null;
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);	
	}
}

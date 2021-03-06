package net.ulrice.databinding.viewadapter.impl;

import java.util.List;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.TableAM;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

public class JListViewAdapter extends AbstractViewAdapter implements ListModel {

    private EventListenerList listenerList = new EventListenerList();
    private TableAM attributeModel;
    private JList list;
    
    public JListViewAdapter(JList list, IFAttributeInfo attributeInfo) {
    	super(List.class, attributeInfo);
    	this.list = list;
        setEditable(list.isEnabled());
    }
    
	@Override
	public void updateFromBinding(IFBinding binding) {
		if(binding instanceof TableAM) {
			attributeModel = (TableAM)binding;									
		}
		if(!isInNotification()) {
			fireListChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, attributeModel.getRowCount()));
		}		
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, list);
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, isEditable(), binding.isDirty(), binding.isValid(), list);
		}
	}
	
    @Override
    protected void setEditableInternal(boolean editable) {
        list.setEnabled(editable);
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
	public JList getComponent() {
		return list;
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


	@Override
	public Object getValue() {
		return null;
	}

	@Override
	protected void setValue(Object value) {
	}

	@Override
	protected void addComponentListener() {
	}

	@Override
	protected void removeComponentListener() {
	}

    @Override
    public Object getDisplayedValue() {
        return null;
    }
}

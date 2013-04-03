package net.ulrice.databinding.bufferedbinding.impl;

import java.util.Collection;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;

public class ListAM<T> extends GenericAM<List<T>> implements ListModel {

    public ListAM(IFModelValueAccessor modelAccessor, IFAttributeInfo attributeInfo, boolean isListOrderRelevant) {
        super(modelAccessor, attributeInfo);
        setListOrderRelevant(isListOrderRelevant);
    }

    public ListAM(String id, IFAttributeInfo attributeInfo, boolean readOnly, boolean isListOrderRelevant) {
        super(id, attributeInfo, readOnly);
        setListOrderRelevant(isListOrderRelevant);
    }

    public boolean add(T object) {
        int index = getCurrentValue().size();
        boolean result = getCurrentValue().add(object);
        calculateState(null);
        fireDataChanged(null);
        if (result) {
            fireIntervalAdded(this, index, index);
        }
        return result;
    }

    public void add(int index, T object) {
        getCurrentValue().add(index, object);
        calculateState(null);
        fireDataChanged(null);
        fireIntervalAdded(this, index, index);
    }

    public boolean addAll(Collection<T> c) {
        int index = getCurrentValue().size();
        boolean result = getCurrentValue().addAll(c);
        calculateState(null);
        fireDataChanged(null);
        if (result) {
            fireIntervalAdded(this, index, index + c.size() - 1);
        }
        return result;
    }

    public boolean addAll(int index, Collection<T> c) {
        boolean result = getCurrentValue().addAll(index, c);
        calculateState(null);
        fireDataChanged(null);
        if (result) {
            fireIntervalAdded(this, index, index + c.size() - 1);
        }
        return result;
    }

    public boolean remove(T object) {
        boolean result = getCurrentValue().remove(object);
        calculateState(null);
        fireDataChanged(null);
        return result;
    }

    public T remove(int index) {
        T result = getCurrentValue().remove(index);
        calculateState(null);
        fireDataChanged(null);
        return result;
    }

    @Override
    public int getSize() {
        if (getCurrentValue() == null) {
            return 0;
        }
        return getCurrentValue().size();
    }

    @Override
    public T getElementAt(int index) {
        return getCurrentValue().get(index);
    }

    @Override
    public void addListDataListener(ListDataListener listener) {
        listenerList.add(ListDataListener.class, listener);

    }

    @Override
    public void removeListDataListener(ListDataListener listener) {
        listenerList.remove(ListDataListener.class, listener);

    }

    public ListDataListener[] getListDataListeners() {
        return listenerList.getListeners(ListDataListener.class);
    }

    protected void fireContentsChanged(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    protected void fireIntervalAdded(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    protected void fireIntervalRemoved(Object source, int index0, int index1) {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    public boolean contains(Object item) {
        return getCurrentValue().contains(item);
    }

}

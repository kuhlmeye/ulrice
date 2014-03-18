package net.ulrice.databinding.viewadapter.utable;


import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ulrice.databinding.bufferedbinding.impl.Element;

public abstract class UTableListSelectionListener<T> implements ListSelectionListener {
    
    private Element currentElement = null;
    private UTableComponent table;
    
    public UTableListSelectionListener(UTableComponent table) {
        this.table = table;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void valueChanged(ListSelectionEvent e) {
        Element oldElement = null;
        Element newElement = table.getSelectedElement();
        boolean changed = true;
        
        if(currentElement != null) {
            oldElement = currentElement;
            
            changed = !oldElement.equals(newElement);
        }             
        currentElement = newElement;
        
        if(changed) {
            listSelectionChanged(oldElement, oldElement != null ? ((T)oldElement.getCurrentValue()) : null, newElement, newElement != null ? ((T)newElement.getCurrentValue()) : null);
        }
    }
    
    protected abstract void listSelectionChanged(Element oldElement, T oldObject, Element newElement, T newObject);

    public Element getCurrentElement() {
        return currentElement;
    }
}

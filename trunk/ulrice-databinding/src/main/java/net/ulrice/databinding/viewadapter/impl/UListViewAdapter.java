package net.ulrice.databinding.viewadapter.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

public class UListViewAdapter extends AbstractViewAdapter implements ListModel, ListDataListener {

    private JList listComponent;
    private DefaultListModel model;

    public UListViewAdapter(JList listComponent, Class viewType, IFAttributeInfo attributeInfo) {
        super(viewType, attributeInfo);
        this.listComponent = listComponent;
        this.model = new DefaultListModel();
        listComponent.setModel(model);
    }

    @Override
    public List getValue() {
        return Collections.list(model.elements());
    }

    @Override
    public List getDisplayedValue() {
        return Collections.list(model.elements());
    }

    @Override
    public JList getComponent() {
        return listComponent;
    }

    @Override
    public int getSize() {
        return model.getSize();
    }

    @Override
    public Object getElementAt(int index) {
        if (getSize() > index) {
            return model.getElementAt(index);
        }
        return null;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        model.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        model.removeListDataListener(l);
    }

    @Override
    protected void setEditableInternal(boolean editable) {
        listComponent.setEnabled(editable);
    }

    @Override
    protected void addComponentListener() {
        model.addListDataListener(this);
        fireViewChange();
    }

    @Override
    protected void setValue(Object value) {
        model.removeAllElements();
        if(value instanceof Collection){
            for(Object item : (Collection)value){
                model.addElement(item);
            }
        }
        else {
            model.addElement(value);
        }
        fireViewChange();
    }

    @Override
    protected void removeComponentListener() {
        model.removeListDataListener(this);
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        fireViewChange();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        fireViewChange();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        fireViewChange();

    }

    public boolean contains(Object item) {
        return model.contains(item);
    }

    public void add(Object item) {
        model.addElement(item);
    }

    public void remove(int index) {
        model.remove(index);
    }

}

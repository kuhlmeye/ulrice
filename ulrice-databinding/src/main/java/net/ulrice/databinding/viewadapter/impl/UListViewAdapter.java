package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.ListAM;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

public class UListViewAdapter extends AbstractViewAdapter implements ListModel, ListDataListener {

    private JList listComponent;
    private ListAM model;

    public UListViewAdapter(JList listComponent, ListAM model, Class viewType, IFAttributeInfo attributeInfo) {
        super(viewType, attributeInfo);
        this.listComponent = listComponent;
        this.model = model;
    }

    @Override
    public Object getValue() {
        return model.getCurrentValue();
    }

    @Override
    public Object getDisplayedValue() {
        return model.getCurrentValue();
    }

    @Override
    public JList getComponent() {
        return listComponent;
    }

    @Override
    public int getSize() {
        return listComponent.getComponentCount();
    }

    @Override
    public Object getElementAt(int index) {
        if (getSize() > index) {
            return listComponent.getModel().getElementAt(index);
        }
        return null;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listComponent.getModel().addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listComponent.getModel().removeListDataListener(l);

    }

    @Override
    protected void setEditableInternal(boolean editable) {
        listComponent.setEnabled(editable);
    }

    @Override
    protected void addComponentListener() {
        listComponent.getModel().addListDataListener(this);

    }

    @Override
    protected void setValue(Object value) {
        ((ListAM) listComponent.getModel()).add(value);
    }

    @Override
    protected void removeComponentListener() {
        listComponent.getModel().removeListDataListener(this);

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

    public ListAM getModel() {
        return model;
    }
}

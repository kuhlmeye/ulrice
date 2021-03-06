package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JComboBoxViewAdapter<M> extends AbstractViewAdapter <M, ObjectWithPresentation<M>> implements ItemListener {
    private final JComboBox comboBox;
    private final PresentationProvider<M> presentationProvider;

    public JComboBoxViewAdapter (JComboBox combo, IFAttributeInfo attributeInfo, PresentationProvider<M> presentationProvider) {
        super (ObjectWithPresentation.class, attributeInfo);
        this.presentationProvider = presentationProvider;
        comboBox = combo;        
        comboBox.addItemListener(this);
        setEditable(comboBox.isEnabled());
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getValue () {
        return comboBox.getSelectedItem() == null ? null : ((ObjectWithPresentation<M>) comboBox.getSelectedItem()).getValue();
    }
    
    @Override
    protected void setEditableInternal(boolean editable) {
        comboBox.setEnabled(editable);
    }

	@Override
	protected void setValue(M value) {
    	comboBox.setSelectedItem (new ObjectWithPresentation<M>(value, presentationProvider.getPresentation(value)));
	}

	@Override
	public JComboBox getComponent() {
		return comboBox;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
	    // Fire only event for SELECTED, not for DESELECTED
	    if (e.getStateChange() == ItemEvent.SELECTED) {
	        fireViewChange();
	    }
	}

	@Override
	protected void addComponentListener() {
		comboBox.addItemListener(this);
	}

	@Override
	protected void removeComponentListener() {
		comboBox.removeItemListener(this);
	}

    @Override
    public Object getDisplayedValue() {
        return comboBox.getSelectedItem();
    }
}

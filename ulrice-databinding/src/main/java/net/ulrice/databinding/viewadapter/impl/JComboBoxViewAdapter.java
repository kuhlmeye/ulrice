package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JComboBoxViewAdapter<M> extends AbstractViewAdapter <M, ObjectWithPresentation<M>> implements ItemListener {
    private final JComboBox comboBox;
    private final PresentationProvider<M> presentationProvider;

    public JComboBoxViewAdapter (JComboBox combo, PresentationProvider<M> presentationProvider) {
        super (ObjectWithPresentation.class);
        this.presentationProvider = presentationProvider;
        comboBox = combo;        
        comboBox.addItemListener(this);
        setEditable(isComponentEnabled());
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getValue () {
        return comboBox.getSelectedItem() == null ? null : ((ObjectWithPresentation<M>) comboBox.getSelectedItem()).getValue();
    }

	@Override
	protected void setValue(M value) {
    	comboBox.setSelectedItem (new ObjectWithPresentation<M>(value, presentationProvider.getPresentation(value)));
	}

	@Override
    public void setComponentEnabled (boolean enabled) {
        comboBox.setEnabled (enabled);
    }

	@Override
	public JComboBox getComponent() {
		return comboBox;
	}

	@Override
	public boolean isComponentEnabled() {
		return comboBox.isEnabled();
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
}

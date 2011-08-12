package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JComboBoxViewAdapter<M> extends AbstractViewAdapter <M, ObjectWithPresentation<M>> implements ItemListener {
    private final JComboBox comboBox;
    private final PresentationProvider<M> presentationProvider;

    public JComboBoxViewAdapter(PresentationProvider<M> presentationProvider) {
    	this(new JComboBox(), presentationProvider);
    }
    
    public JComboBoxViewAdapter (JComboBox combo, PresentationProvider<M> presentationProvider) {
        super (ObjectWithPresentation.class);
        this.presentationProvider = presentationProvider;
        comboBox = combo;        
        comboBox.addItemListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getValue () {
        return ((ObjectWithPresentation<M>) comboBox.getSelectedItem()).getValue();
    }

	@Override
	protected void setValue(M value) {
    	comboBox.setSelectedItem (new ObjectWithPresentation<M>(value, presentationProvider.getPresentation(value)));
	}

	@Override
    public void setEnabled (boolean enabled) {
        comboBox.setEnabled (enabled);
    }

	@Override
	public JComboBox getComponent() {
		return comboBox;
	}

	@Override
	public boolean isEnabled() {
		return comboBox.isEnabled();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		fireViewChange();
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

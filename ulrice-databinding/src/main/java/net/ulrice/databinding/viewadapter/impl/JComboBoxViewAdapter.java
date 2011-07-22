package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JComboBoxViewAdapter extends AbstractViewAdapter implements ItemListener {
    private final JComboBox comboBox;

    public JComboBoxViewAdapter() {
    	this(new JComboBox());
    }
    
    public JComboBoxViewAdapter (JComboBox combo) {
        super (String.class);
        comboBox = combo;        
        comboBox.addItemListener(this);
    }

    @Override
    public Object getValue () {
        return viewToModel(((ObjectWithPresentation) comboBox.getSelectedItem()).getValue());
    }

	@Override
	protected void setValue(Object value) {
    	comboBox.setSelectedItem (new ObjectWithPresentation(modelToView(value), ""));
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

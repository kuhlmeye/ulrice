package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.ReflectiveObjectWithPresentationConverter;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JComboBoxViewAdapter<M> extends AbstractViewAdapter <M, ObjectWithPresentation<M>> implements ItemListener {
    private final JComboBox comboBox;

    public JComboBoxViewAdapter() {
    	this(new JComboBox());
    }
    
    public JComboBoxViewAdapter (JComboBox combo) {
        super (ObjectWithPresentation.class);
        comboBox = combo;        
        comboBox.addItemListener(this);
    }

    public JComboBoxViewAdapter (JComboBox combo, Class<M> modelClass, String attributeToDisplay) {
        super (ObjectWithPresentation.class);
        comboBox = combo;        
        comboBox.addItemListener(this);
        setValueConverter(new ReflectiveObjectWithPresentationConverter<M> (modelClass, attributeToDisplay));
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getValue () {
        return viewToModel(((ObjectWithPresentation<M>) comboBox.getSelectedItem()));
    }

	@Override
	protected void setValue(M value) {
    	comboBox.setSelectedItem (new ObjectWithPresentation<M>(value, String.valueOf(modelToView(value))));
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

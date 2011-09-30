package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JCheckBoxViewAdapter extends AbstractViewAdapter implements ActionListener {
    private final JCheckBox checkBox;
    
    public JCheckBoxViewAdapter (JCheckBox checkBox) {
        super (Boolean.class);        
        this.checkBox = checkBox;        
        this.checkBox.addActionListener (this);
    }

	@Override
	public JCheckBox getComponent() {
		return checkBox;
	}

    public Object getValue () {
        return viewToModel(checkBox.isSelected());
    }

	@Override
	protected void setValue(Object value) {
		checkBox.setSelected((Boolean)modelToView(value));
	}

    public void setEnabled (boolean enabled) {
        checkBox.setEnabled (enabled);
    }
    
	@Override
	public boolean isEnabled() {
		return checkBox.isEnabled();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		fireViewChange ();
	}

	@Override
	protected void addComponentListener() {
		checkBox.addActionListener(this);
	}

	@Override
	protected void removeComponentListener() {
		checkBox.removeActionListener(this);
	}

}

package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JCheckBoxViewAdapter extends AbstractViewAdapter implements ActionListener {
    private final JCheckBox checkBox;
    
    public JCheckBoxViewAdapter (JCheckBox checkBox, IFAttributeInfo attributeInfo) {
        super (Boolean.class, attributeInfo);        
        this.checkBox = checkBox;        
        this.checkBox.addActionListener (this);
        setEditable(checkBox.isEnabled());
    }

	@Override
	public JCheckBox getComponent() {
		return checkBox;
	}

    public Object getValue () {
        return viewToModel(checkBox.isSelected());
    }
    
    @Override
    protected void setEditableInternal(boolean editable) {
        checkBox.setEnabled(editable);
    }

	@Override
	protected void setValue(Object value) {
		Boolean modelBoolean = (Boolean)modelToView(value);
		if(modelBoolean != null) {
		    checkBox.setSelected(modelBoolean);
		} else {
		    // Default handling. Checkbox is not selected, if model not filled.
		    checkBox.setSelected(false);
		}
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

    @Override
    public Object getDisplayedValue() {
        return checkBox.isSelected();
    }

}

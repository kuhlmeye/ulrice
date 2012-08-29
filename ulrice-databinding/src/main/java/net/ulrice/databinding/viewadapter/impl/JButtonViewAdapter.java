package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JButton;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JButtonViewAdapter extends AbstractViewAdapter {
    private final JButton button;
    
    public JButtonViewAdapter (JButton button, IFAttributeInfo attributeInfo) {
        super (String.class, attributeInfo);
        this.button = button;        
        setEditable(button.isEnabled());
    }
    
    @Override
    protected void setEditableInternal(boolean editable) {
        button.setEnabled(editable);
    }

	@Override
    public Object getValue () {
        return viewToModel(button.getText ());
    }

	@Override
	protected void setValue(Object value) {
		button.setText((String)modelToView(value));
	}

	@Override
	public JButton getComponent() {
		return button;
	}

	@Override
	protected void addComponentListener() {
	}


	@Override
	protected void removeComponentListener() {
	}

    @Override
    public Object getDisplayedValue() {
        return button.getText();
    }
}



package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JButton;

import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JButtonViewAdapter extends AbstractViewAdapter {
    private final JButton button;
    
    public JButtonViewAdapter (JButton button) {
        super (String.class);
        this.button = button;        
        setEditable(isComponentEnabled());
    }

	@Override
    public Object getValue () {
        return viewToModel(button.getText ());
    }

	@Override
	protected void setValue(Object value) {
		button.setText((String)modelToView(value));
	}
	
	public void setComponentEnabled (boolean enabled) {
        button.setEnabled (enabled);
    }

	@Override
	public boolean isComponentEnabled() {
		return button.isEnabled();
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



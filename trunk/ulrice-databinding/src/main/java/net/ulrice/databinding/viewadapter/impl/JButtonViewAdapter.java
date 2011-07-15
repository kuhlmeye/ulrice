package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JButton;
import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JButtonViewAdapter extends AbstractViewAdapter {
    private final JButton button;
    
    public JButtonViewAdapter (JButton button) {
        super (String.class);
        this.button = button;
    }

	@Override
    public Object getValue () {
        return viewToModel(button.getText ());
    }

	@Override
	protected void setValue(Object value) {
		button.setText((String)modelToView(value));
	}
	
	public void setEnabled (boolean enabled) {
        button.setEnabled (enabled);
    }

	@Override
	public boolean isEnabled() {
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
}



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

    public Object getValue () {
        return button.getText ();
    }

    public void setEnabled (boolean enabled) {
        button.setEnabled (enabled);
    }

	@Override
	public boolean isEnabled() {
		return button.isEnabled();
	}

	@Override
	public void updateBinding(IFBinding binding) {
		if(!isInNotification() && !isBindWithoutValue()) {
			button.setText((String)binding.getCurrentValue());
		}
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, button);
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, button);
		}
	}

	@Override
	public JButton getComponent() {
		return button;
	}
}



package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JCheckBoxViewAdapter extends AbstractViewAdapter implements ActionListener {
    private final JCheckBox checkBox;
    
    public JCheckBoxViewAdapter (JCheckBox checkBox) {
        super (Boolean.class);        
        this.checkBox = checkBox;        
        this.checkBox.addActionListener (this);
    }


	@Override
	public void updateBinding(IFBinding binding) {
		if(!isInNotification()) {
	        this.checkBox.removeActionListener (this);
			checkBox.setSelected((Boolean)binding.getCurrentValue());
	        this.checkBox.addActionListener (this);
		}
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, checkBox);
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, checkBox);
		}
	}

	@Override
	public JCheckBox getComponent() {
		return checkBox;
	}

    public Object getValue () {
        return checkBox.isSelected ();
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

}

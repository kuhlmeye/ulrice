package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;


public class JComboBoxViewAdapter extends AbstractViewAdapter implements ItemListener {
    private final JComboBox _combo;
//    private Color _rememberedColor;
//    private String _rememberedTooltip;
//    
//    private boolean _isValid = true;
    
    public JComboBoxViewAdapter (JComboBox combo) {
        super (String.class);
        _combo = combo;        
        _combo.addItemListener(this);
    }

    @Override
    public Object getValue () {
        return ((ObjectWithPresentation) _combo.getSelectedItem()).getValue();
    }


    public void setEnabled (boolean enabled) {
        _combo.setEnabled (enabled);
    }

	@Override
	public void updateBinding(IFBinding binding) {
		if(!isInNotification()) {
	    	_combo.setSelectedItem (new ObjectWithPresentation(binding.getCurrentValue(), ""));
		}
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, _combo);
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, _combo);
		}
	}

	@Override
	public JComponent getComponent() {
		return _combo;
	}

	@Override
	public boolean isEnabled() {
		return _combo.isEnabled();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		fireViewChange();
	}
}

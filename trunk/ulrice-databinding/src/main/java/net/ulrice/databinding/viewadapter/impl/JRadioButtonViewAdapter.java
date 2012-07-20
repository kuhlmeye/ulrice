package net.ulrice.databinding.viewadapter.impl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComponent;
import javax.swing.JRadioButton;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

public class JRadioButtonViewAdapter<M> extends AbstractViewAdapter<M, Boolean> implements ItemListener
{
	
	private final JRadioButton radioButton;
	private final M value;
	
	public JRadioButtonViewAdapter(JRadioButton radioButton, IFAttributeInfo attributeInfo, M value) {
		super(Boolean.class, attributeInfo);
		this.radioButton = radioButton;
		this.value = value;
		radioButton.addItemListener(this);
        setEditable(isComponentEnabled());
	}
	
	@Override
	public boolean isUseAutoValueConverter() {
		return false;
	}

	@Override
	public M getValue() {
		return value;
	}

	@Override
	protected void setValue(M value) {
		if ( value != null && this.value == value) {
			radioButton.setSelected(true);
		}
		else {
			radioButton.setSelected(false);
		}
	}

	@Override
	public JComponent getComponent() {
		return radioButton;
	}

	@Override
	public void setComponentEnabled(boolean enabled) {
		radioButton.setEnabled(enabled);
	}

	@Override
	public boolean isComponentEnabled() {
		return radioButton.isEnabled();
	}

	@Override
	protected void addComponentListener() {
		radioButton.addItemListener(this);
	}

	@Override
	protected void removeComponentListener() {
		radioButton.removeItemListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			fireViewChange();
		}
	}

    @Override
    public Object getDisplayedValue() {
        return radioButton.getText();
    }

}

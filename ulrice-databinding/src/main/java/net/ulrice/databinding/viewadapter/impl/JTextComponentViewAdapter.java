/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;
import net.ulrice.databinding.viewadapter.IFStateMarker;
import net.ulrice.databinding.viewadapter.IFTooltipHandler;

/**
 * Gui accessor for text fields.
 * 
 * @author christof
 */
public class JTextComponentViewAdapter extends AbstractViewAdapter implements DocumentListener {

	
	private JTextComponent textComponent;


	public JTextComponentViewAdapter(JTextComponent textComponent) {
		super(String.class);
		this.textComponent = textComponent;
		textComponent.getDocument().addDocumentListener(this);
	}
	
	@Override
	public void updateBinding(IFBinding binding) {
		if(!isInNotification()) {
			textComponent.getDocument().removeDocumentListener(this);
			textComponent.setText((String)binding.getCurrentValue());
			textComponent.getDocument().addDocumentListener(this);
		}
		if(getTooltipHandler() != null) {
			getTooltipHandler().updateTooltip(binding, textComponent);
		}
		if(getStateMarker() != null) {
			getStateMarker().updateState(binding, textComponent);
		}
	}
	


	@Override
	public Object getValue() {
		return textComponent.getText();
	}

	@Override
	public JComponent getComponent() {
		return textComponent;
	}

	@Override
	public void setEnabled(boolean enabled) {
		textComponent.setEnabled(enabled);
	}


    @Override
    public void changedUpdate(DocumentEvent e) {
    	fireViewChange();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
    	fireViewChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    	fireViewChange();
    }

	@Override
	public boolean isEnabled() {
		return textComponent.isEnabled();
	}

}

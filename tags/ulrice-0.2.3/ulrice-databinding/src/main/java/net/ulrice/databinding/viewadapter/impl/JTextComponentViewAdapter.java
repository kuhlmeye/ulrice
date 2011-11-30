/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.ulrice.databinding.ui.BindingUI;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

/**
 * Gui accessor for text fields.
 * 
 * @author christof
 */
public class JTextComponentViewAdapter extends AbstractViewAdapter implements DocumentListener {

	
	private JTextComponent textComponent;
	private boolean convertEmptyToNull = true;
	private boolean enableSelectionIfComponentDisabled = false;

	public JTextComponentViewAdapter(JTextComponent textComponent) {
	    
		super(String.class);
		
		this.enableSelectionIfComponentDisabled = BindingUI.getBoolean(BindingUI.MARKABLE_DURING_DISABLED_STATE, Boolean.FALSE);

		this.textComponent = textComponent;
        setEditable(isComponentEnabled());
        textComponent.getDocument().addDocumentListener(this);
        setEditable(isComponentEnabled());
	}
	
	@Override
	public Object getValue() {
		String text = textComponent.getText();
		// Convert empty to null, if flagged.
		text = text != null && "".equals(text) && isConvertEmptyToNull() ? null : text;

		return viewToModel(text);
	}

	@Override
	protected void setValue(Object value) {
		textComponent.setText((String)modelToView(value));
	}

	@Override
	public JTextComponent getComponent() {
		return textComponent;
	}
	
	public void setEnableSelectionIfComponentDisabled(boolean enableSelectionIfComponentDisabled) {
        this.enableSelectionIfComponentDisabled = enableSelectionIfComponentDisabled;
    }
	
	public boolean isEnableSelectionIfComponentDisabled() {
        return enableSelectionIfComponentDisabled;
    }

	@Override
	public void setComponentEnabled(boolean enabled) {
            if(isEnableSelectionIfComponentDisabled()) {
    	        textComponent.setEditable(enabled);
    	        textComponent.setEnabled(true);
            } else {
                textComponent.setEnabled(enabled);
            }
	}
	
	@Override
	public boolean isComponentEnabled() {
        if(isEnableSelectionIfComponentDisabled()) {
            return textComponent.isEditable();
        } else {
            return textComponent.isEnabled();
        }
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
	protected void addComponentListener() {
		textComponent.getDocument().removeDocumentListener(this);
	}

	@Override
	protected void removeComponentListener() {
		textComponent.getDocument().addDocumentListener(this);	
	}

	public void setConvertEmptyToNull(boolean convertEmptyToNull) {
		this.convertEmptyToNull = convertEmptyToNull;
	}
	
	public boolean isConvertEmptyToNull() {
		return convertEmptyToNull;
	}
}

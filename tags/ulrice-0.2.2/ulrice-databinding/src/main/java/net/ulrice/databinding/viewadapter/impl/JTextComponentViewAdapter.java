/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

/**
 * Gui accessor for text fields.
 * 
 * @author christof
 */
public class JTextComponentViewAdapter extends AbstractViewAdapter implements DocumentListener {

	
	private JTextComponent textComponent;
	private boolean convertEmptyToNull = true;

	public JTextComponentViewAdapter() {
		this(new JTextField());
	}

	public JTextComponentViewAdapter(JTextComponent textComponent) {
		super(String.class);
		this.textComponent = textComponent;
		textComponent.getDocument().addDocumentListener(this);
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

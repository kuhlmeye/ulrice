/**
 * 
 */
package net.ulrice.databinding.viewadapter.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;

/**
 * Gui accessor for text fields.
 * 
 * @author christof
 */
public class JTextComponentViewAdapter extends AbstractViewAdapter implements DocumentListener, PropertyChangeListener {

	
	private JTextComponent textComponent;
	private boolean convertEmptyToNull = true;

	public JTextComponentViewAdapter(JTextComponent textComponent, IFAttributeInfo attributeInfo) {
		super(String.class, attributeInfo);

		this.textComponent = textComponent;
        setEditable(textComponent.isEditable());
        textComponent.getDocument().addDocumentListener(this);
        textComponent.addPropertyChangeListener(this);
        setEditable(textComponent.isEditable());
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
    protected void setEditableInternal(boolean editable) {
        textComponent.setEditable(editable);
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
	    textComponent.getDocument().addDocumentListener(this);
	}

	@Override
	protected void removeComponentListener() {
	    textComponent.getDocument().removeDocumentListener(this);
	}
	
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("editable")) {
            setEditable((Boolean) evt.getNewValue());
        }
    }

	public void setConvertEmptyToNull(boolean convertEmptyToNull) {
		this.convertEmptyToNull = convertEmptyToNull;
	}
	
	public boolean isConvertEmptyToNull() {
		return convertEmptyToNull;
	}

    @Override
    public Object getDisplayedValue() {
        return textComponent.getText();
    }

}

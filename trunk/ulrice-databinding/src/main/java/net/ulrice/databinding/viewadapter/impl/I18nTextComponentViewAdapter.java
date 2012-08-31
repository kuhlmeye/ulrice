package net.ulrice.databinding.viewadapter.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.viewadapter.AbstractViewAdapter;
import net.ulrice.ui.components.I18nTextComponent;

/**
 * Gui accessor for i18n text fields.
 *
 * @author christof
 */
@SuppressWarnings("rawtypes")
public class I18nTextComponentViewAdapter extends AbstractViewAdapter implements DocumentListener, PropertyChangeListener {


	private I18nTextComponent textComponent;

	public I18nTextComponentViewAdapter(I18nTextComponent textComponent, IFAttributeInfo attributeInfo) {
		super(Map.class, attributeInfo);

		this.textComponent = textComponent;
        setEditable(textComponent.isEnabled());
        textComponent.addDocumentListener(this);
        textComponent.addPropertyChangeListener(this);
        setEditable(textComponent.isEnabled());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue() {
		Map<Locale, String> data = textComponent.getData();
		return viewToModel(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object value) {
        // TODO quickfix
        if (value == null) {
            value = new HashMap<Locale, String>();
        }
		textComponent.setData((Map<Locale, String>) modelToView(value));
	}

	@Override
	public I18nTextComponent getComponent() {
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
		textComponent.addDocumentListener(this);
	}

	@Override
	protected void removeComponentListener() {
		textComponent.removeDocumentListener(this);
	}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("editable")) {
            setEditable((Boolean) evt.getNewValue());
        }
    }

    @Override
    public Object getDisplayedValue() {
        return textComponent.getData();
    }
}

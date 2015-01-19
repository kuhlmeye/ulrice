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
import net.ulrice.ui.components.I18nTextPane;

/**
 * Gui accessor for i18n text pane fields.
 *
 */
@SuppressWarnings("rawtypes")
public class I18nTextPaneViewAdapter extends AbstractViewAdapter implements DocumentListener, PropertyChangeListener {

	private I18nTextPane textPane;

	public I18nTextPaneViewAdapter(I18nTextPane textPane, IFAttributeInfo attributeInfo) {
		super(Map.class, attributeInfo);
		this.textPane = textPane;
        setEditable(textPane.isEnabled());
        textPane.addDocumentListener(this);
        textPane.addPropertyChangeListener(this);
        setEditable(textPane.isEnabled());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue() {
		Map<Locale, String> data = textPane.getData();
		return viewToModel(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object value) {
        // TODO quickfix
        if (value == null) {
            value = new HashMap<Locale, String>();
        }
		textPane.setData((Map<Locale, String>) modelToView(value));
	}

	@Override
	public I18nTextPane getComponent() {
		return textPane;
	}

	@Override
	protected void setEditableInternal(boolean editable) {
	    textPane.setEditable(editable);
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
		textPane.addDocumentListener(this);
	}

	@Override
	protected void removeComponentListener() {
		textPane.removeDocumentListener(this);
	}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("editable")) {
            setEditable((Boolean) evt.getNewValue());
        }
    }

    @Override
    public Object getDisplayedValue() {
        return textPane.getData();
    }
}

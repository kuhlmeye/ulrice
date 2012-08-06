package net.ulrice.databinding.viewadapter.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.ui.BindingUI;
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
	private boolean enableSelectionIfComponentDisabled = false;

	public I18nTextComponentViewAdapter(I18nTextComponent textComponent, IFAttributeInfo attributeInfo) {	    
		super(Map.class, attributeInfo);
		
		this.enableSelectionIfComponentDisabled = BindingUI.getBoolean(BindingUI.MARKABLE_DURING_DISABLED_STATE, Boolean.FALSE);

		this.textComponent = textComponent;
        setEditable(isComponentEnabled());
        textComponent.addDocumentListener(this);
        textComponent.addPropertyChangeListener(this);
        setEditable(isComponentEnabled());
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
		textComponent.setData((Map<Locale, String>) modelToView(value));
	}

	@Override
	public I18nTextComponent getComponent() {
		return textComponent;
	}
	
	@Override
	protected void onSetEditable(boolean editable) {
	    textComponent.setEditable(editable);
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
		textComponent.removeDocumentListener(this);
	}

	@Override
	protected void removeComponentListener() {
		textComponent.addDocumentListener(this);	
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

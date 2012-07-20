package net.ulrice.databinding.bufferedbinding.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.ulrice.databinding.UlriceDatabinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewChangeListener;
import net.ulrice.databinding.viewadapter.impl.I18nTextComponentViewAdapter;
import net.ulrice.ui.components.LocaleSelectorItem;

public class I18nTextAM implements IFAttributeModel<Map<Locale, String>>, IFViewChangeListener {

	private String id;
	@SuppressWarnings("rawtypes")
	private IFModelValueAccessor modelAccessor;
	private IFAttributeInfo attributeInfo;
	private boolean readOnly;
	
    private List<IFValidator<Map<Locale, String>>> validators = new ArrayList<IFValidator<Map<Locale, String>>>();
	private List<IFViewAdapter<Map<Locale, String>, ?>> viewAdapterList = new ArrayList<IFViewAdapter<Map<Locale, String>, ?>>();

    private EventListenerList listenerList = new EventListenerList();

    private LocaleSelectorItem[] localeItems = new LocaleSelectorItem[0];

    @SuppressWarnings("rawtypes")
	private IFValueConverter valueConverter;
	
    private List<ValidationError> externalValidationErrors = new LinkedList<ValidationError>();
   
	private Map<Locale, String> originalValue;
	private Map<Locale, String> currentValue;
	
	private boolean initialized = false;
	private boolean dirty = false;
	private boolean valid = true;

	public I18nTextAM(String id) {
		this.id = id;
	}
	
	public I18nTextAM(IFModelValueAccessor<?> modelAccessor, IFAttributeInfo attributeInfo) {
		this.modelAccessor = modelAccessor;
		this.attributeInfo = attributeInfo;
		this.id = modelAccessor.getAttributeId();		
	}

	public I18nTextAM(String id, IFAttributeInfo attributeInfo, boolean readOnly) {
		this.id = id;
		this.attributeInfo = attributeInfo;
		this.readOnly = readOnly;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void viewValueChanged(IFViewAdapter viewAdapter) {
        gaChanged(viewAdapter, (Map<Locale, String>) viewAdapter.getValue());
	}

    @SuppressWarnings("unchecked")
	@Override
    public void addViewAdapter(@SuppressWarnings("rawtypes") IFViewAdapter viewAdapter) {
        Class< ?> modelType = null;

		if(viewAdapter instanceof I18nTextComponentViewAdapter) {
			I18nTextComponentViewAdapter i18nVA = (I18nTextComponentViewAdapter) viewAdapter;
			i18nVA.getComponent().setAvailableLocales(localeItems);
		}
        
        if (getValueConverter() != null) {
            modelType = getValueConverter().getViewType(modelType);
        }
        else {
            modelType = modelAccessor != null ? modelAccessor.getModelType() : null;
        }

        if (modelType != null
            && viewAdapter.isUseAutoValueConverter()
            && (viewAdapter.getValueConverter() == null || viewAdapter.getValueConverter().equals(
                DoNothingConverter.INSTANCE))) {
            viewAdapter.setValueConverter(UlriceDatabinding.getConverterFactory().createConverter(
                viewAdapter.getViewType(), modelType));
        }

        viewAdapterList.add(viewAdapter);
        viewAdapter.addViewChangeListener(this);
        viewAdapter.bind(this);
    }

    @Override
    public void removeViewAdapter(@SuppressWarnings("rawtypes") IFViewAdapter viewAdapter) {
        viewAdapterList.remove(viewAdapter);
        viewAdapter.removeViewChangeListener(this);
        viewAdapter.detach(this);
    }

	
	public void setAvailableLocales(LocaleSelectorItem... localeItems) {
		if(localeItems == null) {
			throw new IllegalArgumentException("LocaleItems must not be null.");
		}
		
		this.localeItems = localeItems;
		if(viewAdapterList != null) {
			for(@SuppressWarnings("rawtypes") IFViewAdapter viewAdapter : viewAdapterList) {
				if(viewAdapter instanceof I18nTextComponentViewAdapter) {
					I18nTextComponentViewAdapter i18nVA = (I18nTextComponentViewAdapter) viewAdapter;
					i18nVA.getComponent().setAvailableLocales(localeItems);
				}
			}
		}		
	}

    
	@Override
	public Map<Locale, String> getCurrentValue() {
		return currentValue;
	}

	@Override
	public Map<Locale, String> getOriginalValue() {
		return originalValue;
	}

	@Override
    public void read() {
        this.initialized = true;
        if (modelAccessor == null) {
            throw new IllegalStateException("No data accessor is available.");
        }

        Object value = modelAccessor.getValue();
        @SuppressWarnings("unchecked")
		Map<Locale, String> converted = (Map<Locale, String>) (getValueConverter() != null ? getValueConverter().modelToView(value, attributeInfo) : value);
        directRead(converted);
    }

    public void directRead(Map<Locale, String> value) {
    	this.originalValue = new HashMap<Locale, String>();
    	for(LocaleSelectorItem item : localeItems) {
    		this.originalValue.put(item.getLocale(), value.get(item.getLocale()));
    	}
    	
        setCurrentValue(value);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void write() {
        if (modelAccessor == null) {
            throw new IllegalStateException("No data accessor is available.");
        }

        if (!modelAccessor.isReadOnly()) {
            Map<Locale, String> value = directWrite();
            modelAccessor.setValue(getValueConverter() != null ? getValueConverter().viewToModel(value, attributeInfo) : value);
        }
    }
    
    public Map<Locale, String> directWrite() {
    	Map<Locale, String> result = new HashMap<Locale, String>();
		for(LocaleSelectorItem localeItem : localeItems) {
			if(currentValue.containsKey(localeItem.getLocale())) {
				String value = currentValue.get(localeItem.getLocale());
				if(value != null && !"".equals(value)) {
					result.put(localeItem.getLocale(), value);
				} else {
					result.remove(localeItem.getLocale());
				}
			}
		}	
    	
        return result;
    }

	@SuppressWarnings("unchecked")
	@Override
	public void gaChanged(@SuppressWarnings("rawtypes") IFViewAdapter viewAdapter, Map<Locale, String> value) {
        setCurrentValueIntern(value);
        calculateState(viewAdapter);
        fireDataChanged(viewAdapter);
	}
	
	public void setCurrentValue(Map<Locale, String> value) {
        setCurrentValueIntern(value);
        calculateState(null);
        fireDataChanged(null);
    }
	
	private void setCurrentValueIntern(Map<Locale, String> value) {
        this.initialized = true;
        clearExternalValidationErrors();

        this.currentValue= new HashMap<Locale, String>();
    	for(LocaleSelectorItem item : localeItems) {
    		this.currentValue.put(item.getLocale(), value.get(item.getLocale()));
    	}
    }

	@Override
	public void addValidator(IFValidator<Map<Locale, String>> validator) {
        if (validator == null) {
            return;
        }
		validators.add(validator);
	}

	@Override
	public List<IFValidator<Map<Locale, String>>> getValidators() {
		return validators;
	}
	
    @Override
    public ValidationResult getValidationResult() {
        ValidationResult result = new ValidationResult();
        if (!externalValidationErrors.isEmpty()) {
            result.addValidationErrors(externalValidationErrors);
        }

        if (getValidators() != null) {
            for (IFValidator<?> validator : getValidators()) {
                ValidationResult lastValidationErrors = validator.getLastValidationErrors();
                if (lastValidationErrors != null) {
                    result.addValidationErrors(lastValidationErrors.getValidationErrors());
                }
            }
        }

        return result.isValid() ? null : result;
    }

    @Override
    public List<String> getValidationFailures() {
        return getValidationResult() != null ? getValidationResult().getMessagesByBinding(this) : new ArrayList<String>();
    }

    @Override
    public void addAttributeModelEventListener(IFAttributeModelEventListener<Map<Locale, String>> listener) {
        listenerList.add(IFAttributeModelEventListener.class, listener);
    }

    @Override
    public void removeAttributeModelEventListener(IFAttributeModelEventListener<Map<Locale, String>> listener) {
        listenerList.remove(IFAttributeModelEventListener.class, listener);
    }

	@Override
	public void setValueConverter(@SuppressWarnings("rawtypes") IFValueConverter valueConverter) {
        this.valueConverter = valueConverter;
	}
	
	@SuppressWarnings("rawtypes")
	public IFValueConverter getValueConverter() {
		return valueConverter;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		boolean oldReadOnly = this.readOnly;
        this.readOnly = readOnly;
        if(oldReadOnly != this.readOnly) {
            fireUpdateViews();
        }
    }
	
    @Override
    public void addExternalValidationError(String translatedMessage) {
        addExternalValidationError(new ValidationError(this, translatedMessage, null));
    }

    @Override
    public void addExternalValidationError(ValidationError validationError) {
        boolean wasEmpty = externalValidationErrors.isEmpty();
        externalValidationErrors.add(validationError);
        if(wasEmpty) {
            calculateState(null);
            fireDataChanged(null);
        }
    }

    protected void calculateState(IFViewAdapter<Map<Locale, String>, ?> viewAdapter) {
        boolean stateChanged = false;

        try {
            if (getValidators() != null || !externalValidationErrors.isEmpty()) {
                boolean newValid = true;
                for (IFValidator<Map<Locale, String>> validator : getValidators()) {
                    Object displayedValue = (viewAdapter == null) ? null : viewAdapter.getDisplayedValue();
                    ValidationResult errors = validator.isValid(this, getCurrentValue(), displayedValue);
                    if (errors != null && !errors.isValid()) {
                        newValid &= false;
                    }
                    else {
                        validator.clearValidationErrors();
                        newValid &= true;
                    }
                }
                newValid &= externalValidationErrors.isEmpty();
                stateChanged = (valid != newValid);
                valid = newValid;
            }

            if (getCurrentValue() == null && getOriginalValue() == null) {
                stateChanged |= (dirty != false);
                dirty = false;
            }
            else if (getCurrentValue() != null && getOriginalValue() != null) {
                boolean newDirty = !getCurrentValue().equals(getOriginalValue());
                stateChanged |= (newDirty != dirty);
                dirty = newDirty;
            }
            else {
                stateChanged |= (dirty != true);
                dirty = true;
            }
        }
        finally {
            if (stateChanged) {
                fireStateChanged(viewAdapter);
            }
        }
    }
    
    public void fireDataChanged(final IFViewAdapter<Map<Locale, String>, ?> viewAdapter) {
        @SuppressWarnings("unchecked")
		IFAttributeModelEventListener<Map<Locale, String>>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        
        if (listeners != null) {
            for (final IFAttributeModelEventListener<Map<Locale, String>> listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                listener.dataChanged(viewAdapter, I18nTextAM.this);
                            }
                        });
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    listener.dataChanged(viewAdapter, this);
                }
            }
        }
        fireUpdateViews();
    }

    public void fireStateChanged(final IFViewAdapter<Map<Locale, String>, ?> viewAdapter) {
        @SuppressWarnings("unchecked")
		IFAttributeModelEventListener<Map<Locale, String>>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);

        if (listeners != null) {
            for (final IFAttributeModelEventListener<Map<Locale, String>> listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                listener.stateChanged(viewAdapter, I18nTextAM.this);
                            }
                        });
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    listener.stateChanged(viewAdapter, this);
                }
            }
        }
        fireUpdateViews();
    }

    public void fireUpdateViews() {
        if (viewAdapterList != null) {
            for (final IFViewAdapter<Map<Locale, String>, ?> viewAdapter : viewAdapterList) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                viewAdapter.updateFromBinding(I18nTextAM.this);
                            }
                        });
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    viewAdapter.updateFromBinding(this);
                }
            }
        }
    }


    
    @Override
    public void clearExternalValidationErrors() {
        externalValidationErrors.clear();        
    }
    
	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isDirty() {
		calculateState(null);
		return dirty;
	}

	@Override
	public boolean isReadOnly() {
		return (modelAccessor != null && modelAccessor.isReadOnly() || readOnly);
	}

	@Override
	public boolean isValid() {
		calculateState(null);
		return valid;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public IFAttributeInfo getAttributeInfo() {
		return attributeInfo;
	}	
}

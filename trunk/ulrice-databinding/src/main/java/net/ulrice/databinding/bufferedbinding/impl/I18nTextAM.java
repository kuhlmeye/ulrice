package net.ulrice.databinding.bufferedbinding.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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

	protected EventListenerList listenerList = new EventListenerList();
	private String id;
	private IFAttributeInfo attributeInfo;
	private boolean readOnly;
	private IFModelValueAccessor modelAccessor;
	private boolean initialized = false;

	private List<IFViewAdapter> viewAdapterList = new ArrayList<IFViewAdapter>();

	private Map<Locale, GenericAM<String>> modelMap = new HashMap<Locale, GenericAM<String>>();
	private List<IFValidator<Map<Locale, String>>> validators = new ArrayList<IFValidator<Map<Locale, String>>>();
	private List<ValidationError> externalValidationErrors = new LinkedList<ValidationError>();
	private IFValueConverter valueConverter;
	private LocaleSelectorItem[] localeItems = new LocaleSelectorItem[0];

	public I18nTextAM(IFModelValueAccessor modelAccessor, IFAttributeInfo attributeInfo) {
		this.modelAccessor = modelAccessor;
		this.attributeInfo = attributeInfo;
		this.id = modelAccessor.getAttributeId();
		
	}

	public I18nTextAM(String id, IFAttributeInfo attributeInfo, boolean readOnly) {
		this.id = id;
		this.attributeInfo = attributeInfo;
		this.readOnly = readOnly;
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

	public I18nTextAM(String id) {
		this.id = id;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isDirty() {
		for(GenericAM<String> model : modelMap.values()) {
			if(model.isDirty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isValid() {
		for(GenericAM<String> model : modelMap.values()) {
			if(!model.isValid()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void addViewAdapter(IFViewAdapter viewAdapter) {
		Class<?> modelType = null;
		
		if(viewAdapter instanceof I18nTextComponentViewAdapter) {
			I18nTextComponentViewAdapter i18nVA = (I18nTextComponentViewAdapter) viewAdapter;
			i18nVA.getComponent().setAvailableLocales(localeItems);
		}

		if (getValueConverter() != null) {
			modelType = getValueConverter().getViewType(modelType);
		} else {
			modelType = modelAccessor != null ? modelAccessor.getModelType() : null;
		}

		if (modelType != null && viewAdapter.isUseAutoValueConverter()
				&& (viewAdapter.getValueConverter() == null || viewAdapter.getValueConverter().equals(DoNothingConverter.INSTANCE))) {
			viewAdapter.setValueConverter(UlriceDatabinding.getConverterFactory().createConverter(viewAdapter.getViewType(), modelType));
		}

		viewAdapterList.add(viewAdapter);
		viewAdapter.addViewChangeListener(this);
		viewAdapter.bind(this);
	}

	@Override
	public void removeViewAdapter(IFViewAdapter viewAdapter) {
		viewAdapterList.remove(viewAdapter);
		viewAdapter.removeViewChangeListener(this);
		viewAdapter.detach(this);
	}

	@Override
	public List<String> getValidationFailures() {
		List<String> result = new ArrayList<String>();
		for (GenericAM<String> model : modelMap.values()) {
			result.addAll(getValidationFailures());
		}

		return result;
	}

	@Override
	public void read() {
		if (modelAccessor == null) {
			throw new IllegalStateException("No data accessor is available.");
		}

		Object value = modelAccessor.getValue();
		Map<Locale, String> converted = (Map<Locale, String>) (getValueConverter() != null ? getValueConverter().modelToView(value) : value);
		directRead(converted);
	}

	public void directRead(Map<Locale, String> dataMap) {
		modelMap.clear();
		if (dataMap != null) {
			initialized = true;
			for(LocaleSelectorItem localeItem : localeItems) {
				GenericAM<String> model = new GenericAM<String>(id + localeItem.getLocale().toString());
				if(dataMap.containsKey(localeItem.getLocale())) {
					model.directRead(dataMap.get(localeItem.getLocale()));
				}
				modelMap.put(localeItem.getLocale(), model);
				
				model.addAttributeModelEventListener(new IFAttributeModelEventListener<String>() {

					@Override
					public void dataChanged(IFViewAdapter viewAdapter, IFAttributeModel<String> amSource) {
						fireDataChanged(viewAdapter);
					}

					@Override
					public void stateChanged(IFViewAdapter viewAdapter, IFAttributeModel<String> amSource) {
						fireStateChanged(viewAdapter);
					}
				});
				
			}
		}
		fireUpdateViews();
	}

	@Override
	public Map<Locale, String> getCurrentValue() {
		Map<Locale, String> result = new HashMap<Locale, String>();
		for (Entry<Locale, GenericAM<String>> entry : modelMap.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getCurrentValue());
		}

		return result;
	}

	@Override
	public Map<Locale, String> getOriginalValue() {
		Map<Locale, String> result = new HashMap<Locale, String>();
		for (Entry<Locale, GenericAM<String>> entry : modelMap.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getOriginalValue());
		}

		return result;
	}
	
	@Override
	public void write() {
		if (modelAccessor == null) {
			throw new IllegalStateException("No data accessor is available.");
		}

		if (!modelAccessor.isReadOnly()) {
			Map<Locale, String> value = directWrite();
			Object converted = (getValueConverter() != null ? getValueConverter().viewToModel(value) : value);
			modelAccessor.setValue(converted);
		}
	}

	public Map<Locale, String> directWrite() {
		Map<Locale, String> result = new HashMap<Locale, String>();
		for(LocaleSelectorItem localeItem : localeItems) {
			if(modelMap.containsKey(localeItem.getLocale())) {
				String value = modelMap.get(localeItem.getLocale()).directWrite();
				if(value != null || "".equals(value)) {
					result.put(localeItem.getLocale(), value);
				} else {
					result.remove(localeItem.getLocale());
				}
			}
		}		

		return result;
	}

	@Override
	public void addValidator(IFValidator<Map<Locale, String>> validator) {
		if (validator == null) {
			return;
		}
		this.validators.add(validator);
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
			for (IFValidator<Map<Locale, String>> validator : getValidators()) {
				ValidationResult lastValidationErrors = validator.getLastValidationErrors();
				if (lastValidationErrors != null) {
					result.addValidationErrors(lastValidationErrors.getValidationErrors());
				}
			}

		}

		return result.isValid() ? null : result;
	}

	@Override
	public void addAttributeModelEventListener(IFAttributeModelEventListener<Map<Locale, String>> listener) {
		listenerList.add(IFAttributeModelEventListener.class, listener);
	}

	@Override
	public void removeAttributeModelEventListener(IFAttributeModelEventListener<Map<Locale, String>> listener) {
		listenerList.remove(IFAttributeModelEventListener.class, listener);
	}

    public IFValueConverter getValueConverter() {
        return valueConverter;
    }

    @Override
    public void setValueConverter(IFValueConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    @Override
    public IFAttributeInfo getAttributeInfo() {
        return attributeInfo;
    }

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public void addExternalValidationError(String translatedMessage) {
		addExternalValidationError(new ValidationError(this, translatedMessage, null));
	}

	@Override
	public void addExternalValidationError(ValidationError validationError) {
		boolean wasEmpty = externalValidationErrors.isEmpty();
		externalValidationErrors.add(validationError);
		if (wasEmpty) {
			fireDataChanged(null);
		}
	}

	@Override
	public void clearExternalValidationErrors() {
		externalValidationErrors.clear();
	}

    @Override
    public void viewValueChanged(IFViewAdapter viewAdapter) {
        gaChanged(viewAdapter, (Map<Locale, String>) viewAdapter.getValue());
    }


	@Override
	public void gaChanged(IFViewAdapter viewAdapter, Map<Locale, String> value) {
        setCurrentValueIntern(value);
        fireDataChanged(viewAdapter);
	}
	
    private void setCurrentValueIntern(Map<Locale, String> value) {
    	initialized = true;
        clearExternalValidationErrors();
		for (Entry<Locale, GenericAM<String>> entry : modelMap.entrySet()) {
			entry.getValue().setCurrentValue(value.get(entry.getKey()));
		}
    }
    
    @SuppressWarnings("unchecked")
    public void fireDataChanged(final IFViewAdapter viewAdapter) {
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
    
    @SuppressWarnings("unchecked")
    public void fireStateChanged(final IFViewAdapter viewAdapter) {
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
            for (final IFViewAdapter viewAdapter : viewAdapterList) {
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
}

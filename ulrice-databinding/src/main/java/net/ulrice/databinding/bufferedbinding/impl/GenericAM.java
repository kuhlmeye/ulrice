package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.UlriceDatabinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;
import net.ulrice.databinding.viewadapter.IFViewChangeListener;

/**
 * A generic attribute model.
 * 
 * @author christof
 */
public class GenericAM<T> implements IFAttributeModel<T>, IFViewChangeListener {

	/** The event listener. */
	private EventListenerList listenerList = new EventListenerList();

	/** The data accessor used to write and read the data. */
	private IFModelValueAccessor modelAccessor;

	/** The original value read from the model. */
	private T originalValue;

	/** The current value. */
	private T currentValue;

	/** The validator of this attribute model. */
	private List<IFValidator<T>> validators = new ArrayList<IFValidator<T>>();

	private List<IFViewAdapter> viewAdapterList = new ArrayList<IFViewAdapter>();

	private boolean readOnly = false;

	private IFValueConverter valueConverter;
	
	private IFAttributeInfo attributeInfo;

	private boolean dirty = false;
	private boolean valid = true;
	private boolean initialized = false;

	private String id;

	public GenericAM(String id, IFModelValueAccessor modelAccessor, IFAttributeInfo attributeInfo) {
	    this.modelAccessor = modelAccessor;
        this.attributeInfo = attributeInfo;
        this.id = modelAccessor.getAttributeId();
	}
	
	/**
	 * Creates a new generic attribute model.
	 * 
	 * @param id
	 *            The Identifier.
	 * @param dataAccessor
	 *            The data accessor.
	 */
	public GenericAM(IFModelValueAccessor modelAccessor, IFAttributeInfo attributeInfo) {
	    this(modelAccessor.getAttributeId(), modelAccessor, attributeInfo);
	}

	/**
	 * Creates a new generic attribute model.
	 * 
	 * @param id
	 *            The Identifier.
	 */
	public GenericAM(String id) {
		this.id = id;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getCurrentValue()
	 */
	public T getCurrentValue() {
		return currentValue;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		T tValue = (T) value;
		setCurrentValue(tValue);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setCurrentValue(java.lang.Object)
	 */
	public void setCurrentValue(T value) {
		this.initialized = true;
		this.currentValue = value;
		calculateState(null);
		fireDataChanged(null);
	}
	
	public void recalculateState() {
	    calculateState(null);	    
	}

	public void gaChanged(IFViewAdapter viewAdapter, T value) {
		this.currentValue = value;
		calculateState(viewAdapter);
		fireDataChanged(viewAdapter);
	}

	/**
	 * 
	 */
	private void calculateState(IFViewAdapter viewAdapter) {
		boolean stateChanged = false;

		try {
			if (getValidators() != null) {
			    for(IFValidator<T> validator : getValidators()) {
    				ValidationResult errors = validator.isValid(this, getCurrentValue());
    				if (errors != null && !errors.isValid()) {
    					stateChanged |= (valid != false);
    					valid = false;
    				} else {
    				    validator.clearValidationErrors();
    					stateChanged |= (valid != true);
    					valid = true;
    				}
			    }
			}

			if (getCurrentValue() == null && getOriginalValue() == null) {
				stateChanged |= (dirty != false);
				dirty = false;
			} else if (getCurrentValue() != null && getOriginalValue() != null) {
				boolean newDirty = !getCurrentValue().equals(getOriginalValue());
				stateChanged |= (newDirty != dirty);
				dirty = newDirty;
			} else {
				stateChanged |= (dirty != true);
				dirty = true;
			}
		} finally {
			if (stateChanged) {
				fireStateChanged(viewAdapter);
			}
		}
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getOriginalValue()
	 */
	public T getOriginalValue() {
		return originalValue;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#read()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void read() {
		this.initialized = true;
		if (modelAccessor == null) {
			throw new IllegalStateException("No data accessor is available.");
		}

		Object value = modelAccessor.getValue();
		T converted = (T) (getValueConverter() != null ? getValueConverter().modelToView(value) : value);
		directRead((T) converted);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#directRead(java.lang.Object)
	 */
	public void directRead(T value) {
		this.originalValue = value;
		setCurrentValue(value);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#write()
	 */
	@Override
	public void write() {
		if (modelAccessor == null) {
			throw new IllegalStateException("No data accessor is available.");
		}

		T value = directWrite();
		Object converted = (getValueConverter() != null ? getValueConverter().viewToModel(value) : value);
		modelAccessor.setValue(converted);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#directWrite()
	 */
	public T directWrite() {
		return currentValue;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidator()
	 */
	@Override
	public List<IFValidator<T>> getValidators() {
		return validators;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValidator(net.ulrice.databinding.validation.IFValidator)
	 */
	@Override
	public void addValidator(IFValidator<T> validator) {
	    if(validator == null) {
	        return;
	    }
		this.validators.add(validator);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidationResult()
	 */
	@Override
	public ValidationResult getValidationResult() {
		if (getValidators() != null) {
		    ValidationResult result = new ValidationResult();
		    for(IFValidator<T> validator : getValidators()) {		        
		        ValidationResult lastValidationErrors = validator.getLastValidationErrors();
		        if(lastValidationErrors != null) {
		            result.addValidationErrors(lastValidationErrors.getValidationErrors());
		        }
		    }
			return result;
		}
		return null;
	}

	public List<String> getValidationFailures() {
		return getValidationResult() != null ? getValidationResult().getMessagesByBinding(this) : new ArrayList<String>();
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#addAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
	 */
	@Override
	public void addAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
		listenerList.add(IFAttributeModelEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#removeAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
	 */
	@Override
	public void removeAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
		listenerList.remove(IFAttributeModelEventListener.class, listener);
	}

	@SuppressWarnings("unchecked")
	public void fireDataChanged(IFViewAdapter viewAdapter) {
		IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
		if (listeners != null) {
			for (IFAttributeModelEventListener<T> listener : listeners) {
				listener.dataChanged(viewAdapter, this);
			}
		}
		fireUpdateViews();
	}

	@SuppressWarnings("unchecked")
	public void fireStateChanged(IFViewAdapter viewAdapter) {
		IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
		if (listeners != null) {
			for (IFAttributeModelEventListener<T> listener : listeners) {
				listener.stateChanged(viewAdapter, this);
			}
		}
		fireUpdateViews();
	}

	public void fireUpdateViews() {
		if (viewAdapterList != null) {
			for (IFViewAdapter viewAdapter : viewAdapterList) {
				viewAdapter.updateFromBinding(this);
			}
		}
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return (modelAccessor == null && readOnly) || (modelAccessor != null && modelAccessor.isReadOnly());
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public void addViewAdapter(IFViewAdapter viewAdapter) {
		Class<?> modelType = modelAccessor != null ? modelAccessor.getModelType() : null;
		if (modelType != null && viewAdapter.isUseAutoValueConverter() && (viewAdapter.getValueConverter() == null || viewAdapter.getValueConverter().equals(DoNothingConverter.INSTANCE))) {
			viewAdapter.setValueConverter(UlriceDatabinding.getConverterFactory().createConverter(viewAdapter.getViewType(), modelType));
		}

		viewAdapterList.add(viewAdapter);
		viewAdapter.addViewChangeListener(this);
		viewAdapter.bind(this);
	}
	
	public void removeViewAdapter(IFViewAdapter viewAdapter) {
	    viewAdapterList.remove(viewAdapter);
	    viewAdapter.removeViewChangeListener(this);
	    viewAdapter.detach(this);
	}

	@Override
	public void viewValueChanged(IFViewAdapter viewAdapter) {
		setCurrentValue((T) viewAdapter.getValue());
	}

	public IFValueConverter getValueConverter() {
		return valueConverter;
	}

	@Override
	public void setValueConverter(IFValueConverter valueConverter) {
		this.valueConverter = valueConverter;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public boolean isDirty() {
		return dirty;
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

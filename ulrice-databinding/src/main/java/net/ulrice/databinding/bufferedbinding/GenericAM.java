package net.ulrice.databinding.bufferedbinding;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFBinding;
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
public class GenericAM<T> implements IFExtdAttributeModel<T>, IFBinding, IFViewChangeListener {

    /** The event listener. */
    private EventListenerList listenerList = new EventListenerList();

    /** The identifier of this generic attribute model. */
    private String id;

    /** The data accessor used to write and read the data. */
    private IFModelValueAccessor modelAccessor;

    /** The state of this attribute model. */
    private DataState state = DataState.NotInitialized;

    /** The original value read from the model. */
    private T originalValue;

    /** The current value. */
    private T currentValue;

    /** The validator of this attribute model. */
    private IFValidator<T> validator;

	private List<IFViewAdapter> viewAdapterList;

    /**
     * Creates a new generic attribute model.
     * 
     * @param id The Identifier.
     * @param dataAccessor The data accessor.
     */
    public GenericAM(String id, IFModelValueAccessor modelAccessor) {
        this.id = id;
        this.modelAccessor = modelAccessor;
        this.viewAdapterList = new ArrayList();
    }
    
    

    /**
     * Creates a new generic attribute model.
     * 
     * @param id The Identifier.
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
        T oldValue = this.currentValue;
        this.currentValue = value;
        calculateState(null);
        fireDataChanged(null, oldValue, getCurrentValue(), getState());
    }

    public void gaChanged(IFViewAdapter viewAdapter, T value) {
        T oldValue = this.currentValue;
        this.currentValue = value;
        calculateState(viewAdapter);
        fireDataChanged(viewAdapter, oldValue, getCurrentValue(), getState());
    }

    /**
	 * 
	 */
    private void calculateState(IFViewAdapter viewAdapter) {
        DataState oldState = state;
        try {
            if (getValidator() != null) {
                ValidationResult errors = getValidator().isValid(this, getCurrentValue());
                if (errors != null) {
                    state = DataState.Invalid;
                    return;
                } else {
                    getValidator().clear();
                }
            }

            if (getCurrentValue() == null && getOriginalValue() == null) {
                state = DataState.NotChanged;
            } else if (getCurrentValue() != null && getOriginalValue() != null) {
                state = getCurrentValue().equals(getOriginalValue()) ? DataState.NotChanged : DataState.Changed;
            } else {
                state = DataState.Changed;
            }
        } finally {
            if (!oldState.equals(state)) {
                fireStateChanged(viewAdapter, oldState, state);
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
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getState()
     */
    @Override
    public DataState getState() {
        return state;
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#read()
     */
    @SuppressWarnings("unchecked")
	@Override
    public void read() {
        if (modelAccessor == null) {
            throw new IllegalStateException("No data accessor is available.");
        }
        directRead((T)modelAccessor.getValue());
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
        modelAccessor.setValue(directWrite());
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
    public IFValidator<T> getValidator() {
        return validator;
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValidator(net.ulrice.databinding.validation.IFValidator)
     */
    @Override
    public void setValidator(IFValidator<T> validator) {
        this.validator = validator;
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidationResult()
     */
    @Override
    public ValidationResult getValidationResult() {
        if(getValidator() != null) {
            return getValidator().getLastValidationErrors();
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
    public void fireDataChanged(IFViewAdapter viewAdapter, T oldValue, T newValue, DataState state) {
        IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (IFAttributeModelEventListener<T> listener : listeners) {
                listener.dataChanged(viewAdapter, this, oldValue, newValue, state);
            }
        }
        fireUpdateViews();
    }

    @SuppressWarnings("unchecked")
    public void fireStateChanged(IFViewAdapter viewAdapter, DataState oldState, DataState newState) {
        IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (IFAttributeModelEventListener<T> listener : listeners) {
                listener.stateChanged(viewAdapter, this, oldState, newState);
            }
        }
        fireUpdateViews();
    }
    
    public void fireUpdateViews() {
    	if(viewAdapterList != null) {
    		for(IFViewAdapter viewAdapter: viewAdapterList) {
    			viewAdapter.updateBinding(this);
    		}
    	}
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return true;
    }



	@Override
	public void addViewAdapter(IFViewAdapter viewAdapter) {
		viewAdapterList.add(viewAdapter);
		viewAdapter.addViewChangeListener(this);
	}



	@Override
	public void viewValueChanged(IFViewAdapter viewAdapter) {
		setCurrentValue((T)viewAdapter.getValue());
	}
}

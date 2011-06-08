package net.ulrice.databinding.impl.am;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFAttributeModelEventListener;
import net.ulrice.databinding.IFDataAccessor;
import net.ulrice.databinding.IFGuiAccessor;
import net.ulrice.databinding.IFValidator;
import net.ulrice.databinding.impl.validation.ValidationErrors;

/**
 * A generic attribute model.
 * 
 * @author christof
 */
public class GenericAM<T> implements IFAttributeModel<T> {

    /** The event listener. */
    private EventListenerList listenerList = new EventListenerList();

    /** The identifier of this generic attribute model. */
    private String id;

    /** The data accessor used to write and read the data. */
    private IFDataAccessor<T> dataAccessor;

    /** The state of this attribute model. */
    private DataState state = DataState.NotInitialized;

    /** The original value read from the model. */
    private T originalValue;

    /** The current value. */
    private T currentValue;

    /** The validator of this attribute model. */
    private IFValidator<T> validator;

    /**
     * Creates a new generic attribute model.
     * 
     * @param id The Identifier.
     * @param dataAccessor The data accessor.
     */
    public GenericAM(String id, IFDataAccessor<T> dataAccessor) {
        this.id = id;
        this.dataAccessor = dataAccessor;
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
     * @see net.ulrice.databinding.IFAttributeModel#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#getCurrentValue()
     */
    @Override
    public T getCurrentValue() {
        return currentValue;
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#setValue(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public void setValue(Object value) {
        T tValue = (T) value;
        setCurrentValue(tValue);
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#setCurrentValue(java.lang.Object)
     */
    public void setCurrentValue(T value) {
        T oldValue = this.currentValue;
        this.currentValue = value;
        calculateState(null);
        fireDataChanged(null, oldValue, getCurrentValue(), getState());
    }

    public void gaChanged(IFGuiAccessor<?, ?> gaSource, T value) {
        T oldValue = this.currentValue;
        this.currentValue = value;
        calculateState(gaSource);
        fireDataChanged(gaSource, oldValue, getCurrentValue(), getState());
    }

    /**
	 * 
	 */
    private void calculateState(IFGuiAccessor<?, ?> gaSource) {
        DataState oldState = state;
        try {
            if (getValidator() != null) {
                ValidationErrors errors = getValidator().validate(this, getCurrentValue());
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
                fireStateChanged(gaSource, oldState, state);
            }
        }
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#getOriginalValue()
     */
    @Override
    public T getOriginalValue() {
        return originalValue;
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#getState()
     */
    @Override
    public DataState getState() {
        return state;
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#read()
     */
    @Override
    public void read() {
        if (dataAccessor == null) {
            throw new IllegalStateException("No data accessor is available.");
        }
        directRead(dataAccessor.readValue());
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#directRead(java.lang.Object)
     */
    @Override
    public void directRead(T value) {
        this.originalValue = value;
        setCurrentValue(value);
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#write()
     */
    @Override
    public void write() {
        if (dataAccessor == null) {
            throw new IllegalStateException("No data accessor is available.");
        }
        dataAccessor.writeValue(directWrite());
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#directWrite()
     */
    @Override
    public T directWrite() {
        return currentValue;
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#getValidator()
     */
    @Override
    public IFValidator<T> getValidator() {
        return validator;
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#setValidator(net.ulrice.databinding.IFValidator)
     */
    @Override
    public void setValidator(IFValidator<T> validator) {
        this.validator = validator;
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#getValidationErrors()
     */
    @Override
    public ValidationErrors getValidationErrors() {
        if(getValidator() != null) {
            return getValidator().getLastValidationErrors();
        }
        return null;
    }


    /**
     * @see net.ulrice.databinding.IFAttributeModel#addAttributeModelEventListener(net.ulrice.databinding.IFAttributeModelEventListener)
     */
    @Override
    public void addAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
        listenerList.add(IFAttributeModelEventListener.class, listener);
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#removeAttributeModelEventListener(net.ulrice.databinding.IFAttributeModelEventListener)
     */
    @Override
    public void removeAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
        listenerList.remove(IFAttributeModelEventListener.class, listener);
    }

    @SuppressWarnings("unchecked")
    public void fireDataChanged(IFGuiAccessor<?, ?> gaSource, T oldValue, T newValue, DataState state) {
        IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (IFAttributeModelEventListener<T> listener : listeners) {
                listener.dataChanged(gaSource, this, oldValue, newValue, state);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void fireStateChanged(IFGuiAccessor<?, ?> gaSource, DataState oldState, DataState newState) {
        IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (IFAttributeModelEventListener<T> listener : listeners) {
                listener.stateChanged(gaSource, this, oldState, newState);
            }
        }
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#isEditable()
     */
    @Override
    public boolean isEditable() {
        return true;
    }
}

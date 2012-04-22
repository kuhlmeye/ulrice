package net.ulrice.databinding.bufferedbinding.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    private List<ValidationError> externalValidationErrors = new LinkedList<ValidationError>();

    public GenericAM(IFModelValueAccessor modelAccessor, IFAttributeInfo attributeInfo) {
        this.modelAccessor = modelAccessor;
        this.attributeInfo = attributeInfo;
        this.id = modelAccessor.getAttributeId();

    }
    
    public GenericAM(String id, IFAttributeInfo attributeInfo, boolean readOnly) {
        this.id = id;
        this.attributeInfo = attributeInfo;
        this.readOnly = readOnly;
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
        setCurrentValueIntern(value);
        calculateState(null);
        fireDataChanged(null);
    }

    private void setCurrentValueIntern(T value) {
        this.initialized = true;
        clearExternalValidationErrors();
        this.currentValue = value;
    }

    public void recalculateState() {
        calculateState(null);
    }

    public void gaChanged(IFViewAdapter viewAdapter, T value) {
        setCurrentValueIntern(value);
        calculateState(viewAdapter);
        fireDataChanged(viewAdapter);
    }
    
    public void recalculateStateForThisValidator(IFValidator caller, boolean newValid) {
        boolean stateChanged = false;

        try {
            if (getValidators() != null || !externalValidationErrors.isEmpty()) {
                for (IFValidator<T> validator : getValidators()) {
                    if (validator.getClass().equals(caller.getClass())) {
                        continue;
                    }
                    ValidationResult errors = validator.isValid(this, getCurrentValue());
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
        }
        finally {
            if (stateChanged) {
                fireStateChanged(null);
            }
        }        
    }

    /**
	 * 
	 */
    private void calculateState(IFViewAdapter viewAdapter) {
        boolean stateChanged = false;

        try {
            if (getValidators() != null || !externalValidationErrors.isEmpty()) {
                boolean newValid = true;
                for (IFValidator<T> validator : getValidators()) {
                    ValidationResult errors = validator.isValid(this, getCurrentValue());
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
                boolean newDirty;
                // XHU: Needed, because it is not allowed to compare BigDecimal values with equals()!
                if (getCurrentValue() instanceof BigDecimal) {
                    newDirty = !(((BigDecimal) getCurrentValue()).compareTo((BigDecimal) getOriginalValue()) == 0);
                }
                else {
                    newDirty = !getCurrentValue().equals(getOriginalValue());
                }
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

        if (!modelAccessor.isReadOnly()) {
            T value = directWrite();
            Object converted = (getValueConverter() != null ? getValueConverter().viewToModel(value) : value);
            modelAccessor.setValue(converted);
        }
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
        if (validator == null) {
            return;
        }
        this.validators.add(validator);
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidationResult()
     */
    @Override
    public ValidationResult getValidationResult() {
        ValidationResult result = new ValidationResult();
        if (!externalValidationErrors.isEmpty()) {
            result.addValidationErrors(externalValidationErrors);
        }

        if (getValidators() != null) {
            for (IFValidator<T> validator : getValidators()) {
                ValidationResult lastValidationErrors = validator.getLastValidationErrors();
                if (lastValidationErrors != null) {
                    result.addValidationErrors(lastValidationErrors.getValidationErrors());
                }
            }

        }

        return result.isValid() ? null : result;
    }

    public List<String> getValidationFailures() {
        return getValidationResult() != null ? getValidationResult().getMessagesByBinding(this)
                : new ArrayList<String>();
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
    public void fireDataChanged(final IFViewAdapter viewAdapter) {
        IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (final IFAttributeModelEventListener<T> listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                listener.dataChanged(viewAdapter, GenericAM.this);
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
        IFAttributeModelEventListener<T>[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if (listeners != null) {
            for (final IFAttributeModelEventListener<T> listener : listeners) {
                if (!SwingUtilities.isEventDispatchThread()) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                listener.stateChanged(viewAdapter, GenericAM.this);
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
                                viewAdapter.updateFromBinding(GenericAM.this);
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

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return (modelAccessor != null && modelAccessor.isReadOnly() || readOnly);
    }

    public void setReadOnly(boolean readOnly) {
        boolean oldReadOnly = this.readOnly;
        this.readOnly = readOnly;
        if(oldReadOnly != this.readOnly) {
            fireUpdateViews();
        }
    }

    @Override
    public void addViewAdapter(IFViewAdapter viewAdapter) {
        Class< ?> modelType = null;

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

    public void removeViewAdapter(IFViewAdapter viewAdapter) {
        viewAdapterList.remove(viewAdapter);
        viewAdapter.removeViewChangeListener(this);
        viewAdapter.detach(this);
    }

    @Override
    public void viewValueChanged(IFViewAdapter viewAdapter) {
        gaChanged(viewAdapter, (T) viewAdapter.getValue());
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

    @Override
    public void clearExternalValidationErrors() {
        externalValidationErrors.clear();
    }

}

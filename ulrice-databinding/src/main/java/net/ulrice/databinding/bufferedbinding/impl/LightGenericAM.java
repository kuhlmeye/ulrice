package net.ulrice.databinding.bufferedbinding.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
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
public class LightGenericAM<T> implements IFAttributeModel<T>, IFViewChangeListener, IFElementInternalAM<T> {

    private String id;

    private T originalValue;
    private T currentValue;
    private List<IFValidator<T>> validators = null;
    private List<ValidationError> externalValidationErrors = null;
    private boolean readOnly = false;
    private boolean dirty = false;
    private boolean valid = true;
    private boolean isListOrderRelevant = false;


    public LightGenericAM(String id, boolean readOnly) {
        this.id = id;
        this.readOnly = readOnly;
    }

    /**
     * Creates a new generic attribute model.
     *
     * @param id The Identifier.
     */
    public LightGenericAM(String id) {
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
    @Override
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
    }

    private void setCurrentValueIntern(T value) {
        clearExternalValidationErrors();
        this.currentValue = value;
    }

    public void recalculateState() {
        calculateState(null);
    }

    @Override
    public void gaChanged(IFViewAdapter viewAdapter, T value) {
        setCurrentValueIntern(value);
        calculateState(viewAdapter);
    }


    /**
	 *
	 */
    protected void calculateState(IFViewAdapter viewAdapter) {

        boolean newValid = true;
        if(getValidators() != null) {
            for (IFValidator<T> validator : getValidators()) {
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
        }
        if(externalValidationErrors != null && !externalValidationErrors.isEmpty()) {
        	newValid &= externalValidationErrors.isEmpty();
        }
        valid = newValid;


        if (getCurrentValue() == null && getOriginalValue() == null) {
            dirty = false;
        }
        else if (readOnly) {
            dirty = false; 
        }
        else if ( getCurrentValue() != null && getOriginalValue() != null) {
            boolean newDirty;
            // XHU: Needed, because it is not allowed to compare BigDecimal values with equals()!
            if (getCurrentValue() instanceof BigDecimal) {
                newDirty = !(((BigDecimal) getCurrentValue()).compareTo((BigDecimal) getOriginalValue()) == 0);
            }
            else if (getCurrentValue() instanceof List) {
                // compare lists
                List current = (List) getCurrentValue();
                List original = (List) getOriginalValue();

                if (current == null && original == null) {
                    newDirty = false;
                }
                else if (current == null || original == null) {
                    newDirty = true;
                }
                else if (current.isEmpty() && original.isEmpty()) {
                    newDirty = false;
                }
                else if (current.size() == original.size()) {
                    if (isListOrderRelevant) {
                        newDirty = false;
                        for (int index = 0; index < current.size(); index++) {
                            if (!current.get(index).equals(original.get(index))) {
                                newDirty = true;
                                break;
                            }
                        }
                    }
                    else {
                        newDirty = !(current.containsAll(original) && original.containsAll(current));
                    }
                }
                else {
                    newDirty = true;
                }
            }
            else {
                newDirty = !getCurrentValue().equals(getOriginalValue());
            }
            dirty = newDirty;

        }
        else {
            dirty = true;
        }
        
    }

    @Override
    public T getOriginalValue() {
        return originalValue;
    }


    @Override
    public void read() {
    	throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void directRead(T value) {
        this.originalValue = value;
        setCurrentValue(value);
    }

    @Override
    public void write() {
    	throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public T directWrite() {
        return currentValue;
    }

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
        if(this.validators == null) {
        	this.validators = new ArrayList<IFValidator<T>>(1);
        }
        this.validators.add(validator);
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidationResult()
     */
    @Override
    public ValidationResult getValidationResult() {
        ValidationResult result = new ValidationResult();
        if (externalValidationErrors != null && !externalValidationErrors.isEmpty()) {
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

    @Override
    public List<String> getValidationFailures() {
        return getValidationResult() != null ? getValidationResult().getMessagesByBinding(this)
                : new ArrayList<String>();
    }

    /**
     * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public void addViewAdapter(IFViewAdapter viewAdapter) {
    	throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void removeViewAdapter(IFViewAdapter viewAdapter) {
    	throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void viewValueChanged(IFViewAdapter viewAdapter) {
        gaChanged(viewAdapter, (T) viewAdapter.getValue());
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
    	throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public IFAttributeInfo getAttributeInfo() {
    	throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void addExternalValidationError(String translatedMessage) {
        addExternalValidationError(new ValidationError(this, translatedMessage, null));
    }

    @Override
    public void addExternalValidationError(ValidationError validationError) {
        boolean wasEmpty = externalValidationErrors == null || externalValidationErrors.isEmpty();
        if (externalValidationErrors == null) {
            externalValidationErrors = new ArrayList<ValidationError>();
        }
        externalValidationErrors.add(validationError);
        if(wasEmpty) {
            calculateState(null);
        }
    }

    @Override
    public void clearExternalValidationErrors() {
        externalValidationErrors = null;
    }

    /**
     * @return the isListOrderRelevant
     */
    public boolean isListOrderRelevant() {
        return isListOrderRelevant;
    }

    /**
     * @param isListOrderRelevant the isListOrderRelevant to set
     */
    public void setListOrderRelevant(boolean isListOrderRelevant) {
        this.isListOrderRelevant = isListOrderRelevant;
    }

	@Override
	public void addAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
    	throw new UnsupportedOperationException("Operation not supported");
	}

	@Override
	public void removeAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
    	throw new UnsupportedOperationException("Operation not supported");
	}

	@Override
	public void setValueConverter(IFValueConverter valueConverter) {
    	throw new UnsupportedOperationException("Operation not supported");
	}

}

package net.ulrice.databinding.validation;

import net.ulrice.databinding.IFBindingIdentifier;

/**
 * Abstract implementation of the ifvalidator interface. This class handles the
 * method for getting the last validation errors.
 * 
 * @author christof
 */
public abstract class AbstractValidator<T> implements IFValidator<T> {

    /** The last validation errors. */
    private ValidationResult errors;

    /**
     * @see net.ulrice.databinding.validation.IFValidator#getLastValidationErrors()
     */
    @Override
    public ValidationResult getLastValidationErrors() {
        return this.errors;
    }

    /**
     * @see net.ulrice.databinding.validation.IFValidator#validate(java.lang.Object)
     */
    @Override
    public ValidationResult isValid(IFBindingIdentifier bindingId, T attribute) {
        this.errors = validate(bindingId, attribute);
        return errors;
    }

    /**
     * @see net.ulrice.databinding.validation.IFValidator#clear()
     */
    @Override
    public void clear() {
        this.errors = null;
    }
    
    /**
     * Validates the attribute. 
     *
     * @param bindingId The identifier of the binding
     * @param attribute The attribute to be validated.
     * @return The validation errors or null, if there are no validation errors.
     */
    protected abstract ValidationResult validate(IFBindingIdentifier bindingId, T attribute);

}

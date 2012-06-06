package net.ulrice.databinding.validation;

import net.ulrice.databinding.IFBinding;

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
    public ValidationResult isValid(IFBinding bindingId, T attribute, Object rawAttribute) {
        this.errors = validate(bindingId, attribute, rawAttribute);
        return errors;
    }

    /**
     * @see net.ulrice.databinding.validation.IFValidator#clearValidationErrors()
     */
    @Override
    public void clearValidationErrors() {
        this.errors = null;
    }
    
    /**
     * Validates the attribute. 
     *
     * @param bindingId The identifier of the binding
     * @param attribute The attribute to be validated.
     * @param rawAttribute 
     * @return The validation errors or null, if there are no validation errors.
     */
    protected abstract ValidationResult validate(IFBinding bindingId, T attribute, Object rawAttribute);

}

package net.ulrice.databinding.impl.validation;

import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFValidator;

/**
 * Abstract implementation of the ifvalidator interface. This class handles the
 * method for getting the last validation errors.
 * 
 * @author christof
 */
public abstract class AbstractValidator<T> implements IFValidator<T> {

    /** The last validation errors. */
    private ValidationErrors errors;

    /**
     * @see net.ulrice.databinding.IFValidator#getLastValidationErrors()
     */
    @Override
    public ValidationErrors getLastValidationErrors() {
        return this.errors;
    }

    /**
     * @see net.ulrice.databinding.IFValidator#validate(java.lang.Object)
     */
    @Override
    public ValidationErrors validate(IFAttributeModel<?> model, T attribute) {
        this.errors = validateIntern(model, attribute);
        return errors;
    }

    /**
     * @see net.ulrice.databinding.IFValidator#clear()
     */
    @Override
    public void clear() {
        this.errors = null;
    }
    
    /**
     * Validates the attribute. 
     *
     * @param model The attribute model.
     * @param attribute The attribute to be validated.
     * @return The validation errors or null, if there are no validation errors.
     */
    protected abstract ValidationErrors validateIntern(IFAttributeModel<?> model, T attribute);

}

package net.ulrice.databinding;

import net.ulrice.databinding.impl.validation.ValidationErrors;

/**
 * Interface for an attribute validator.
 * 
 * @author christof
 */
public interface IFValidator<T> {

    /**
     * Returns validation errors or null, if the attribute is valid.
     * 
     * @param model The attribute model.
     * @param attribute The attribute to be validated
     * @return Validation errors.
     */
    ValidationErrors validate(IFAttributeModel<?> model, T attribute);
    
    /**
     * Returns the last validation errors.
     * 
     * @return The last validation errors
     */
    ValidationErrors getLastValidationErrors();

    /**
     * Clear the validation errors.
     */
    void clear();
}

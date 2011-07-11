package net.ulrice.databinding;

import net.ulrice.databinding.impl.validation.ValidationResult;

/**
 * Interface for an attribute validator.
 * 
 * @author christof
 */
public interface IFValidator<T> {

    /**
     * Returns validation errors or null, if the attribute is valid.
     * 
     * @param bindingId The identifier of the binding
     * @param attribute The attribute to be validated
     * @return Validation errors.
     */
    ValidationResult isValid(IFBindingIdentifier bindingId, T attribute);
    
    /**
     * Returns the last validation errors.
     * 
     * @return The last validation errors
     */
    ValidationResult getLastValidationErrors();

    /**
     * Clear the validation errors.
     */
    void clear();
}

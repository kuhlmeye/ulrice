package net.ulrice.databinding.validation;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

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
     * @param displayedValue The value that comes from the Swing component in
     * most cases. E.g. if the attribute is a date the displayedValue will be
     * the String the Date is created from.
     * {@link IFViewAdapter#getDisplayedValue()} returns this value.
     * @return Validation errors.
     */
    ValidationResult isValid(IFBinding bindingId, T attribute, Object displayedValue);
    
    /**
     * Returns the last validation errors.
     * 
     * @return The last validation errors
     */
    ValidationResult getLastValidationErrors();

    /**
     * Clear the validation errors.
     */
    void clearValidationErrors();
}

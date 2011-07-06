package net.ulrice.databinding.impl.validation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class containing the validation errors.
 * 
 * @author christof
 */
public class ValidationErrors {

    /** The list of validation errors. */
    private List<ValidationError> validationErrors;
    
    /**
     * Creates a new validation errors object.
     */
    public ValidationErrors() {
        this.validationErrors = new LinkedList<ValidationError>();
    }
    
    /**
     * Creates a new validation errors objects with a validation error.
     * 
     * @param validationError The validation error
     */
    public ValidationErrors(ValidationError validationError) {
        this();
        addValidationError(validationError);
    }
    
    /**
     * Adds a new validation error. 
     * 
     * @param validationError
     */
    public void addValidationError(ValidationError validationError) {
        this.validationErrors.add(validationError);
    }
    
    /**
     * Returns an unmodifiable list of validation errors.
     * 
     * @return The list of validation errors. 
     */
    public List<ValidationError> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }
    
    /**
     * Resets the list of validation errors
     */
    public void clearValidationErrors() {
        this.validationErrors.clear();
    }
    
}

package net.ulrice.databinding.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ulrice.databinding.IFBindingIdentifier;

/**
 * Class containing the validation errors.
 * 
 * @author christof
 */
public class ValidationResult {

    /** The list of validation errors. */
    private List<ValidationError> validationErrors;
    
    private Map<IFBindingIdentifier, List<String>> messagesByBindingMap;
    
    /**
     * Creates a new validation errors object.
     */
    public ValidationResult() {
        this.validationErrors = new LinkedList<ValidationError>();
        this.messagesByBindingMap = new HashMap<IFBindingIdentifier, List<String>>();
    }
    
    /**
     * Creates a new validation errors objects with a validation error.
     * 
     * @param validationError The validation error
     */
    public ValidationResult(ValidationError validationError) {
        this();
        addValidationError(validationError);
    }
    
    /**
     * Returns an unmodifiable list of validation errors.
     * 
     * @return The list of validation errors. 
     */
    public List<ValidationError> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }
 
    
    public boolean isValid () {
        return validationErrors.isEmpty ();
    }
    
    public List<String> getMessagesByBinding (IFBindingIdentifier b) {
        return messagesByBindingMap.get (b);
    }
       
    public void addValidationError(ValidationError validationError) {
        this.validationErrors.add(validationError);
		List<String> messageList = messagesByBindingMap.get(validationError.getBindingId());
		if(messageList == null) {
			messageList = new ArrayList<String>();
			messagesByBindingMap.put(validationError.getBindingId(), messageList);
		}
		messageList.add(validationError.getMessage());
    }
    
	public void addFailure(IFBindingIdentifier bindingId, String message) {
		addValidationError(new ValidationError(bindingId, message, null));
	}    
	
    public void addValidationErrors(List<ValidationError> validationErrors) {
    	if(validationErrors != null) {
    		for(ValidationError error : validationErrors) {
    			addValidationError(error);
    		}
    	}
    }
    
    /**
     * Resets the list of validation errors
     */
    public void clearValidationErrors() {
        this.validationErrors.clear();
    }
}

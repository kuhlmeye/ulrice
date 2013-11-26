package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;


public class StringLengthValidator extends AbstractValidator<String> {
    private final Integer minLength;
    private final Integer maxLength;
    
    public StringLengthValidator (Integer minLength, Integer maxLength) {
    	this.minLength = minLength;
    	this.maxLength = maxLength;
    }

	@Override
	protected ValidationResult validate(IFBinding bindingId, String attribute, Object rawAttribute) {
		ValidationResult result = new ValidationResult();
		
		// just validate length, not null should be handled by a different validator
		if (attribute == null) {
			return result;
		}
		
        if (minLength != null && attribute.length () < minLength) {
        	// TODO Christof Internationalize
            result.addValidationError(new ValidationError(bindingId, "min. L\u00e4nge: " + minLength, null));
        }
        if (maxLength != null && attribute.length () > maxLength) {
        	// TODO Christof Internationalize
            result.addValidationError(new ValidationError(bindingId, "max. L\u00e4nge: " + maxLength, null));
        }
        
        return result;
    }
}

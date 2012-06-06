package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;


public class StringLengthValidator extends AbstractValidator<String> {
    private final Integer _minLength;
    private final Integer _maxLength;
    
    public StringLengthValidator (Integer minLength, Integer maxLength) {
        _minLength = minLength;
        _maxLength = maxLength;
    }

	@Override
	protected ValidationResult validate(IFBinding bindingId, String attribute, Object rawAttribute) {
		ValidationResult result = new ValidationResult();
		
		// just validate length, not null should be handled by a different validator
		if (attribute == null) {
			return result;
		}
		
        if (_minLength != null && attribute.length () < _minLength) {
        	// TODO Christof Internationalize
            result.addValidationError(new ValidationError(bindingId, "min. L\u00e4nge: " + _minLength, null));
        }
        if (_maxLength != null && attribute.length () > _maxLength) {
        	// TODO Christof Internationalize
            result.addValidationError(new ValidationError(bindingId, "max. L\u00e4nge: " + _minLength, null));
        }
        
        return result;
    }
}

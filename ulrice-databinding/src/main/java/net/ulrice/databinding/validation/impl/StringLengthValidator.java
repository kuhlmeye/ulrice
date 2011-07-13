package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;


public class StringLengthValidator extends AbstractValidator<String> {
    private final int _minLength;
    private final int _maxLength;
    
    public StringLengthValidator (int minLength, int maxLength) {
        _minLength = minLength;
        _maxLength = maxLength;
    }

	@Override
	protected ValidationResult validate(IFBinding bindingId, String attribute) {
        if (attribute.length () < _minLength) {
        	// TODO Christof Internationalize
            ValidationResult errors = new ValidationResult(new ValidationError(bindingId, "min. Länge: " + _minLength, null));
            return errors;
        }
        if (attribute.length () > _maxLength) {
        	// TODO Christof Internationalize
            ValidationResult errors = new ValidationResult(new ValidationError(bindingId, "max. Länge: " + _minLength, null));
            return errors;
        }
        
        return null;
    }
}

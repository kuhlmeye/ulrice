package net.ulrice.databinding.validation.impl;

import java.util.regex.Pattern;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Creates a new validator validating an attribute against a regular expression.
 * 
 * @author christof
 */
public class RegExValidator<T extends Object> extends AbstractValidator<T> {

    /** The reg-ex pattern used for validation. */
    private Pattern pattern;
    
    /** The message thrown, if the validation fails. */
    private String message;

    /**
     * Creates a new regex validator.
     * 
     * @param regEx The regular expression
     * @param message The message, if the validation fails.
     */
    public RegExValidator(String regEx, String message) {
        this.pattern = Pattern.compile(regEx);
        this.message = message;
    }
    
    @Override
    protected ValidationResult validate(IFBinding bindingId, T attribute, Object rawAttribute) {

        if(attribute != null) {
            if(!pattern.matcher(attribute.toString()).matches()) {
                ValidationResult errors = new ValidationResult(new ValidationError(bindingId, message, null));
                return errors;
            }
        }
        
        
        return null;
    }

}

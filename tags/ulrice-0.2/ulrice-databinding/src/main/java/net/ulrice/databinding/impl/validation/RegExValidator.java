package net.ulrice.databinding.impl.validation;

import java.util.regex.Pattern;

import net.ulrice.databinding.IFAttributeModel;

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
    
    /**
     * @see net.ulrice.databinding.impl.validation.AbstractValidator#validateIntern(java.lang.Object)
     */
    @Override
    protected ValidationErrors validateIntern(IFAttributeModel<?>model, T attribute) {

        if(attribute != null) {
            if(!pattern.matcher(attribute.toString()).matches()) {
                ValidationErrors errors = new ValidationErrors(new ValidationError(model.getId(), message, null));
                return errors;
            }
        }
        
        
        return null;
    }

}

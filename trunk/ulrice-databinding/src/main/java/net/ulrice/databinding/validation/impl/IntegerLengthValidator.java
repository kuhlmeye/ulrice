package net.ulrice.databinding.validation.impl;

/**
 * Save while using StringToIntegerConverter.
 * 
 * @author - apunahassaphemapetilon@hotmail.com
 */
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationResult;

public class IntegerLengthValidator extends AbstractValidator<Integer> {
    
    private final StringLengthValidator validator;
    
    public IntegerLengthValidator (Integer minLength, Integer maxLength) {
        validator = new StringLengthValidator(minLength, maxLength);
    }

    @Override
    protected ValidationResult validate(IFBinding bindingId, Integer attribute) {
        if (attribute != null) {
            validator.validate(bindingId, attribute.toString());
        }
        return null;
    }

}

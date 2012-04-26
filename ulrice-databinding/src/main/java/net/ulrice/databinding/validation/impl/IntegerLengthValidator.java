package net.ulrice.databinding.validation.impl;

/**
 * Integer validator checking the minimum and maximum value of an Integer.
 * 
 * @author - apunahassaphemapetilon@hotmail.com
 */
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

public class IntegerLengthValidator extends AbstractValidator<Integer> {
    
    private final Integer minValue;
    private final Integer maxValue;
    
    public IntegerLengthValidator (Integer minValue, Integer maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    protected ValidationResult validate(IFBinding bindingId, Integer attribute) {
        ValidationResult result = new ValidationResult();
        
        if (attribute == null) {
            return result;
        }
        
        if (minValue != null && attribute < minValue) {
            // TODO Christof Internationalize
            result.addValidationError(new ValidationError(bindingId, "min. Wert: " + minValue, null));
        }
        if (maxValue != null && attribute > maxValue) {
            // TODO Christof Internationalize
            result.addValidationError(new ValidationError(bindingId, "max. Wert: " + maxValue, null));
        }
        
        return result;
    }

}

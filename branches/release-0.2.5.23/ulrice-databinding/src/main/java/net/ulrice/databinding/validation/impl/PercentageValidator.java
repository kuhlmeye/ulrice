package net.ulrice.databinding.validation.impl;

import java.math.BigDecimal;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Checks if the bigdecimal number has a valid percentage interval [0% - 100%].
 */
public class PercentageValidator extends AbstractValidator {

    /**
     * {@inheritDoc}
     * @see net.ulrice.databinding.validation.AbstractValidator#validate(net.ulrice.databinding.IFBinding, java.lang.Object, java.lang.Object)
     */
    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();
        
        if (attribute != null && attribute instanceof BigDecimal) {
            BigDecimal percentage = (BigDecimal) attribute;
            if (percentage.compareTo(BigDecimal.ZERO) == -1 || percentage.compareTo(BigDecimal.ONE) == 1) { 
                result.addValidationError(new ValidationError(bindingId, "attribute is a percentage number", null));
            }
        }
        
        return result;
    }
}

package net.ulrice.databinding.validation.impl;

import java.math.BigDecimal;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * 
 * Checks if a bigdecimal data model is below zero or not.
 */

public class NotNegativeNumberValidator extends AbstractValidator {

    @Override
    @SuppressWarnings("boxing")
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        if (attribute != null) {
            BigDecimal number = BigDecimal.ZERO;

            if (attribute instanceof Number) {
                if (attribute instanceof BigDecimal) {
                    number = (BigDecimal) attribute;
                }
                else if (attribute instanceof Integer) {
                    number = BigDecimal.valueOf(((Integer) attribute).doubleValue());
                }
                else if (attribute instanceof Long) {
                    number = BigDecimal.valueOf(((Long) attribute));
                }
                else {
                    result.addValidationError(new ValidationError(bindingId, "attribute type is not supported", null));
                }

                if (BigDecimal.ZERO.compareTo(number) == 1) {
                    result.addValidationError(new ValidationError(bindingId, "attribute is a negative number", null));
                }
            }
            else {
                result.addValidationError(new ValidationError(bindingId, "attribute is not a number", null));
            }
        }
        return result;
    }
}

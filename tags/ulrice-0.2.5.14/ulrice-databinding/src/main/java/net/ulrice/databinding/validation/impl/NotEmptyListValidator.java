package net.ulrice.databinding.validation.impl;

import java.util.Collection;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

public class NotEmptyListValidator extends AbstractValidator {

    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        if (attribute != null) {
            if (attribute instanceof Collection) {
                if (((Collection) attribute).size() == 0) {
                    result.addValidationError(new ValidationError(bindingId, "The list can not be empty!", null));
                }
            }
        }
        return result;

    }

}

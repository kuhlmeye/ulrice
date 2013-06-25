package net.ulrice.databinding.validation.impl;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/*
 * Validator: if an attribute model value must be differ from the others 
 */
@SuppressWarnings("rawtypes")
public class NotEqualValidator<T> extends AbstractValidator {

    private final List<IFAttributeModel<T>> models;

    public NotEqualValidator(IFAttributeModel<T>... attributeModels) {
        this.models = Arrays.asList(attributeModels);
    }

    /**
     * {@inheritDoc}
     * 
     * @see net.ulrice.databinding.validation.AbstractValidator#validate(net.ulrice.databinding.IFBinding,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        if (attribute != null) {
            for (IFAttributeModel<T> model : models) {
                T value = (model.getCurrentValue() != null) ? model.getCurrentValue() : model.getOriginalValue();
                if (value == null)
                    break;
                if (attribute.equals(value)) {
                    result.addValidationError(new ValidationError(bindingId, "The value can not be the same!", null));
                    break;
                }
            }
        }
        return result;
    }
}

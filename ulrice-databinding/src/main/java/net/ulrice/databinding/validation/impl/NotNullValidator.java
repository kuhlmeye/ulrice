package net.ulrice.databinding.validation.impl;

import net.ulrice.Ulrice;
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.message.TranslationProvider;
import net.ulrice.message.TranslationUsage;

/**
 * @author apunahassaphemapetilon@hotmail.com Rawtypes is ok as we just check if the object is null.
 */

@SuppressWarnings("rawtypes")
public class NotNullValidator extends AbstractValidator {

    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        if (attribute == null) {
            TranslationProvider translationProvider = Ulrice.getTranslationProvider();

            if (bindingId.getOriginalValue() instanceof Number) {
                if (translationProvider != null) {
                    result.addValidationError(new ValidationError(bindingId, translationProvider.getUlriceTranslation(TranslationUsage.ValidationLabel, "NumberValueExpected")
                        .getText(), null));
                }
                else {
                    result.addValidationError(new ValidationError(bindingId, "attribute is not a number", null));
                }
            }
            else {
                if (translationProvider != null) {
                    result.addValidationError(new ValidationError(bindingId, translationProvider.getUlriceTranslation(TranslationUsage.ValidationLabel, "NotNullValueExpected")
                        .getText(), null));
                }
                else {
                    result.addValidationError(new ValidationError(bindingId, "attribute must not be null!", null));
                }
            }
        }

        return result;
    }

}

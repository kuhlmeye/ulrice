package net.ulrice.databinding.validation.impl;

import java.util.ArrayList;

import net.ulrice.Ulrice;
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.message.TranslationUsage;

@SuppressWarnings("rawtypes")
public class AllOrNothingValidator extends AbstractValidator {

    private final ArrayList<GenericAM< ?>> modelList = new ArrayList<GenericAM< ?>>();
    private final String module;
    private final String key;

    public AllOrNothingValidator(String module, String key, IFAttributeModel< ?>... models) {
        super();
        this.module = module;
        this.key = key;
        for (IFAttributeModel model : models) {
            this.modelList.add((GenericAM< ?>) model);
        }
    }

    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        boolean allEmpty = true;
        boolean allFilled = true;

        for (GenericAM< ?> model : modelList) {
            if (model.getCurrentValue() != null) {
                allEmpty &= false;
            }
            else {
                allFilled &= false;
            }
            if (model.equals(bindingId) && attribute != null) {
                model.clearExternalValidationErrors();
            }
        }
        if (!allEmpty && !allFilled) {
            String text = Ulrice.getTranslationProvider().getTranslationText(module, TranslationUsage.Message, key);
            ValidationError error = new ValidationError(bindingId, text, null);
            result.addValidationError(error);
            for (GenericAM< ?> model : modelList) {
                if (model.getCurrentValue() == null) {
                    if (!model.equals(bindingId)) {
                        model.addExternalValidationError(error);
                        model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
                    }
                }
            }
        }
        else {
            for (GenericAM< ?> model : modelList) {
                if (!model.equals(bindingId)) {
                    model.clearExternalValidationErrors();
                    model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
                }
            }
        }
        if (attribute == null) {
            return result;
        }
        else {
            return null;
        }
    }

}

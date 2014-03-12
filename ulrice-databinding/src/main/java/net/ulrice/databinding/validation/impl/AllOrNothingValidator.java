package net.ulrice.databinding.validation.impl;

import net.ulrice.Ulrice;
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.message.TranslationUsage;

public class AllOrNothingValidator extends AbstractCrossFieldValidator {

    private final String module;
    private final String key;

    public AllOrNothingValidator(String module, String key, IFAttributeModel< ?>... models) {
        super(models);
        this.module = module;
        this.key = key;
    }

    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        if (!isModelListInitialized()) {
            return result;
        }

        boolean allEmpty = true;
        boolean allFilled = true;

        for (GenericAM< ?> model : getModelList()) {
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
            for (GenericAM< ?> model : getModelList()) {
                if (model.getCurrentValue() == null) {
                    if (!model.equals(bindingId)) {
                        model.addExternalValidationError(error);
                        model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
                    }
                }
            }
        }
        else {
            for (GenericAM< ?> model : getModelList()) {
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

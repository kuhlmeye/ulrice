package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Checks if all of the fields are filled.
 */

public class FieldANDCombinationValidator extends AbstractCrossFieldValidator {

    protected String idString = new String();

    public FieldANDCombinationValidator(GenericAM< ?>... models) {
        super(models);
        for (GenericAM< ?> model : models) {
            idString = idString + " " + model.getId();
        }
    }

    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        if (!isModelListInitialized()) {
            return result;
        }

        if (attribute == null) { /* actual field is cleared right now */
            for (GenericAM< ?> model : getModelList()) {
                if (model.getCurrentValue() == null) {
                    // TODO Internationalize
                    result.addValidationError(new ValidationError(bindingId, "all of these fields must be filled "
                            + bindingId.getId() + idString, null));
                    return result;
                }
            }
            return result;
        }

        for (GenericAM< ?> model : getModelList()) { /* actualize dependent fields */
            if (result.getValidationErrors().isEmpty() != model.isValid()) {
                model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
            }
        }
        return result;
    }
}

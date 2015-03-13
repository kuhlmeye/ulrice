package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Checks if minimum one of a list of fields is filled
 *
 * @author silvia.roessler@gmail.com
 */

public class FieldORCombinationValidator extends AbstractCrossFieldValidator {

    protected String idString = new String();

    public FieldORCombinationValidator(GenericAM< ?>... models) {
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
                if (model.getCurrentValue() != null) {
                    return result; /* the other field is filled -> the actual empty field is valid */
                }
            }
            // TODO Internationalize
            result.addValidationError(new ValidationError(bindingId, "one of these fields must be filled "
                + bindingId.getId() + idString, null));
        }

        for (GenericAM< ?> model : getModelList()) { /* actualize dependent fields */
            if (result.getValidationErrors().isEmpty() != model.isValid()) {
                model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
            }
        }

        return result;
    }
}

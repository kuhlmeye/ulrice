package net.ulrice.databinding.validation.impl;

import java.util.ArrayList;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Checks if all of the fields are filled.
 */

/* Rawtypes is ok as we just check if the object is null or other objects are filled */
@SuppressWarnings("rawtypes")
public class FieldORCombinationValidator2 extends AbstractValidator {

    protected ArrayList<GenericAM< ?>> modelList = new ArrayList<GenericAM< ?>>();
    protected String idString = new String();

    public FieldORCombinationValidator2(GenericAM< ?>... models) {
        for (GenericAM< ?> model : models) {
            modelList.add(model);
            idString = idString + " " + model.getId();
        }
    }

    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();

        if (attribute == null) { /* actual field is cleared right now */
            for (GenericAM< ?> model : modelList) {
                if (model.getCurrentValue() == null) {
                    // TODO Internationalize
                    result.addValidationError(new ValidationError(bindingId, "all of these fields must be filled "
                            + bindingId.getId() + idString, null));
                    return result;
                }
            }
            return result;
        }

        for (GenericAM< ?> model : modelList) { /* actualize dependent fields */
            if (result.getValidationErrors().isEmpty() != model.isValid()) {
                model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
            }
        }
        return result;
    }
}

package net.ulrice.databinding.validation.impl;

import java.util.ArrayList;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Checks if a field is filled IF another field is filled
 *
 * @author silvia.roessler@gmail.com
 */

/* Rawtypes is ok as we just check if the object is null or other objects are filled */
@SuppressWarnings("rawtypes")
public class FieldIFCombinationValidator extends AbstractValidator {

    protected ArrayList<GenericAM< ?>> modelList = new ArrayList<GenericAM< ?>>();
    protected String idString = new String();

    public FieldIFCombinationValidator(GenericAM< ?>... models) {
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
                if (model.getCurrentValue() != null) {
                    // TODO Internationalize
                    result.addValidationError(new ValidationError(bindingId, "all of these fields must be filled "
                        + bindingId.getId() + idString, null));
                    return result;
                }
            }
        }

        /* actual field is filled -> also the other fields must be filled */
        for (GenericAM< ?> model : modelList) { /* actualize dependent fields */
             model.recalculateStateForThisValidator(this, attribute==null || model.getCurrentValue()!=null, model.getCurrentValue());
        }

        return result;
    }
}

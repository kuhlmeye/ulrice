package net.ulrice.databinding.validation.impl;

import java.util.ArrayList;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.AbstractValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Checks if all of the dependent fields are filled out if @this is not filled. 
 */

/* Rawtypes is ok as we just check if the object is null or other objects are filled */
@SuppressWarnings("rawtypes")
public class FieldXORCombinationValidator extends AbstractValidator {
    protected ArrayList<GenericAM< ?>> modelList = new ArrayList<GenericAM< ?>>();
    protected String idString = new String();
    protected final String specifiedErrorMessage;

    public FieldXORCombinationValidator(GenericAM< ?>... models) {
       this(null, models);
    }
    
    public FieldXORCombinationValidator(String errorMessage, GenericAM< ?>... models) {
        this.specifiedErrorMessage = errorMessage;
        for (GenericAM< ?> model : models) {
            modelList.add(model);
            idString = idString + " " + model.getId();
        }
    }

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.databinding.validation.AbstractValidator#validate(net.ulrice.databinding.IFBinding, java.lang.Object, java.lang.Object)
     */
    @Override
    protected ValidationResult validate(IFBinding bindingId, Object attribute, Object rawAttribute) {
        ValidationResult result = new ValidationResult();
        final String errorMessage = (specifiedErrorMessage == null) ? "one of these fields must be filled "
                + bindingId.getId() + idString: specifiedErrorMessage;

        if (attribute == null) { /* actual field is cleared right now */
            for (GenericAM< ?> model : modelList) {
                if (model.getCurrentValue() == null) {
                    result.addValidationError(new ValidationError(bindingId,errorMessage, null));
                    break;
                }
            }
        }
        else {
            for (GenericAM< ?> model : modelList) {
                if (model.getCurrentValue() != null) {
                    result.addValidationError(new ValidationError(bindingId, errorMessage, null));
                    break;
                }
            }
        }

        for (GenericAM< ?> model : modelList) { /* actualize dependent fields */
            if (result.getValidationErrors().isEmpty() != model.isValid()) {
                model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
            }
        }
        return result;
    }
}

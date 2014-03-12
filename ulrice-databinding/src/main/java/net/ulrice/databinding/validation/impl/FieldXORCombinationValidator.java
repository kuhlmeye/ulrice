package net.ulrice.databinding.validation.impl;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * Checks if all of the dependent fields are filled out if @this is not filled.
 */

public class FieldXORCombinationValidator extends AbstractCrossFieldValidator {
    protected String idString = new String();
    protected final String specifiedErrorMessage;

    public FieldXORCombinationValidator(GenericAM< ?>... models) {
       this(null, models);
    }

    public FieldXORCombinationValidator(String errorMessage, GenericAM< ?>... models) {
        super(models);
        this.specifiedErrorMessage = errorMessage;
        for (GenericAM< ?> model : models) {
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

        if (!isModelListInitialized()) {
            return result;
        }

        final String errorMessage = (specifiedErrorMessage == null) ? "one of these fields must be filled "
                + bindingId.getId() + idString: specifiedErrorMessage;

        if (attribute == null) { /* actual field is cleared right now */
            for (GenericAM< ?> model : getModelList()) {
                if (model.getCurrentValue() == null) {
                    result.addValidationError(new ValidationError(bindingId,errorMessage, null));
                    break;
                }
            }
        }
        else {
            for (GenericAM< ?> model : getModelList()) {
                if (model.getCurrentValue() != null) {
                    result.addValidationError(new ValidationError(bindingId, errorMessage, null));
                    break;
                }
            }
        }

        for (GenericAM< ?> model : getModelList()) { /* actualize dependent fields */
            if (result.getValidationErrors().isEmpty() != model.isValid()) {
                model.recalculateStateForThisValidator(this, result.getValidationErrors().isEmpty(), model.getCurrentValue());
            }
        }
        return result;
    }
}

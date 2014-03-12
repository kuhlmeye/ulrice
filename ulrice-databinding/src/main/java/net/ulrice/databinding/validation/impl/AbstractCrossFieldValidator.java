package net.ulrice.databinding.validation.impl;

import java.util.ArrayList;

import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.impl.GenericAM;
import net.ulrice.databinding.validation.AbstractValidator;

/* Rawtypes is ok as we just check if the object is null or other objects are filled */
@SuppressWarnings("rawtypes")
public abstract class AbstractCrossFieldValidator extends AbstractValidator {

    private final ArrayList<GenericAM< ?>> modelList = new ArrayList<GenericAM< ?>>();

    public AbstractCrossFieldValidator(IFAttributeModel< ?>... models) {
        super();
        for (IFAttributeModel model : models) {
            this.getModelList().add((GenericAM< ?>) model);
        }
    }

    protected boolean isModelListInitialized() {
        for (GenericAM< ?> model : getModelList()) {
            if (!model.isInitialized()) {
                return false;
            }
        }
        return true;
    }

    protected ArrayList<GenericAM< ?>> getModelList() {
        return modelList;
    }

}

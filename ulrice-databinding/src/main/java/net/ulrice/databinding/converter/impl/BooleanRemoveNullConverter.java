package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;

/**
 * Converts a NULL-Boolean object to False
 * 
 * @author DL10KUH
 */
public class BooleanRemoveNullConverter implements IFValueConverter<Boolean, Boolean> {

    @Override
    public Class<Boolean> getViewType(Class<? extends Boolean> modelType) {
        return Boolean.class;
    }

    @Override
    public Class<Boolean> getModelType(Class<? extends Boolean> viewType) {
        return Boolean.class;
    }
    
    @Override
    public Boolean viewToModel(Boolean view, IFAttributeInfo attributeInfo) {
        return view == null ? Boolean.FALSE : view;
    }

    @Override
    public Boolean modelToView(Boolean model, IFAttributeInfo attributeInfo) {
        return model == null ? Boolean.FALSE : model;
    }

    @Override
    public boolean canHandle(Class<? extends Object> modelType,
            Class<? extends Object> viewType) {
        if (Boolean.class.equals(viewType)) {
            if (Boolean.class.equals(modelType) || Boolean.TYPE.equals(modelType)) {
                return true;
            }
        }
        return false;
    }
}

package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.ValueConverterException;

/**
 * String to Integer converter
 * 
 * @author andre
 * 
 */
public class StringToBooleanConverter implements IFValueConverter<Boolean, String> {

    @Override
    public Class<String> getViewType(Class<? extends Boolean> modelType) {
        return String.class;
    }

    @Override
    public Class<Boolean> getModelType(Class<? extends String> viewType) {
        return Boolean.class;
    }
    
    @Override
    public Boolean viewToModel(String view, IFAttributeInfo attributeInfo) {
        if (view == null || "".equals(view.trim())) {
            return null;
        }
        if("X".equals(view.trim())){
            return true;
        }
        
        try {
            return Boolean.valueOf(view.toString());
        } catch (NumberFormatException ex) {
            throw new ValueConverterException(ex);
        }
        
    }

    @Override
    public String modelToView(Boolean model, IFAttributeInfo attributeInfo) {
        return model == null ? "" : String.valueOf(model);
    }

    @Override
    public boolean canHandle(Class<? extends Object> modelType,
            Class<? extends Object> viewType) {
        if (String.class.equals(viewType)) {
            if (Boolean.class.equals(modelType) || Boolean.TYPE.equals(modelType)) {
                return true;
            }
        }
        return false;
    }
}

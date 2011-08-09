package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;



public class DoNothingConverter implements IFValueConverter {
    @Override
    public Class<?> getViewType(Class<?> modelType) {
        return modelType;
    }
    
    public Class<?> getModelType(Class<?> viewType) {
        return viewType;
    }
    
    public Object modelToView (Object o) {
        return o;
    }

    public Object viewToModel (Object o) {
        return o;
    }
}

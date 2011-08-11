package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;



public class DoNothingConverter <M, V> implements IFValueConverter <M, V> {
    @SuppressWarnings("rawtypes")
    public static final DoNothingConverter INSTANCE = new DoNothingConverter();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Class<? extends V> getViewType(Class<? extends M> modelType) {
        return (Class) modelType;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class<? extends M> getModelType(Class<? extends V> viewType) {
        return (Class) viewType;
    }
    
    @SuppressWarnings("unchecked")
    public V modelToView (M o) {
        return (V) o;
    }

    @SuppressWarnings("unchecked")
    public M viewToModel (V o) {
        return (M) o;
    }
}

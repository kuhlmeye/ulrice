package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
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
    public V modelToView (M o, IFAttributeInfo attributeInfo) {
        return (V) o;
    }

    @SuppressWarnings("unchecked")
    public M viewToModel (V o, IFAttributeInfo attributeInfo) {
        return (M) o;
    }

	@Override
	public boolean canHandle(Class<? extends Object> modelType,
			Class<? extends Object> viewType) {
		if (modelType.equals(viewType) || ObjectWithPresentation.class.equals(viewType)) {
			return true;
		}
		return false;
	}
}

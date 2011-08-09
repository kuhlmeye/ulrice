package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;


public class Reverser implements IFValueConverter {
    private final IFValueConverter inner;
    
    public Reverser (IFValueConverter converter) {
        this.inner = converter;
    }
    
    @Override
    public Class<?> getViewType(Class<?> modelType) {
        return inner.getModelType(modelType);
    }

    @Override
    public Class<?> getModelType(Class<?> viewType) {
        return inner.getViewType(viewType);
    }
	
	@Override
	public Object viewToModel(Object o) {
		return inner.modelToView(o);
	}

	@Override
	public Object modelToView(Object o) {
		return inner.viewToModel(o);
	}
}

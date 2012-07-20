package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;


public class Reverser<M, V> implements IFValueConverter<M, V> {
    private final IFValueConverter<V, M> inner;
    
    public Reverser (IFValueConverter<V, M> converter) {
        this.inner = converter;
    }
    
    @Override
    public Class<? extends V> getViewType (Class<? extends M> modelType) {
        return inner.getModelType(modelType);
    }

    @Override
    public Class<? extends M> getModelType(Class<? extends V> viewType) {
        return inner.getViewType(viewType);
    }

    
	@Override
	public M viewToModel(V o, IFAttributeInfo attributeInfo) {
		return inner.modelToView(o, attributeInfo);
	}

	@Override
	public V modelToView(M o, IFAttributeInfo attributeInfo) {
		return inner.viewToModel(o, attributeInfo);
	}

	@Override
	public boolean canHandle(Class<? extends Object> modelType,
			Class<? extends Object> viewType) {
		return inner.canHandle(viewType, modelType);
	}
}

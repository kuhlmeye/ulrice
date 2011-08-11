package net.ulrice.databinding.converter.impl;

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
	public M viewToModel(V o) {
		return inner.modelToView(o);
	}

	@Override
	public V modelToView(M o) {
		return inner.viewToModel(o);
	}
}

package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;

public class Reverser implements IFValueConverter {

	private IFValueConverter converter;

	public Reverser (IFValueConverter converter) {
		this.converter = converter;
	}
	
	@Override
	public Object viewToModel(Object o) {
		return converter.modelToView(o);
	}

	@Override
	public Object modelToView(Object o) {
		return converter.viewToModel(o);
	}
}

package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.ValueConverterException;

/**
 * String to Integer converter
 * 
 * @author andre
 * 
 */
public class StringToIntegerConverter implements IFValueConverter<String, Integer> {

    @Override
    public Class<Integer> getViewType(Class<? extends String> modelType) {
        return Integer.class; // TODO arno - this looks lilke it is the wrong way around
    }

    @Override
    public Class<String> getModelType(Class<? extends Integer> viewType) {
        return String.class;
    }
    
	@Override
	public String viewToModel(Integer view) {
		return view == null ? "" : String.valueOf(view);
	}

	@Override
	public Integer modelToView(String model) {
		if (model == null || "".equals(model.trim())) {
			return null;
		}
		try {
			return Integer.valueOf(model.toString());
		} catch (NumberFormatException ex) {
			throw new ValueConverterException(ex);
		}
	}
}

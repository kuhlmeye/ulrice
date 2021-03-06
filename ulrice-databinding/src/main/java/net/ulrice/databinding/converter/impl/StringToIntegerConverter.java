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
public class StringToIntegerConverter implements IFValueConverter<Integer, String> {

    @Override
    public Class<String> getViewType(Class<? extends Integer> modelType) {
        return String.class;
    }

    @Override
    public Class<Integer> getModelType(Class<? extends String> viewType) {
        return Integer.class;
    }
    
	@Override
	public Integer viewToModel(String view, IFAttributeInfo attributeInfo) {
		if (view == null || "".equals(view.trim()) || "-".equals(view.trim())) {
			return null;
		}
		try {
			return Integer.valueOf(view.toString());
		} catch (NumberFormatException ex) {
			//throw new ValueConverterException(ex);
			return null;
		}
		
	}

	@Override
	public String modelToView(Integer model, IFAttributeInfo attributeInfo) {
		return model == null ? "" : String.valueOf(model);
	}

	@Override
	public boolean canHandle(Class<? extends Object> modelType,
			Class<? extends Object> viewType) {
		if (String.class.equals(viewType)) {
			if (Integer.class.equals(modelType) || Integer.TYPE.equals(modelType)) {
				return true;
			}
		}
		return false;
	}
}

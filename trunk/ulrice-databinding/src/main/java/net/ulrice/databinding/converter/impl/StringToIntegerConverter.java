package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.ValueConverterException;

/**
 * String to Integer converter
 * 
 * @author andre
 * 
 */
public class StringToIntegerConverter implements IFValueConverter {

    @Override
    public Class<?> getViewType(Class<?> modelType) {
        return Integer.class; // TODO arno - this looks lilke it is the wrong way around
    }

    @Override
    public Class<?> getModelType(Class<?> viewType) {
        return String.class;
    }
    
	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see net.ulrice.databinding.IFConverter#mapToSource(java.lang.Object)
	 */
	@Override
	public Object viewToModel(Object view) {
		return view == null ? "" : String.valueOf(view);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see net.ulrice.databinding.IFConverter#mapToTarget(java.lang.Object)
	 */
	@Override
	public Object modelToView(Object model) {
		if (model == null || "".equals(((String) model).trim())) {
			return null;
		}
		try {
			return Integer.valueOf(model.toString());
		} catch (NumberFormatException ex) {
			return new ValueConverterException(ex);
		}
	}
}

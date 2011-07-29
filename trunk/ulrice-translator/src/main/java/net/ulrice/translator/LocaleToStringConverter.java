package net.ulrice.translator;

import java.util.Locale;

import net.ulrice.databinding.converter.IFValueConverter;

public class LocaleToStringConverter implements IFValueConverter {

	@Override
	public Object viewToModel(Object o) {
		if(o == null) {
			return null;
		}
		
		String string = (String)o;
		Locale locale = new Locale(string);
		return locale;
	}

	@Override
	public Object modelToView(Object o) {
		if(o == null) {
			return null;
		}
		Locale locale = (Locale)o;
		return locale.toString();
	}

}

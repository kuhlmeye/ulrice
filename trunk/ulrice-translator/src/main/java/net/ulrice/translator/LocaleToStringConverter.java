package net.ulrice.translator;

import java.util.Locale;

import net.ulrice.databinding.converter.IFValueConverter;

public class LocaleToStringConverter implements IFValueConverter<Locale, String> {

    @Override
    public Class<String> getViewType(Class<? extends Locale> modelType) {
        return String.class;
    }
    
    @Override
    public Class<Locale> getModelType(Class<? extends String> viewType) {
        return Locale.class;
    }
    
	@Override
	public Locale viewToModel(String o) {
		if(o == null) {
			return null;
		}
		
		return new Locale(o);
	}

	@Override
	public String modelToView(Locale o) {
		if(o == null) {
			return null;
		}
		return o.toString();
	}

}

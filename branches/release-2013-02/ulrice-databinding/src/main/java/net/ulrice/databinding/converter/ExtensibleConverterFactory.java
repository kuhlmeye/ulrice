package net.ulrice.databinding.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.converter.impl.GenericStringToNumberConverter;
import net.ulrice.databinding.converter.impl.StringToBooleanConverter;
/**
 * 
 * @author apunahassaphemapetilon@hotmail.com
 *
 */
public class ExtensibleConverterFactory implements IFConverterFactory {
	private static final List<? extends IFValueConverter<?, ?>> builtin = Arrays.asList (
			GenericStringToNumberConverter.INT,
			GenericStringToNumberConverter.LONG,
			GenericStringToNumberConverter.DOUBLE,
			GenericStringToNumberConverter.SHORT,
			new StringToBooleanConverter()
			);
	
	
	private final List<IFValueConverter<?,?>> contributed = new ArrayList<IFValueConverter <?,?>>();

	public void registerConverter (IFValueConverter <?, ?> converter) {
		contributed.add (converter);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <M, V> IFValueConverter<M, V> createConverter (Class<V> presentationType, Class<M> modelType) {
		if (presentationType.equals (modelType)) {
            return DoNothingConverter.INSTANCE;
        }
        
        if (ObjectWithPresentation.class.equals (presentationType)) {
            return DoNothingConverter.INSTANCE;
        }
        
        for (IFValueConverter converter : contributed) {
        	if (converter.canHandle(modelType, presentationType)) {
        		return converter;
        	}
//        	if (converter.canHandle(presentationType, modelType)) {
//        		return (IFValueConverter<M, V>) new Reverser<V, M>(converter);
//        	}
        }
        for (IFValueConverter converter : builtin) {
        	if (converter.canHandle(modelType, presentationType)) {
        		return converter;
        	}
        }
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }

}

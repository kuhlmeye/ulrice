package net.ulrice.databinding.converter;

import java.util.List;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.UlriceDatabinding;
import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.converter.impl.Reverser;
/**
 * 
 * @author apunahassaphemapetilon@hotmail.com
 *
 */
public class ExtensibleConverterFactory implements IFConverterFactory {
	
	private final List<IFValueConverter> converterList;
	
	public ExtensibleConverterFactory(List<IFValueConverter> converterList) {
		this.converterList = converterList;
	}
	
	@SuppressWarnings("unchecked")
	public <M, V> IFValueConverter<M, V> createConverter (Class<V> presentationType, Class<M> modelType) {
        if (presentationType.equals (modelType)) {
            return DoNothingConverter.INSTANCE;
        }
        if (ObjectWithPresentation.class.equals (presentationType)) {
            return DoNothingConverter.INSTANCE;
        }
        
        for (IFValueConverter converter : converterList) {
        	if (converter.getModelType(null).equals(modelType) && converter.getViewType(null).equals(presentationType)) {
        		return converter;
        	}
        	if (converter.getModelType(null).equals(presentationType) && converter.getViewType(null).equals(modelType)) {
        		return (IFValueConverter<M, V>) new Reverser<V, M>(converter);
        	}
        }
        
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }

}

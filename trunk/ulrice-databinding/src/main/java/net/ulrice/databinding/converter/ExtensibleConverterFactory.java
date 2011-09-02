package net.ulrice.databinding.converter;

import java.util.List;

import net.ulrice.databinding.converter.impl.Reverser;
/**
 * 
 * @author apunahassaphemapetilon@hotmail.com
 *
 */
public class ExtensibleConverterFactory implements IFConverterFactory {
	
	@SuppressWarnings("rawtypes")
	private final List<IFValueConverter> converterList;
	
	@SuppressWarnings("rawtypes")
	public ExtensibleConverterFactory(List<IFValueConverter> converterList) {
		this.converterList = converterList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <M, V> IFValueConverter<M, V> createConverter (Class<V> presentationType, Class<M> modelType) {
        for (IFValueConverter converter : converterList) {
        	if (converter.canHandle(modelType, presentationType)) {
        		return converter;
        	}
        	if (converter.canHandle(presentationType, modelType)) {
        		return (IFValueConverter<M, V>) new Reverser<V, M>(converter);
        	}
        }
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }

}

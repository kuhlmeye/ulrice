package net.ulrice.databinding.converter;

/**
 * 
 * @author apunahassaphemapetilon@hotmail.com
 *
 */
public interface IFConverterFactory {

	<M, V> IFValueConverter<M, V> createConverter (Class<V> presentationType, Class<M> modelType);
	
}

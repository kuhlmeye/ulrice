package net.ulrice.databinding.configuration;

/**
 * @author apunahassaphemapetilon@hotmail.com
 */
import java.util.List;

import net.ulrice.databinding.converter.IFConverterFactory;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.viewadapter.impl.factory.ViewAdapterFactoryCallback;

public interface IFUlriceDatabindingConfiguration {
	
	List<IFValueConverter<?, ?>> getValueConverterList();
	
	IFConverterFactory getConverterFactory();

    ViewAdapterFactoryCallback getViewAdapterFactoryCallback();

}

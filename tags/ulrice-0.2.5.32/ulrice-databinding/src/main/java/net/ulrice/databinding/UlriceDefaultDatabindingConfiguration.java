package net.ulrice.databinding;

import java.util.List;

import net.ulrice.databinding.UlriceDatabinding;
import net.ulrice.databinding.configuration.IFUlriceDatabindingConfiguration;
import net.ulrice.databinding.converter.ExtensibleConverterFactory;
import net.ulrice.databinding.converter.IFConverterFactory;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.impl.UlriceValueConverterContributer;

public class UlriceDefaultDatabindingConfiguration implements
		IFUlriceDatabindingConfiguration {
	
	private static List<IFValueConverter<?, ?>> valueConverterList;
	
	private static IFConverterFactory converterFactory;
	
	public static void initialize() {
		new UlriceDefaultDatabindingConfiguration();
	}
	
	public UlriceDefaultDatabindingConfiguration() {
		ExtensibleConverterFactory factory = new ExtensibleConverterFactory();
		valueConverterList = new UlriceValueConverterContributer().contribute();
		for (IFValueConverter<?, ?> converter : new UlriceValueConverterContributer().contribute()) {
			factory.registerConverter(converter);
		}
		converterFactory = factory;
		UlriceDatabinding.initialize(this);
	}

	@Override
	public List<IFValueConverter<?, ?>> getValueConverterList() {
		return valueConverterList;
	}

	@Override
	public IFConverterFactory getConverterFactory() {
		return converterFactory;
	}
}

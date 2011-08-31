package net.ulrice.sample;

import java.util.List;

import net.ulrice.databinding.UlriceDatabinding;
import net.ulrice.databinding.configuration.IFUlriceDatabindingConfiguration;
import net.ulrice.databinding.converter.ExtensibleConverterFactory;
import net.ulrice.databinding.converter.IFConverterFactory;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.impl.UlriceValueConverterContributer;

public class UlriceSampleDatabindingConfiguration implements
		IFUlriceDatabindingConfiguration {
	
	private static List<IFValueConverter> valueConverterList;
	
	private static IFConverterFactory converterFactory;
	
	public static void initialize() {
		new UlriceSampleDatabindingConfiguration();
	}
	
	public UlriceSampleDatabindingConfiguration() {
		valueConverterList = new UlriceValueConverterContributer().contribute();
//		for (IFExtensibleConverterFactoryContributer contributer : contributerList) {
//			valueConverterList.addAll(contributer.contribute());
//		}
		converterFactory = new ExtensibleConverterFactory(valueConverterList);
		UlriceDatabinding.initialize(this);
	}

	@Override
	public List<IFValueConverter> getValueConverterList() {
		return valueConverterList;
	}

	@Override
	public IFConverterFactory getConverterFactory() {
		return converterFactory;
	}

}

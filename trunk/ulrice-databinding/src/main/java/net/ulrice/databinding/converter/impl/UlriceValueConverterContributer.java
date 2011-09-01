package net.ulrice.databinding.converter.impl;

import java.util.Arrays;
import java.util.List;

import net.ulrice.databinding.converter.IFExtensibleConverterFactoryContributer;
import net.ulrice.databinding.converter.IFValueConverter;

public class UlriceValueConverterContributer implements
		IFExtensibleConverterFactoryContributer {
	
	private static final IFValueConverter[] valueConverterArray = {
			new StringToIntegerConverter()
	};

	@Override
	public List<IFValueConverter> contribute() {
		return Arrays.asList(valueConverterArray);
	}

}

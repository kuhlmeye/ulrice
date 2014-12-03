package net.ulrice.databinding.converter;

import java.util.List;

/**
 * 
 * @author apunahassaphemapetilon@hotmail.com
 *
 */
public interface IFExtensibleConverterFactoryContributer {
	
	List<IFValueConverter<?, ?>> contribute();
}

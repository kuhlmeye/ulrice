package net.ulrice.databinding;

/**
 * @author apunahassaphemapetilon@hotmail.com
 */
import net.ulrice.databinding.configuration.IFUlriceDatabindingConfiguration;
import net.ulrice.databinding.converter.IFConverterFactory;

public class UlriceDatabinding {

    private static IFConverterFactory converterFactory;

    public static void initialize(IFUlriceDatabindingConfiguration configuration) {
        UlriceDatabinding.converterFactory = configuration.getConverterFactory();
    }

    public static IFConverterFactory getConverterFactory() {
        return converterFactory;
    }

}

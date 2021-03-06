package net.ulrice.databinding;

/**
 * @author apunahassaphemapetilon@hotmail.com
 */
import net.ulrice.databinding.configuration.IFUlriceDatabindingConfiguration;
import net.ulrice.databinding.converter.IFConverterFactory;
import net.ulrice.databinding.ui.BindingUI;

public class UlriceDatabinding {

    private static IFConverterFactory converterFactory;

    public static void initialize(IFUlriceDatabindingConfiguration configuration) {
    	BindingUI.applyDefaultUI();
        UlriceDatabinding.converterFactory = configuration.getConverterFactory();
    }

    public static IFConverterFactory getConverterFactory() {
        return converterFactory;
    }

}

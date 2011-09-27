package net.ulrice.databinding;

/**
 * @author apunahassaphemapetilon@hotmail.com
 */
import java.util.List;

import net.ulrice.databinding.configuration.IFUlriceDatabindingConfiguration;
import net.ulrice.databinding.converter.IFConverterFactory;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.viewadapter.impl.factory.ViewAdapterFactory;
import net.ulrice.databinding.viewadapter.impl.factory.ViewAdapterFactoryCallback;
import net.ulrice.databinding.viewadapter.impl.factory.ViewAdapterFactoryCallbackImpl;

public class UlriceDatabinding {

    private static IFConverterFactory converterFactory;

    public static void initialize(IFUlriceDatabindingConfiguration configuration) {
        UlriceDatabinding.converterFactory = configuration.getConverterFactory();
        ViewAdapterFactoryCallback callback = configuration.getViewAdapterFactoryCallback();
        if (callback != null) {
            ViewAdapterFactory.setViewAdapterFactoryCallback(callback);
        }
        else {
            ViewAdapterFactory.setViewAdapterFactoryCallback(new ViewAdapterFactoryCallbackImpl());
        }
    }

    public static IFConverterFactory getConverterFactory() {
        return converterFactory;
    }

}

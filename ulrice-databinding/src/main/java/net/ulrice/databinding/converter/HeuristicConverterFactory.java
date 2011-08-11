package net.ulrice.databinding.converter;

import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.converter.impl.Reverser;
import net.ulrice.databinding.converter.impl.StringToIntegerConverter;



public class HeuristicConverterFactory {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <M, V> IFValueConverter<M, V> createConverter (Class<V> presentationType, Class<M> modelType) {
        if (presentationType.equals (modelType)) {
            return DoNothingConverter.INSTANCE;
        }

        // Model: String => View: Int
        if (String.class.equals (modelType) && (Integer.class.equals (presentationType) || Integer.TYPE.equals (presentationType))) {
            return (IFValueConverter) new StringToIntegerConverter();
        }
        
        // Model: Int => View: String
        if ((Integer.class.equals (modelType) || Integer.TYPE.equals (modelType)) && String.class.equals (presentationType)) {
            return (IFValueConverter) new Reverser <Integer, String> (new StringToIntegerConverter());
        }
        
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }
}

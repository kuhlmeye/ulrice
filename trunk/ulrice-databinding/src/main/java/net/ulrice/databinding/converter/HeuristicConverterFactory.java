package net.ulrice.databinding.converter;

import net.ulrice.databinding.ObjectWithPresentation;
import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.converter.impl.GenericStringToNumberConverter;
import net.ulrice.databinding.converter.impl.Reverser;
import net.ulrice.databinding.converter.impl.StringToIntegerConverter;



public class HeuristicConverterFactory implements IFConverterFactory {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <M, V> IFValueConverter<M, V> createConverter (Class<V> presentationType, Class<M> modelType) {
        
        if (presentationType.equals (modelType)) {
            return DoNothingConverter.INSTANCE;
        }
        
        if (ObjectWithPresentation.class.equals (presentationType)) {
            return DoNothingConverter.INSTANCE;
        }

        // Model: Int => View: String
        if (String.class.equals (modelType) && (Integer.class.equals (presentationType) || Integer.TYPE.equals (presentationType))) {
            return (IFValueConverter) GenericStringToNumberConverter.INT;
        }
        
        // Model: String => View: Int
        if ((Integer.class.equals (modelType) || Integer.TYPE.equals (modelType)) && String.class.equals (presentationType)) {
            return (IFValueConverter) new Reverser <String, Integer> (new StringToIntegerConverter());
        }
        
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }
}

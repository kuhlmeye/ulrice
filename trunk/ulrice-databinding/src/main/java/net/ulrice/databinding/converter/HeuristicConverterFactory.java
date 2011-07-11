package net.ulrice.databinding.converter;

import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.converter.impl.IntAsStringConverter;



public class HeuristicConverterFactory {
    public static IFValueConverter createConverter (Class<?> presentationType, Class<?> modelType) {
        if (presentationType.equals (modelType))
            return new DoNothingConverter ();
        
        if (String.class.equals (presentationType) && (Integer.class.equals (modelType) || Integer.TYPE.equals (modelType)))
            return new IntAsStringConverter ();
        
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }
}

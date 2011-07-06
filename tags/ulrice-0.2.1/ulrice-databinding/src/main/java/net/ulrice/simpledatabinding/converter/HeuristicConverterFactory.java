package net.ulrice.simpledatabinding.converter;

import net.ulrice.simpledatabinding.converter.impl.DoNothingConverter;
import net.ulrice.simpledatabinding.converter.impl.IntAsStringConverter;



public class HeuristicConverterFactory {
    public static ValueConverter createConverter (Class<?> presentationType, Class<?> modelType) {
        if (presentationType.equals (modelType))
            return new DoNothingConverter ();
        
        if (String.class.equals (presentationType) && (Integer.class.equals (modelType) || Integer.TYPE.equals (modelType)))
            return new IntAsStringConverter ();
        
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }
}

package net.ulrice.databinding.converter;

import net.ulrice.databinding.converter.impl.DoNothingConverter;
import net.ulrice.databinding.converter.impl.Reverser;
import net.ulrice.databinding.converter.impl.StringToIntegerConverter;



public class HeuristicConverterFactory {
    public static IFValueConverter createConverter (Class<?> presentationType, Class<?> modelType) {
        if (presentationType.equals (modelType)) {
            return new DoNothingConverter ();
        }

        // Model: String => View: Int
        if (String.class.equals (modelType) && (Integer.class.equals (presentationType) || Integer.TYPE.equals (presentationType))) {
            return new StringToIntegerConverter();
        }
        
        // Model: Int => View: String
        if ((Integer.class.equals (modelType) || Integer.TYPE.equals (modelType)) && String.class.equals (presentationType)) {
            return new Reverser(new StringToIntegerConverter());
        }
        
        throw new IllegalArgumentException ("keine Implizite Konvertierung von " + presentationType.getName () + " in " + modelType.getName () + ".");
    }
}

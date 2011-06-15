package net.ulrice.simpledatabinding.converter.impl;

import net.ulrice.simpledatabinding.converter.ValueConverter;
import net.ulrice.simpledatabinding.converter.ValueConverterException;



public class IntAsStringConverter implements ValueConverter {
    public Object modelToView (Object o) {
        if (o == null)
            return "";
        
        return String.valueOf (o);
    }

    public Object viewToModel (Object o) {
        if (o == null || "".equals (((String) o).trim ()))
            return null;
        
        try {
            return new Integer ((String) o);
        }
        catch (NumberFormatException exc) {
            throw new ValueConverterException ();
        }
    }
}

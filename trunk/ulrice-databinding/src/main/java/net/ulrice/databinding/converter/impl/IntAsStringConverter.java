package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.converter.ValueConverterException;



public class IntAsStringConverter implements IFValueConverter {
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

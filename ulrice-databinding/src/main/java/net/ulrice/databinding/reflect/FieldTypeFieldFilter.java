package net.ulrice.databinding.reflect;

import java.lang.reflect.Field;

public class FieldTypeFieldFilter implements FieldFilter {

    private final Class<?> type;
    
    public FieldTypeFieldFilter(final Class<?> type) {
        this.type = type;
    }

    public boolean accept(final Field aField) {
        final Class<?> aType = aField.getType();
        return aType == this.type;
    }
}

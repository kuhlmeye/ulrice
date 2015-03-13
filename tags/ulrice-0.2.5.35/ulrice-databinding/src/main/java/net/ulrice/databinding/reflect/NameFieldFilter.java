package net.ulrice.databinding.reflect;

import java.lang.reflect.Field;

public class NameFieldFilter implements FieldFilter {

    private final String fieldName;

    public NameFieldFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean accept(Field aField) {
        return fieldName.equals(aField.getName());
    }
}

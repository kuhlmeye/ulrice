package net.ulrice.simpledatabinding.converter.impl;

import net.ulrice.simpledatabinding.converter.ValueConverter;



public class DoNothingConverter implements ValueConverter {
    public Object modelToView (Object o) {
        return o;
    }

    public Object viewToModel (Object o) {
        return o;
    }
}

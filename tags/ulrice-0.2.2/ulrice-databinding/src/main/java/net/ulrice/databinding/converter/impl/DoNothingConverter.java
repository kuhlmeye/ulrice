package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;



public class DoNothingConverter implements IFValueConverter {
    public Object modelToView (Object o) {
        return o;
    }

    public Object viewToModel (Object o) {
        return o;
    }
}

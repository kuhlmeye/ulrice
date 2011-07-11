package net.ulrice.databinding.converter.impl;

import net.ulrice.databinding.converter.IFValueConverter;

/**
 * String to Integer converter
 *
 * @author andre
 *
 */
public class StringToIntegerConverter implements IFValueConverter {

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.databinding.IFConverter#mapToSource(java.lang.Object)
     */
    @Override
    public Object viewToModel(Object view) {
        return view.toString();
    }

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.databinding.IFConverter#mapToTarget(java.lang.Object)
     */
    @Override
    public Object modelToView(Object model) {
        if (model == null || model.toString().equalsIgnoreCase("")){
            return 0;
        }
        try{
            return Integer.valueOf(model.toString());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

}

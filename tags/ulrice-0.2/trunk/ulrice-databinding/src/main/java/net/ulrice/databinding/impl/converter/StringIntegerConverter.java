package net.ulrice.databinding.impl.converter;

import net.ulrice.databinding.IFConverter;

/**
 * String to Integer converter
 *
 * @author andre
 *
 */
public class StringIntegerConverter implements IFConverter<String, Integer>{

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.databinding.IFConverter#mapToSource(java.lang.Object)
     */
    @Override
    public String mapToSource(Integer target) {
        return target.toString();
    }

    /**
     * 
     * {@inheritDoc}
     * @see net.ulrice.databinding.IFConverter#mapToTarget(java.lang.Object)
     */
    @Override
    public Integer mapToTarget(String source) {
        if (source == null || source.equalsIgnoreCase("")){
            return 0;
        }
        try{
            return Integer.valueOf(source);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

}

package net.ulrice.databinding.converter;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;


/**
 * converts a value for presentation
 * 
 * @author arno
 */
public interface IFValueConverter <M, V> {
    /**
     * These two get...Type methods may assume to be called only for types they can actually handle
     */
    Class<? extends V> getViewType(Class<? extends M> modelType);
    Class<? extends M> getModelType(Class<? extends V> viewType);
    
    boolean canHandle(Class<? extends Object> modelType, Class<? extends Object> viewType);
    
    M viewToModel (V o, IFAttributeInfo attributeInfo);
    V modelToView (M o, IFAttributeInfo attributeInfo);
}

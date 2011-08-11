package net.ulrice.databinding.converter;


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
    
    M viewToModel (V o);
    V modelToView (M o);
}

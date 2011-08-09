package net.ulrice.databinding.converter;


/**
 * converts a value for presentation
 * 
 * @author arno
 */
public interface IFValueConverter {
    /**
     * These two get...Type methods may assume to be called only for types they can actually handle
     */
    Class<?> getViewType(Class<?> modelType);
    Class<?> getModelType(Class<?> viewType);
    
    Object viewToModel (Object o);
    Object modelToView (Object o);
}

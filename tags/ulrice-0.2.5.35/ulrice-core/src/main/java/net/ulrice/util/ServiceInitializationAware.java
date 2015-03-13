package net.ulrice.util;

/**
 * @author Stefan Huber
 */
public interface ServiceInitializationAware {

    /**
     * The implementation has to ensure that the bean of given type is initialized
     */
    public void ensureBeanOfType(Class<?> type);

}

package net.ulrice.databinding.modelaccess;


public interface IFIndexedModelValueAccessor {
    Object getValue (int index);
    void setValues (Object values);
    
    /**
     * This method is for use by the 'direct binding' framework only. Use the 'setValues' 
     *  method instead where possible to allow MVA implementations more flexibility and
     *  optimizations.
     */
    void setValue (int index, Object value);
    
    int getSize();
    
    boolean isReadOnly ();
    Class<?> getModelType ();
    
    String getAttributeId();
    
    Object newObjectInstance();
    
    Object cloneObject(Object obj);
}

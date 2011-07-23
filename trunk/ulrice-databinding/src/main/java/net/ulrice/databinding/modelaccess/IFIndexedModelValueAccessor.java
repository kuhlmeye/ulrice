package net.ulrice.databinding.modelaccess;


public interface IFIndexedModelValueAccessor {
    Object getValue (int index);
    void setValue (int index, Object value);
    
    int getSize();
    
    boolean isReadOnly ();
    Class<?> getModelType ();
    
    String getAttributeId();
    
    Object newObjectInstance();
    
    Object cloneObject(Object obj);
}

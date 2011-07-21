package net.ulrice.databinding.modelaccess;


public interface IFIndexedModelValueAccessor {
    Object getValue (int index);
    void setValue (int index, Object value);
    
    boolean isReadOnly ();
    Class<?> getModelType ();
    
    String getAttributeId();
}

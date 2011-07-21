package net.ulrice.databinding.modelaccess;


public interface IFModelValueAccessor {
    Object getValue ();
    void setValue (Object value);
    
    boolean isReadOnly ();
    Class<?> getModelType ();
    
    String getAttributeId();
}

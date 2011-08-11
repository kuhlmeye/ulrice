package net.ulrice.databinding.modelaccess;


public interface IFModelValueAccessor <M> {
    M getValue ();
    void setValue (M value);
    
    boolean isReadOnly ();
    Class<?> getModelType ();
    
    String getAttributeId();
}

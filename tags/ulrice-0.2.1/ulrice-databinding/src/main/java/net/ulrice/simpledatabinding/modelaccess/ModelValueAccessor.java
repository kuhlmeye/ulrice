package net.ulrice.simpledatabinding.modelaccess;


public interface ModelValueAccessor {
    Object getValue ();
    void setValue (Object value);
    
    boolean isReadOnly ();
    Class<?> getModelType ();
}

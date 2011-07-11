package net.ulrice.databinding.modelaccess;


public interface IndexedModelValueAccessor {
    Object getValue (int index);
    void setValue (int index, Object value);
    
    boolean isReadOnly ();
    Class<?> getModelType ();
}

package net.ulrice.simpledatabinding.viewaccess;

import java.util.List;


public interface ViewAdapter {
    void setValue (Object value);
    Object getValue ();

    Class<?> getViewType ();
    boolean isReadOnly ();
    
    /**
     * eine leere Liste bedeutet eine erfolgreiche Validierung
     */
    void setValidationFailures (List<String> messages);
    
    void setEnabled (boolean enabled);
    
    void addViewChangeListener (ViewChangeListener l);
    void removeViewChangeListener (ViewChangeListener l);
}

package net.ulrice.simpledatabinding.viewaccess;


public interface IndexedViewAdapter {
    Object getValue (int index);
    void setValue (int index, Object value);

    Class<?> getViewType ();
    boolean isReadOnly ();
    
    /**
     * eine leere Liste bedeutet eine erfolgreiche Validierung
     */
//TODO    void setValidationFailures (List<String> messages);
    
//TODO    void setEnabled (boolean enabled);
    
//    void addViewChangeListener (ViewChangeListener l);
//    void removeViewChangeListener (ViewChangeListener l);
}

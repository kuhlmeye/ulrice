package net.ulrice.simpledatabinding.viewaccess;

import java.util.ArrayList;
import java.util.List;



public abstract class AbstractViewAdapter implements ViewAdapter {
    private final Class<?> _viewType;
    private final boolean _isReadOnly;
    private final List<ViewChangeListener> _listeners = new ArrayList<ViewChangeListener> ();

    public AbstractViewAdapter (Class<?> viewType, boolean isReadOnly) {
        _viewType = viewType;
        _isReadOnly = isReadOnly;
    }
    
    public Class<?> getViewType () {
        return _viewType;
    }
    
    public boolean isReadOnly () {
        return _isReadOnly;
    }
    
    protected void fireViewChange () {
        for (ViewChangeListener l: _listeners)
            l.viewValueChanged ();
    }
    
    @Override
    public void addViewChangeListener (ViewChangeListener l) {
        _listeners.add (l);
    }


    @Override
    public void removeViewChangeListener (ViewChangeListener l) {
        _listeners.remove (l);
    }
}

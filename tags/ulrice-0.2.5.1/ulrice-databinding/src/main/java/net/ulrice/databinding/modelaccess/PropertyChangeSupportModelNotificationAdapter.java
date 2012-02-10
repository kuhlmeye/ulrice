package net.ulrice.databinding.modelaccess;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;



public class PropertyChangeSupportModelNotificationAdapter implements ModelNotificationAdapter {
    private final List<ModelChangeListener> _listeners = new ArrayList<ModelChangeListener> ();
    private final Object _model;

    private final PropertyChangeListener _pcl = new PropertyChangeListener() {
        public void propertyChange (PropertyChangeEvent evt) {
            for (ModelChangeListener l: _listeners)
                l.modelChanged ();
        }
    };

    public PropertyChangeSupportModelNotificationAdapter (Object model) {
        _model = model;
    }

    private void addRemovePcl (String prefix) {
        try {
            final Method m = _model.getClass ().getMethod (prefix + "PropertyChangeListener", PropertyChangeListener.class);
            m.invoke (_model, _pcl);
        }
        catch (Exception exc) {
            throw new IllegalArgumentException ("kein Getter / Setter für PropertyChangeListener im Model " + _model.getClass ().getName () + ".");
        }
    }

    @Override
    public void addModelChangeListener (ModelChangeListener l) {
        if (_listeners.isEmpty ()) 
            addRemovePcl ("add");

        _listeners.add (l);
    }

    @Override
    public void removeModelChangeListener (ModelChangeListener l) {
        _listeners.remove (l);

        if (_listeners.isEmpty ()) 
            addRemovePcl ("remove");
    }
}

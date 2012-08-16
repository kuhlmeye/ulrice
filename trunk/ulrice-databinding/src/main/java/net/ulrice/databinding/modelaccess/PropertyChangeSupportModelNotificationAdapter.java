package net.ulrice.databinding.modelaccess;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;



public class PropertyChangeSupportModelNotificationAdapter implements ModelNotificationAdapter {
    private final List<ModelChangeListener> listeners = new ArrayList<ModelChangeListener> ();
    private final Object model;

    private final PropertyChangeListener pcl = new PropertyChangeListener() {
        public void propertyChange (PropertyChangeEvent evt) {
            for (ModelChangeListener l: listeners)
                l.modelChanged ();
        }
    };

    public PropertyChangeSupportModelNotificationAdapter (Object model) {
    	this.model = model;
    }

    private void addRemovePcl (String prefix) {
        try {
            final Method m = model.getClass ().getMethod (prefix + "PropertyChangeListener", PropertyChangeListener.class);
            m.invoke (model, pcl);
        }
        catch (Exception exc) {
            throw new IllegalArgumentException ("kein Getter / Setter für PropertyChangeListener im Model " + model.getClass ().getName () + ".");
        }
    }

    @Override
    public void addModelChangeListener (ModelChangeListener l) {
        if (listeners.isEmpty ()) 
            addRemovePcl ("add");

        listeners.add (l);
    }

    @Override
    public void removeModelChangeListener (ModelChangeListener l) {
        listeners.remove (l);

        if (listeners.isEmpty ()) 
            addRemovePcl ("remove");
    }
}

package net.ulrice.simpledatabinding.modelaccess;


public interface ModelNotificationAdapter {
    void addModelChangeListener (ModelChangeListener l);
    void removeModelChangeListener (ModelChangeListener l);
}

package net.ulrice.databinding.bufferedbinding.impl;

/**
 * 
 * Adapter for the {@link ElementLifecycleListener}
 * Extend this adapter if you need just one of the Methods from the Listener
 * 
 * @author rad
 *
 */
public class ElementLifecycleAdapter implements ElementLifecycleListener{

    @Override
    public void elementChanged(TableAM table, Element element, String columnId) {   
    }

    @Override
    public void elementAdded(TableAM table, Element element) {
    }

    @Override
    public void elementRemoved(TableAM table, Element element) {
    }

    @Override
    public void elementStateChanged(TableAM table, Element element) {
    }

    @Override
    public void tableCleared(TableAM table) {
    }

}

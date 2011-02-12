package net.ulrice.databinding;

import javax.swing.JComponent;

/**
 * Interface for all classes marking the state on jcomponents.
 * 
 * @author christof
 */
public interface IFStateMarker {

    void paintState(JComponent component, DataState state);
    
}

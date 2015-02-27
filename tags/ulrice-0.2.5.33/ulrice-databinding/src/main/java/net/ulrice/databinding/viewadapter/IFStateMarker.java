package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

/**
 * Interface for all classes marking the state on jcomponents.
 * 
 * @author christof
 */
public interface IFStateMarker {

	void initialize(JComponent component);
    void updateState(Object value, boolean editable, boolean dirty, boolean valid, JComponent component);
    
}

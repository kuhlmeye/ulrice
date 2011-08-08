package net.ulrice.databinding.viewadapter;

import javax.swing.JComponent;

import net.ulrice.databinding.IFBinding;

/**
 * Interface for all classes marking the state on jcomponents.
 * 
 * @author christof
 */
public interface IFStateMarker {

	void initialize(JComponent component);
    void updateState(IFBinding binding, JComponent component);
    
}

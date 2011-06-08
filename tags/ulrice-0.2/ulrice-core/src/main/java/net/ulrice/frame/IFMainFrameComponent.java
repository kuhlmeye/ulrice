package net.ulrice.frame;

import javax.swing.JComponent;

/**
 * Component that could be shown on the main frame.
 * 
 * @author ckuhlmeyer
 */
public interface IFMainFrameComponent {

	/** The id of the component. */
	String getComponentId();
	
	/** The view component. */
	JComponent getView();	
}

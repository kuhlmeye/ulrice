package net.ulrice.frame;

import javax.swing.JComponent;

import net.ulrice.module.event.IFModuleEventListener;

/**
 * Interface for a class representing a workarea. The purpose of the workarea is to display the views of the
 * controllers.
 * 
 * @author ckuhlmeyer
 */
public interface IFWorkarea extends IFMainFrameComponent, IFModuleEventListener {

	/**
	 * Returns the gui component showing the workarea.
	 * 
	 * @return The workarea.
	 */
	JComponent getView();

	/**
	 * This method is called by the mainframe before this workarea is used by the frame
	 */
	void onActivateWorkarea();

	/**
	 * This method is called by the mainframe if this workarea is not more in use by the frame
	 */
	void onDeactivateWorkarea();
}

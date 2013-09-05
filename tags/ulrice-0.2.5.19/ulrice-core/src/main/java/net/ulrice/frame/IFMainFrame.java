package net.ulrice.frame;

import javax.swing.JFrame;

import net.ulrice.appprefs.IFAppPrefs;
import net.ulrice.configuration.UlriceConfigurationCallback;

/**
 * Interface for the ulrice main frame.
 * 
 * @author ckuhlmeyer
 */
public interface IFMainFrame {

	/**
	 * Returns the main frame of ulrice.
	 * 
	 * @return The main frame.
	 */
	JFrame getFrame();

	IFWorkarea getWorkarea();
	
	/**
	 * Initialize the main frame after setting up the configuration is finished. This is called by the ulrice class.
	 */
	void inializeLayout(final IFAppPrefs appPrefs, UlriceConfigurationCallback configurationCallback);
}

/**
 * 
 */
package net.ulrice.frame.impl;

import javax.swing.JMenuBar;

import net.ulrice.Ulrice;
import net.ulrice.configuration.UlriceConfigurationCallback;
import net.ulrice.module.event.IFModuleActionManagerEventListener;

/**
 * The menubar.
 * 
 * @author christof
 */
public class Menubar extends JMenuBar implements IFModuleActionManagerEventListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 1380850325014925542L;
	private UlriceConfigurationCallback configurationCallback;

	/**
	 * Creates a new toolbar.
	 * @param configurationCallback 
	 */
	public Menubar(UlriceConfigurationCallback configurationCallback) {
		this.configurationCallback = configurationCallback;
		actionsChanged();
		Ulrice.getActionManager().addModuleActionManagerEventListener(this);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleActionManagerEventListener#applicationActionsChanged()
	 */
	@Override
	public void applicationActionsChanged() {
		actionsChanged();
	}
	
	@Override
	public void moduleActionsChanged() {
		actionsChanged();
	}

	private void actionsChanged() {
		removeAll();

		if(configurationCallback != null) {
			configurationCallback.configureMenu(this, Ulrice.getActionManager());
		}
		
		doLayout();
	}
	
}

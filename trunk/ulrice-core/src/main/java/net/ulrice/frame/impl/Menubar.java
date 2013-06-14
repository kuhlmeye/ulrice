/**
 * 
 */
package net.ulrice.frame.impl;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.ulrice.Ulrice;
import net.ulrice.module.event.IFModuleActionManagerEventListener;
import net.ulrice.module.impl.action.CloseAllModulesAction;
import net.ulrice.module.impl.action.CloseModuleAction;
import net.ulrice.module.impl.action.ExitApplicationAction;
import net.ulrice.module.impl.action.ModuleActionManager;

/**
 * The menubar.
 * 
 * @author christof
 */
public class Menubar extends JMenuBar implements IFModuleActionManagerEventListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = 1380850325014925542L;

	/**
	 * Creates a new toolbar.
	 */
	public Menubar() {
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
		ModuleActionManager actionManager = Ulrice.getActionManager();
		
		JMenu file = new JMenu("File");
		file.add(new JMenuItem(actionManager.getApplicationAction(CloseModuleAction.ACTION_ID)));
		file.add(new JMenuItem(actionManager.getApplicationAction(CloseAllModulesAction.ACTION_ID)));		
		file.add(new JMenuItem(actionManager.getApplicationAction(ExitApplicationAction.ACTION_ID)));				

		add(file);
		
		doLayout();
	}
	
}

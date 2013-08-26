package net.ulrice.sample.module.laflist;

import javax.swing.JComponent;

import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.action.ModuleAction;

/**
 * Controller for the module showing the current look and feel constants.
 * 
 * @author christof
 */
public class LafListController extends AbstractController {

	private final LafListModel model = new LafListModel();
	private final LafListView view = new LafListView(model);
	
	@Override
	public void postCreate() {
		super.postCreate();
		refresh();		
	}

	@ModuleAction(actionId = "Refresh", iconName = "refresh.gif", initiallyEnabled = false)
	public void refresh() {
		model.refresh();
		postInfoMessage("Look and feel constants loaded.");
	}
	public JComponent getView() {
	    return view;
	}
}

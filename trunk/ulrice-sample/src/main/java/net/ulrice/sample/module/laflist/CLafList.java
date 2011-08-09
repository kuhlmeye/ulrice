/**
 * 
 */
package net.ulrice.sample.module.laflist;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;

import net.ulrice.Ulrice;
import net.ulrice.module.impl.AbstractController;
import net.ulrice.module.impl.ModuleActionState;
import net.ulrice.module.impl.action.AuthModuleAction;
import net.ulrice.sample.SampleSecurityCallback;
import net.ulrice.security.Authorization;

/**
 * Controller for the module showing the current look and feel constants.
 * 
 * @author christof
 */
public class CLafList extends AbstractController {

	private static final String REFRESH_ACTION = "REFRESH";

	private final MLafList model = new MLafList();
	private final VLafList view = new VLafList();
	
	/**
	 * @see net.ulrice.module.impl.AbstractController#postCreationEvent(net.ulrice.module.IFModule)
	 */
	@Override
	public void postCreate() {
		super.postCreate();
		refresh();
	}

	/**
	 * 
	 */
	private void refresh() {
		postInfoMessage("Loading look and feel constants loaded...");

		SortedSet<Object> lafKeySet = new TreeSet<Object>(new StringComparator());

		lafKeySet.addAll(UIManager.getLookAndFeel().getDefaults().keySet());
		lafKeySet.addAll(UIManager.getDefaults().keySet());
		
		for (Object key : lafKeySet) {
			Object value = UIManager.get(key);
			model.addValue(key == null ? "" : key.toString(), value == null ? "" : value.toString());
		}

		view.getTable().setModel(model);
		
		postInfoMessage("Look and feel constants loaded.");
	}
	
	/**
	 * @see net.ulrice.module.impl.AbstractController#performModuleAction(java.lang.String)
	 */
	@Override
	public boolean performModuleAction(String actionId) {
		if(REFRESH_ACTION.equals(actionId)) {
			refresh();
			return true;
		}
		return false;
	}
	
	@Override
	public List<ModuleActionState> getHandledActions() {
		Icon refreshIcon = new ImageIcon(getClass().getResource("refresh.gif"));
		
		AuthModuleAction refreshAction = new AuthModuleAction(REFRESH_ACTION, "Refresh", true, refreshIcon);
		refreshAction.setAuthorization(new Authorization(SampleSecurityCallback.TYPE_EXECUTE_ACTION, "LAFLIST_REFRESH_EXEC"));

		return Arrays.asList(new ModuleActionState(true, this, Ulrice.getActionManager().getApplicationAction("TEST1")), new ModuleActionState(true, this, refreshAction));
	}
	
	public JComponent getView() {
	    return view.getView();
	}

	/***
	 * Comparator used to sort the look and feel constants.
	 * 
	 * @author christof
	 */
	class StringComparator implements Comparator<Object> {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Object o1, Object o2) {
			return o1.toString().compareTo(o2.toString());
		}
	}
}

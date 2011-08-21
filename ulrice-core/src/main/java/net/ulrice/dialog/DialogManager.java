package net.ulrice.dialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.event.AbstractModuleEventAdapter;

/**
 * Manages the display of dialogs shown by modules.
 * 
 * @author DL10KUH
 */
public class DialogManager {

	private Map<IFController, List<DialogInformation>> ctrlDialogMap;
	private Map<JDialog, DialogInformation> dlgInfoDialogMap;

	public enum DialogMode {
		/** Dialog is blocking the whole application. */
		ApplicationModal,

		/** Dialog is blocking only the module. */
		ModuleModal,

		/** Dialog is not modal. */
		NonModal
	}

	public DialogManager() {
		Ulrice.getModuleManager().addModuleEventListener(
				new ModuleEventListener());

		ctrlDialogMap = new HashMap<IFController, List<DialogInformation>>();
		dlgInfoDialogMap = new HashMap<JDialog, DialogManager.DialogInformation>();
	}

	/**
	 * Show a dialog within a controller.
	 * 
	 * @param controller
	 *            The controller to which the dialog belongs.
	 * @param dialog
	 *            The dialog that should be shown.
	 * @param mode
	 *            The mode in which the dialog should be shown.
	 */
	public void showDialog(IFController controller, JDialog dialog,
			DialogMode mode) {
		DialogInformation dlgInfo = new DialogInformation();
		dlgInfo.dialog = dialog;
		dlgInfo.mode = mode;
		dlgInfo.ctrl = controller;

		dialog.addWindowListener(new DialogWindowListener());

		switch (mode) {
		case ApplicationModal:
			dialog.setModal(true);
			Ulrice.getModuleManager().block (controller, dialog);
			break;
		case ModuleModal:
			dialog.setAlwaysOnTop(true);
			Ulrice.getModuleManager().block (controller, dialog);
			break;
		case NonModal:
			break;
		}
		List<DialogInformation> list = ctrlDialogMap.get(controller);
		list.add(dlgInfo);

		if(Ulrice.getModuleManager().getCurrentController() == controller) {
		    dialog.setVisible(true);
		}
		
		dlgInfoDialogMap.put(dialog, dlgInfo);
	}

	private class DialogInformation {
		private DialogMode mode;
		private JDialog dialog;
		private IFController ctrl;
	}

	private class ModuleEventListener extends AbstractModuleEventAdapter {

		@Override
		public void openModule(IFController controller) {
			ctrlDialogMap.put(controller, new ArrayList<DialogInformation>());
		}

		@Override
		public void closeController(IFController controller) {
			List<DialogInformation> dialogs = ctrlDialogMap.get(controller);
			if (dialogs != null) {
				for (DialogInformation dialogInfo : dialogs) {
					dialogInfo.dialog.dispose();
					dlgInfoDialogMap.remove(dialogInfo.dialog);
				}
			}
			ctrlDialogMap.remove(controller);
		}

		@Override
		public void activateModule(IFController controller) {
			List<DialogInformation> dialogs = ctrlDialogMap.get(controller);
			for (DialogInformation dialogInfo : dialogs) {
				dialogInfo.dialog.setVisible(true);
			}
		}

		@Override
		public void deactivateModule(IFController controller) {
			List<DialogInformation> dialogs = ctrlDialogMap.get(controller);
			if (dialogs != null) {
				for (DialogInformation dialogInfo : dialogs) {
					dialogInfo.dialog.setVisible(false);
				}
			}
		}
	}

	private class DialogWindowListener extends WindowAdapter {

		@Override
		public void windowClosed(WindowEvent e) {
			if (e.getSource() instanceof JDialog) {
				JDialog dialog = (JDialog) e.getSource();
				DialogInformation dlgInfo = dlgInfoDialogMap.remove(dialog);
				if (dlgInfo != null) {
					// Dialog was already removed by closeModule
					switch (dlgInfo.mode) {
					case ApplicationModal:
					case ModuleModal:
					    Ulrice.getModuleManager().unblock(dlgInfo.ctrl, dialog);
						break;
					default:
						break;
					}
					ctrlDialogMap.get(dlgInfo.ctrl).remove(dlgInfo);
				}
			}
		}
	}
}

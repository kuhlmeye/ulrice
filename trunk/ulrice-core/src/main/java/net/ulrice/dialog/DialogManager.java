package net.ulrice.dialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import net.ulrice.ConfigurationListener;
import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.event.AbstractModuleEventAdapter;

/**
 * Manages the display of dialogs shown by modules.
 * 
 * @author DL10KUH
 */
public class DialogManager {

	private Map<IFController, List<DialogInformation>> ctrlDialogMap = new HashMap<IFController, List<DialogInformation>>();
	private Map<JDialog, DialogInformation> dlgInfoDialogMap = new HashMap<JDialog, DialogManager.DialogInformation>();

	public enum DialogMode {
		/** Dialog is blocking the whole application. */
		ApplicationModal,

		/** Dialog is blocking only the module. */
		ModuleModal,

		/** Dialog is not modal. */
		NonModal
	}
	private MainFrameWindowListener windowListener = new MainFrameWindowListener();

	public DialogManager() {		
		Ulrice.addConfigurationListener(new ConfigurationListener() {			
			@Override
			public void initializationFinished() {
				Ulrice.getModuleManager().addModuleEventListener(new ModuleEventListener());
				Ulrice.getMainFrame().getFrame().addWindowListener(windowListener);
			}
		});
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
	public void showDialog(IFController controller, JDialog dialog, DialogMode mode) {
		DialogInformation dlgInfo = new DialogInformation();
		dlgInfo.dialog = dialog;
		dlgInfo.mode = mode;
		dlgInfo.ctrl = controller;

		dialog.addWindowListener(windowListener);

		switch (mode) {
		case ApplicationModal:
			dialog.setModal(true);
			Ulrice.getModuleManager().addBlocker (controller, dialog);
			break;
		case ModuleModal:
			dialog.setFocusableWindowState(false);
			Ulrice.getModuleManager().addBlocker (controller, dialog);
			break;
		case NonModal:
			break;
		}
		List<DialogInformation> list = ctrlDialogMap.get(controller);
		list.add(dlgInfo);
		dlgInfoDialogMap.put(dialog, dlgInfo);

		if(Ulrice.getModuleManager().getCurrentController() == controller) {
            dialog.setLocationRelativeTo(Ulrice.getMainFrame().getWorkarea().getView());
    		dialog.setVisible(true);
		}
	}

	private void showAllDialogs(IFController controller) {
		List<DialogInformation> dialogs = ctrlDialogMap.get(controller);
		for (DialogInformation dialogInfo : dialogs) {
			dialogInfo.dialog.setVisible(true);
		}
	}

	private void hideAllDialogs(IFController controller) {
		List<DialogInformation> dialogs = ctrlDialogMap.get(controller);
		if (dialogs != null) {
			for (DialogInformation dialogInfo : dialogs) {
				dialogInfo.dialog.setVisible(false);
			}
		}
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
			showAllDialogs(controller);
		}

		@Override
		public void deactivateModule(IFController controller) {
			hideAllDialogs(controller);
		}

	}
	
	private class MainFrameWindowListener extends WindowAdapter {

		private boolean isInEventHandling = false;

		@Override
		public void windowActivated(WindowEvent e) {
			super.windowActivated(e);
			if(isInEventHandling) {
				return;
			}
			isInEventHandling = true;
			
			if(isMainFrameEvent(e)) {				
				List<DialogInformation> list = ctrlDialogMap.get(Ulrice.getModuleManager().getCurrentController());				
				if(list != null) {
					for(DialogInformation item : list) {
						item.dialog.toFront();
					}
				}
			}
			isInEventHandling = false;
		}
		
		@Override
		public void windowDeactivated(WindowEvent e) {
			super.windowDeactivated(e);
			if(isInEventHandling) {
				return;
			}
			isInEventHandling = true;
			
			if(isMainFrameEvent(e)) {				
				List<DialogInformation> list = ctrlDialogMap.get(Ulrice.getModuleManager().getCurrentController());				
				if(list != null) {
					for(DialogInformation item : list) {
						item.dialog.toFront();
					}
				}
			}
			isInEventHandling = false;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			e.getWindow().dispose();
		}

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
					    Ulrice.getModuleManager().removeBlocker(dlgInfo.ctrl, dialog);
						break;
					default:
						break;
					}
					ctrlDialogMap.get(dlgInfo.ctrl).remove(dlgInfo);
				}
			}			
		}

		private boolean isMainFrameEvent(WindowEvent e) {
			return e.getSource() == Ulrice.getMainFrame().getFrame();
		}
	}
}

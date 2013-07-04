package net.ulrice.dialog;

import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import net.ulrice.ConfigurationListener;
import net.ulrice.Ulrice;
import net.ulrice.frame.IFMainFrame;
import net.ulrice.module.IFController;
import net.ulrice.module.event.AbstractModuleEventAdapter;

/**
 * Manages the display of dialogs shown by modules.
 * 
 * @author DL10KUH
 */
public class DialogManager {

    private final Map<IFController, List<DialogInformation>> ctrlDialogMap =
            new HashMap<IFController, List<DialogInformation>>();
    private final Map<JDialog, DialogInformation> dlgInfoDialogMap =
            new HashMap<JDialog, DialogManager.DialogInformation>();

    public enum DialogMode {
        /** Dialog is blocking the whole application. */
        ApplicationModal,

        /** Dialog is blocking only the module. */
        ModuleModal,

        /** Dialog is not modal. */
        NonModal
    }

    private final MainFrameWindowListener windowListener = new MainFrameWindowListener();

    private final MainFrameFocusListener focusListener = new MainFrameFocusListener();

    public DialogManager() {
        Ulrice.addConfigurationListener(new ConfigurationListener() {
            @Override
            public void initializationFinished() {
                Ulrice.getModuleManager().addModuleEventListener(new ModuleEventListener());

                IFMainFrame mainFrame = Ulrice.getMainFrame();
                if (mainFrame != null) {
                    mainFrame.getFrame().addWindowListener(windowListener);
                }
            }
        });
    }

    /**
     * Show a dialog within a controller.
     * 
     * @param controller The controller to which the dialog belongs.
     * @param dialog The dialog that should be shown.
     * @param mode The mode in which the dialog should be shown.
     */
    public void showDialog(IFController controller, JDialog dialog, DialogMode mode) {
        DialogInformation dlgInfo = new DialogInformation();
        dlgInfo.dialog = dialog;
        dlgInfo.mode = mode;
        dlgInfo.ctrl = controller;

        dialog.addWindowListener(windowListener);
        dialog.addFocusListener(focusListener);

        switch (mode) {
            case ApplicationModal:
                dialog.setModal(true);
                Ulrice.getModuleManager().addBlocker(controller, dialog);
                break;
            case ModuleModal:
                dialog.setFocusableWindowState(true);
                Ulrice.getModuleManager().addBlocker(controller, dialog);
                break;
            case NonModal:
                break;
        }
        List<DialogInformation> list = ctrlDialogMap.get(controller);
        list.add(dlgInfo);
        dlgInfoDialogMap.put(dialog, dlgInfo);

        if (Ulrice.getModuleManager().getCurrentController() == controller) {
            dialog.setLocationRelativeTo(Ulrice.getMainFrame().getWorkarea().getView());
            dialog.setVisible(true);
        }
    }

    private void showAllDialogs(IFController controller) {
        List<DialogInformation> dialogs = ctrlDialogMap.get(controller);
        if (dialogs != null) {
            for (DialogInformation dialogInfo : dialogs) {
                dialogInfo.dialog.setVisible(true);
            }
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

    private class MainFrameFocusListener extends FocusAdapter {

        private boolean isInEventHandling = false;
        
        @Override
        public void focusLost(FocusEvent e) {
            super.focusLost(e);
            if (isInEventHandling) {
                return;
            }
            isInEventHandling = true;

            if (isMainFrameEvent(e)) {
                List<DialogInformation> list = ctrlDialogMap.get(Ulrice.getModuleManager().getCurrentController());
                if (list != null) {
                    for (DialogInformation item : list) {
                        item.dialog.toFront();
                    }
                }
            }
            isInEventHandling = false;
        }
        
    }
        
    private class MainFrameWindowListener extends WindowAdapter {

        private boolean isInEventHandling = false;

        @Override
        public void windowActivated(WindowEvent e) {
            super.windowActivated(e);
            if (isInEventHandling) {
                return;
            }
            isInEventHandling = true;

            if (isMainFrameEvent(e)) {
                List<DialogInformation> list = ctrlDialogMap.get(Ulrice.getModuleManager().getCurrentController());
                if (list != null) {
                    for (DialogInformation item : list) {
                        item.dialog.toFront();
                    }
                }
            }
            isInEventHandling = false;
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            super.windowDeactivated(e);
            if (isInEventHandling) {
                return;
            }
            isInEventHandling = true;

            if (isMainFrameEvent(e)) {
                List<DialogInformation> list = ctrlDialogMap.get(Ulrice.getModuleManager().getCurrentController());
                if (list != null) {
                    for (DialogInformation item : list) {
                        item.dialog.toFront();
                    }
                }
            }
            isInEventHandling = false;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (e.getSource() instanceof JDialog) {
                // linux only fires windowClosing on dialogs
                unblockParent((JDialog) e.getSource());
            }
            
            if (e.getWindow() != Ulrice.getMainFrame().getFrame()) {
                e.getWindow().dispose();
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {
            if (e.getSource() instanceof JDialog) {
                // windows only fires window closed on dialogs
                unblockParent((JDialog) e.getSource());
            }
        }
        
        private void unblockParent(JDialog dialog) {
            DialogInformation dlgInfo = dlgInfoDialogMap.remove(dialog);
            if (dlgInfo != null) {
                // Dialog was already removed by closeModule
                switch (dlgInfo.mode) {
                    case ApplicationModal:
                    case ModuleModal:
                        if (Ulrice.getModuleManager().isBlockedByBlocker(dlgInfo.ctrl, dialog)) {
                            Ulrice.getModuleManager().removeBlocker(dlgInfo.ctrl, dialog);
                        }
                        break;
                    default:
                        break;
                }
                ctrlDialogMap.get(dlgInfo.ctrl).remove(dlgInfo);
            }
        }
        
    }

    private boolean isMainFrameEvent(ComponentEvent e) {
        return e.getSource() == Ulrice.getMainFrame().getFrame();
    }
}

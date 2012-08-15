package net.ulrice.frame.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.ui.UIConstants;

/**
 * The changeover dialog shown during a module change.
 * 
 * @author ckuhlmeyer
 */
public class ChangeoverDialog extends JPanel implements KeyEventDispatcher {

	private static final long serialVersionUID = 7295269409658971848L;
	private static final Logger LOG = Logger.getLogger(ChangeoverDialog.class.getName());

	/** The default dimension of this panel. */
	private static final Dimension PANEL_DIMENSION = UIManager.getDimension(UIConstants.CHANGEOVER_DIALOG_SIZE);

	/** Border used for marked components. */
	private static final Border markedBorder = UIManager.getBorder(UIConstants.CHANGEOVER_MARKED_BORDER);

	/** Border used for non marked components. */
	private static final Border nonMarkedBorder = UIManager.getBorder(UIConstants.CHANGEOVER_NONMARKED_BORDER);

	/** The panel displaying the list of icons. */
	private JPanel listPanel = null;

	/** The layout of the panel displaying the icons of the modules. */
	private GridBagLayout listLayout = null;

	/** The label displaying the name of the current module. */
	private JLabel currentControllerNameLabel = null;

	/** The currently marked label. */
	private JLabel currentMarkedLabel = null;

	/** Holds the mapping between the labels and the controller. */
	private Map<IFController, JLabel> controllerLabelMap = new HashMap<IFController, JLabel>();

	/** The prechosen controller. */
	private IFController preChosenController;

	/** Flag, if the dialog is currently shown. */
	private boolean showChangeoverDialog = false;

	/**
	 * Creates a new changeover dialog.
	 */
	public ChangeoverDialog() {

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);

		// Create current controller label
		currentControllerNameLabel = new JLabel();
		currentControllerNameLabel.setHorizontalAlignment(JLabel.CENTER);

		// Create list panel showing the icons of the modules.
		listPanel = new JPanel();
		listPanel.setBorder(UIManager.getBorder(UIConstants.CHANGEOVER_ICON_PANEL_BORDER));
		listLayout = new GridBagLayout();
		listPanel.setLayout(listLayout);

		// Set dialog properties
		setSize(PANEL_DIMENSION);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory
				.createEmptyBorder(2, 2, 2, 2)));

		// Layout the dialog.
		setLayout(new BorderLayout());
		add(listPanel, BorderLayout.CENTER);
		add(currentControllerNameLabel, BorderLayout.SOUTH);

		setVisible(false);
	}

	/**
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {

		if (visible) {
			// If not already visible, show the dialog.

			// Define layout.
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.insets = UIManager.getInsets(UIConstants.CHANGEOVER_ICON_INSETS);
			int iconsPerRow = UIManager.getInt(UIConstants.CHANGEOVER_ICONS_PER_ROW);

			// Build list of all controller icons.
			final List <IFController> activeControllers = Ulrice.getModuleManager().getActiveControllers();
			final IFController activeController = Ulrice.getModuleManager().getCurrentController();
			listPanel.removeAll();
			if (activeControllers != null && activeControllers.size() > 0) {
				int i = 1;
				for (IFController ctrl : activeControllers) {
				    // Create and add controller label.
				    final JLabel ctrlLabel = new JLabel(Ulrice.getModuleManager().getModule(ctrl).getIcon(ModuleIconSize.Size_32x32));
				    controllerLabelMap.put(ctrl, ctrlLabel);

				    if (ctrl.equals(activeController)) {
				        currentMarkedLabel = ctrlLabel;
				        ctrlLabel.setBorder(markedBorder);
				    } else {
				        ctrlLabel.setBorder(nonMarkedBorder);
				    }

				    if (i % iconsPerRow == 0) {
				        constraints.gridwidth = GridBagConstraints.REMAINDER;
				    } else {
				        constraints.gridwidth = 1;
				    }

				    listLayout.setConstraints(ctrlLabel, constraints);
				    listPanel.add(ctrlLabel);
				    i++;
				}

				// Set the text of the controller
				if (activeController != null) {
					currentControllerNameLabel.setText(Ulrice.getModuleManager().getTitleProvider(activeController).getModuleTitle(Usage.ChangeOverDialog));
				}

				super.setVisible(true);
			}
		} else {
			// Hide the dialog.
			controllerLabelMap.clear();
			super.setVisible(false);
		}
	}

	/**
	 * Refreshes the view in the changeover dialog.
	 */
	public void newControllerShown(IFController oldController, IFController newController) {
		if (currentMarkedLabel != null) {
			currentMarkedLabel.setBorder(nonMarkedBorder);
		}

		JLabel label = controllerLabelMap.get(newController);
		currentMarkedLabel = label;
		if (label != null) {
			label.setBorder(markedBorder);
		}

		// Set the name of the controller to the label.
		if (newController != null) {
			currentControllerNameLabel.setText(Ulrice.getModuleManager().getTitleProvider(newController).getModuleTitle(Usage.ChangeOverDialog));
		}
	}

	/**
	 * @see java.awt.KeyEventDispatcher#dispatchKeyEvent(java.awt.event.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {

		// Open the changeover dialog.
		KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
		if (KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK, false).equals(ks) && !showChangeoverDialog) {
			LOG.finest("Show Changeover dialog.");
			showChangeoverDialog = true;
			showChangeOverDialog();
			nextModule();
			return true;
		}

		// Select next module.
		if (KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK, false).equals(ks) && showChangeoverDialog) {
			LOG.finest("Next controller.");
			nextModule();
			return true;
		}

		// Hide the changeover dialog.
		if (KeyStroke.getKeyStroke("released CONTROL").equals(ks) && showChangeoverDialog) {
			LOG.finest("Hide Changeover dialog.");
			showChangeoverDialog = false;
			hideChangeOverDialog();
			return true;
		}

		return false;
	}

	private void nextModule() {
		IFModuleManager moduleManager = Ulrice.getModuleManager();
		IFController currentModule = moduleManager.getCurrentController();
		List<IFController> activeModules = moduleManager.getActiveControllers();

		int idx = activeModules.indexOf(preChosenController);
		if (idx > -1 && activeModules.size() > 0) {
			int newIdx = (idx + 1) % activeModules.size();
			preChosenController = activeModules.get(newIdx);

			if (showChangeoverDialog) {
				newControllerShown(currentModule, preChosenController);
			}
		}
	}

	private void showChangeOverDialog() {
		if (Ulrice.getMainFrame() == null) {
			return;
		}

		IFController currentModule = Ulrice.getModuleManager().getCurrentController();		
		if(currentModule != null) {
			preChosenController = currentModule;
			
			JFrame frame = Ulrice.getMainFrame().getFrame();
	
			int x = frame.getWidth() / 2 - getWidth() / 2;
			int y = frame.getHeight() / 2 - getHeight() / 2;
			setBounds(x, y, getWidth(), getHeight());
			setVisible(true);
			frame.getLayeredPane().add(this, JLayeredPane.POPUP_LAYER);
		}
	}

	private void hideChangeOverDialog() {
		if (Ulrice.getMainFrame() == null) {
			return;
		}
		JFrame frame = Ulrice.getMainFrame().getFrame();
		if (preChosenController != null) {
			Ulrice.getModuleManager().activateModule(preChosenController);
		}
		setVisible(false);
		frame.getLayeredPane().remove(this);
	}

}

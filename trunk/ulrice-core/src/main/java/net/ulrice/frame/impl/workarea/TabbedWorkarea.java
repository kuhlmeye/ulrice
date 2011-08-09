package net.ulrice.frame.impl.workarea;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleTitleProvider;
import net.ulrice.module.IFModuleTitleProvider.Usage;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.ui.UIConstants;

/**
 * Workspace that displays the modules in a tabbed view.
 * 
 * @author ckuhlmeyer
 */
public class TabbedWorkarea extends JTabbedPane implements IFWorkarea, MouseListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -4790214373827895177L;

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(TabbedWorkarea.class.getName());

	/** The close icon. */
	private ImageIcon closeIcon;
	
	private Map<JComponent, GlassPanel> glassPanelMap = new HashMap<JComponent, GlassPanel>();

	/**
	 * Creates a new tabbed workarea.
	 */
	public TabbedWorkarea() {
		super();

		URL closeIconUrl = getClass().getResource("close.gif");
		if (closeIconUrl != null) {
			closeIcon = new ImageIcon(closeIconUrl);
		}
	}

	/**
	 * @see net.ulrice.frame.IFWorkarea#getView()
	 */
	public JComponent getView() {
		return this;
	}

	/**
	 * @see net.ulrice.frame.IFWorkarea#onActivateWorkarea()
	 */
	public void onActivateWorkarea() {
		Ulrice.getModuleManager().addModuleEventListener(this);
	}

	/**
	 * @see net.ulrice.frame.IFWorkarea#onDeactivateWorkarea()
	 */
	public void onDeactivateWorkarea() {
		Ulrice.getModuleManager().removeModuleEventListener(this);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
	 */
	public void activateModule(IFController activeController) {

		// Get the component of the controller.
		int idx = getTabIndex(activeController);
		if (idx >= 0) {
			setSelectedIndex(idx);
		} else {
			// Print out log because module could not be found in the tab.
			final String moduleId = Ulrice.getModuleManager().getModule(activeController).getUniqueId();
			LOG.warning("Activated module [id:" + moduleId + "] could not be found in the tab.");

			openModule(activeController);
		}
		

		if(Ulrice.getModuleManager().isBlocked(activeController)) {
			moduleBlocked(activeController);			
		} else {
			moduleUnblocked(activeController);	
		}
	}

	private int getTabIndex(IFController activeController) {
	    // TODO Identify tab component in a different way. component is not stable
		JComponent controllerComponent = getControllerComponent(activeController);
		GlassPanel glassPanel = glassPanelMap.get(controllerComponent);
		int idx = indexOfComponent(glassPanel);
		return idx;
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeController(net.ulrice.module.IFController)
	 */
	public void closeController(IFController activeController) {
		if (activeController == null) {
			return;
		}

		// Get the component of the controller.
		int idx = getTabIndex(activeController);
		if (idx >= 0) {
			remove(idx);
		} else {
			// Print out log because module could not be found in the tab.
			final String moduleId = Ulrice.getModuleManager().getModule(activeController).getUniqueId();
			LOG.warning("Closed module [id:" + moduleId + "] could not be found in the tab.");
		}

		remove(getControllerComponent(activeController));
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	public void deactivateModule(IFController activeController) {
		setSelectedIndex(-1);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	public void openModule(IFController activeController) {
		if (activeController == null) {
			return;
		}

		// Get the component of the controller.
		JComponent controllerComponent = getControllerComponent(activeController);
		GlassPanel glassPanel = new GlassPanel();
		glassPanel.setLayout(new BorderLayout());
		glassPanel.add(controllerComponent, BorderLayout.CENTER);
		
		glassPanelMap.put(controllerComponent, glassPanel);

		// Get the insert position.
		int selectedIdx = getSelectedIndex();
		if (selectedIdx == -1) {
			selectedIdx = getTabCount();
		}

		// Add the tab.
		if (selectedIdx + 1 >= getTabCount()) {
			// Add the tab to the end.
			addTab(null, glassPanel);
		} else {
			// Insert the tab after current selected one.
			insertTab(null, null, glassPanel, null, selectedIdx);
		}
		setSelectedComponent(glassPanel);
		selectedIdx = getSelectedIndex();
		setTabComponentAt(selectedIdx, new TabControllerPanel(activeController));
		
		if(Ulrice.getModuleManager().isBlocked(activeController)) {
			moduleBlocked(activeController);			
		} else {
			moduleUnblocked(activeController);	
		}
	}

	/**
	 * Returns the view component of a given controller.
	 * 
	 * @param controller
	 *            The controller
	 * @return The view component of this controller.
	 */
	private JComponent getControllerComponent(IFController controller) {
		if (controller == null) {
			return null;
		}

		JComponent controllerComponent = null;
		if (controller.getView() != null) {
			controllerComponent = controller.getView();
		}
		return controllerComponent;
	}

	/**
	 * Component displayed in the tab area displaying the information of a
	 * controller.
	 * 
	 * @author ckuhlmeyer
	 */
	class TabControllerPanel extends JComponent {

		/** Default generated serial version uid. */
		private static final long serialVersionUID = -6541174126754145798L;
		private IFController controller;

		TabControllerPanel(final IFController controller) {
			this.controller = controller;

			addMouseListener(TabbedWorkarea.this);
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder());

			final String controllerTitle = Ulrice.getModuleManager().getTitleProvider(controller).getModuleTitle(Usage.TabbedWorkarea);

			// Get the icon.
			final ImageIcon icon = Ulrice.getModuleManager().getModule(controller).getIcon(ModuleIconSize.Size_16x16);

			AbstractAction closeAction = new AbstractAction("X", closeIcon) {

				/** Default generated serial version uid. */
				private static final long serialVersionUID = 4006169832402886959L;

				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					Ulrice.getModuleManager().closeController(controller);
				}
			};

			// Create the button for closing the controller.
			JButton closeButton = new JButton(closeAction);
			closeButton.setOpaque(false);
			closeButton.setBorderPainted(false);
			closeButton.setContentAreaFilled(false);
			closeButton.setFocusPainted(false);
			closeButton.setHorizontalTextPosition(SwingConstants.LEFT);
			closeButton.setHorizontalAlignment(SwingConstants.RIGHT);
			closeButton.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			closeButton.setMargin(new Insets(0, 0, 0, 0));

			// Create the label displaying the controller title
			JLabel label = new JLabel(controllerTitle, icon, JLabel.HORIZONTAL);
			label.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 20));
			label.setHorizontalAlignment(SwingConstants.LEFT);

			// Layout the tab component.
			setLayout(new BorderLayout());
			add(label, BorderLayout.CENTER);
			add(closeButton, BorderLayout.EAST);
		}

		public IFController getController() {
			return controller;
		}

	}

	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return getClass().getName();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getComponent() instanceof TabControllerPanel) {			
			
			TabControllerPanel tabCtrlPanel = (TabControllerPanel) e.getComponent();

			Ulrice.getModuleManager().activateModule(tabCtrlPanel.getController());

			
			if (e.isPopupTrigger()) {
				showPopup(tabCtrlPanel, e.getPoint());
			}
		}
	}

	private void showPopup(final TabControllerPanel tabCtrlPanel, Point point) {
		JPopupMenu popup = new JPopupMenu();

		popup.add(new AbstractAction(UIManager.getString(UIConstants.CLOSE_ACTION_TEXT)) {

			/** Default generated serial version uid.*/
			private static final long serialVersionUID = 4669265533306602938L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Ulrice.getModuleManager().closeController(tabCtrlPanel.getController());
			}
		});

		popup.add(new AbstractAction(UIManager.getString(UIConstants.CLOSE_OTHER_ACTION_TEXT)) {

			/** Default generated serial version uid.*/
			private static final long serialVersionUID = 7971222651198540021L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Ulrice.getModuleManager().closeOtherControllers(tabCtrlPanel.getController());
			}
		});

		popup.add(new AbstractAction(UIManager.getString(UIConstants.CLOSE_ALL_ACTION_TEXT)) {

			/** Default generated serial version uid.*/
			private static final long serialVersionUID = 8170793754336153114L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Ulrice.getModuleManager().closeAllControllers();
			}
		});
		popup.show(tabCtrlPanel, point.x, point.y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
	}


	@Override
	public void moduleBlocked(IFController controller) {
		System.out.println("Block: " + controller.getClass().getCanonicalName());
		// Get the component of the controller.
		JComponent controllerComponent = getControllerComponent(controller);
		GlassPanel glassPanel = glassPanelMap.get(controllerComponent);
		if(glassPanel != null) {
			glassPanel.setBlocked(true);
		}
	}

	@Override
	public void moduleUnblocked(IFController controller) {
		// Get the component of the controller.
		JComponent controllerComponent = getControllerComponent(controller);
		GlassPanel glassPanel = glassPanelMap.get(controllerComponent);
		if(glassPanel != null) {
			glassPanel.setBlocked(false);
		}
	}
}

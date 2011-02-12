package net.ulrice.frame.impl.workarea;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.module.IFController;
import net.ulrice.module.IFModuleManager;
import net.ulrice.module.IFModuleTitleRenderer;
import net.ulrice.module.ModuleIconSize;
import net.ulrice.module.IFModuleTitleRenderer.Usage;

/**
 * Workspace that displays the modules in a tabbed view.
 * 
 * @author ckuhlmeyer
 */
public class TabbedWorkarea extends JTabbedPane implements IFWorkarea, ChangeListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -4790214373827895177L;

	/** The logger used by this class. */
	private static final Logger LOG = Logger.getLogger(TabbedWorkarea.class.getName());

	/** The close icon. */
	private ImageIcon closeIcon;
	
	/** Mapping betweeMap<K, V>ab component and controller. */
	private Map<Component, IFController> componentControllerMap = new HashMap<Component, IFController>();

	/**
	 * Creates a new tabbed workarea.
	 */
	public TabbedWorkarea() {
		super();
		
		addChangeListener(this);

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
		removeChangeListener(this);
		
		// Get the component of the controller.
		JComponent controllerComponent = getControllerComponent(activeController);
		int idx = indexOfComponent(controllerComponent);
		if (idx >= 0) {
			setSelectedIndex(idx);
		} else {
			// Print out log because module could not be found in the tab.
			String moduleId = "<unknown>";
			if (activeController.getModule() != null) {
				moduleId = activeController.getModule().getUniqueId();
			}
			LOG.warning("Activated module [id:" + moduleId + "] could not be found in the tab.");

			openModule(activeController);
		}
		addChangeListener(this);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeModule(net.ulrice.module.IFController)
	 */
	public void closeModule(IFController activeController) {
		removeChangeListener(this);
		if (activeController == null) {
			return;
		}

		// Get the component of the controller.
		JComponent controllerComponent = getControllerComponent(activeController);
		controllerComponent.remove(controllerComponent);
		int idx = indexOfComponent(controllerComponent);
		if (idx >= 0) {
			remove(idx);
		} else {
			// Print out log because module could not be found in the tab.
			String moduleId = "<unknown>";
			if (activeController.getModule() != null) {
				moduleId = activeController.getModule().getUniqueId();
			}
			LOG.warning("Closed module [id:" + moduleId + "] could not be found in the tab.");
		}
		remove(controllerComponent);
		addChangeListener(this);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	public void deactivateModule(IFController activeController) {
		removeChangeListener(this);
		setSelectedIndex(-1);
		addChangeListener(this);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	public void openModule(IFController activeController) {
		removeChangeListener(this);
		if (activeController == null) {
			return;
		}

		// Get the component of the controller.
		JComponent controllerComponent = getControllerComponent(activeController);
		componentControllerMap.put(controllerComponent, activeController);

		// Get the insert position.
		int selectedIdx = getSelectedIndex();
		if (selectedIdx == -1) {
			selectedIdx = getTabCount();
		}

		// Add the tab.
		if (selectedIdx + 1 >= getTabCount()) {
			// Add the tab to the end.
			addTab(null, controllerComponent);
		} else {
			// Insert the tab after current selected one.
			insertTab(null, null, controllerComponent, null, selectedIdx);
			setSelectedComponent(controllerComponent);
		}
		setSelectedComponent(controllerComponent);
		selectedIdx = getSelectedIndex();
		setTabComponentAt(selectedIdx, new TabControllerPanel(activeController));
		addChangeListener(this);
	}

	/**
	 * Returns the view component of a given controller.
	 * 
	 * @param controller The controller
	 * @return The view component of this controller.
	 */
	private JComponent getControllerComponent(IFController controller) {
		if(controller == null) {
			return null;
		}
		
		JComponent controllerComponent = null;
		if (controller.getView() != null) {
			controllerComponent = controller.getView().getView();
		}
		return controllerComponent;
	}

	/**
	 * Component displayed in the tab area displaying the information of a controller.
	 * 
	 * @author ckuhlmeyer
	 */
	class TabControllerPanel extends JComponent {

		/** Default generated serial version uid. */
		private static final long serialVersionUID = -6541174126754145798L;

		TabControllerPanel(final IFController controller) {
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder());

			// Get the renderered title.
			String controllerTitle = null;
			if (controller.getModuleTitleRenderer() != null) {
				IFModuleTitleRenderer moduleTitleRenderer = controller.getModuleTitleRenderer();
				controllerTitle = moduleTitleRenderer.getModuleTitle(Usage.TabbedWorkarea);
			}

			// Get the icon.
			ImageIcon icon = null;
			if (controller.getModule() != null) {
				icon = controller.getModule().getIcon(ModuleIconSize.Size_16x16);
			}

			AbstractAction closeAction = new AbstractAction("X", closeIcon) {

				/** Default generated serial version uid. */
				private static final long serialVersionUID = 4006169832402886959L;

				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					Ulrice.getModuleManager().closeModule(controller);
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
			label.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			label.setHorizontalAlignment(SwingConstants.LEFT);

			// Layout the tab component.
			setLayout(new BorderLayout());
			add(label, BorderLayout.WEST);
			add(Box.createHorizontalStrut(20), BorderLayout.CENTER);
			add(closeButton, BorderLayout.EAST);
		}

	}
	
	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return getClass().getName();
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		IFModuleManager moduleManager = Ulrice.getModuleManager();

		IFController controller = componentControllerMap.get(getSelectedComponent());
		if(controller == null) {
			new RuntimeException("Controller could not be found for tab component.");
		}
		
		moduleManager.activateModule(controller);
	}
}

package net.ulrice.frame.impl.workarea;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.module.IFController;

/**
 * Workarea able to display a single module at a time.
 * 
 * @author ckuhlmeyer
 */
public class SingleWorkarea extends JPanel implements IFWorkarea {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -791167547594342060L;

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
		if (activeController != null && activeController.getView() != null
				&& activeController.getView().getView() != null) {
			add(activeController.getView().getView());
			invalidate();
		} else {
			removeAll();
			invalidate();
		}
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	public void deactivateModule(IFController activeController) {
		removeAll();
		invalidate();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	public void openModule(IFController activeController) {
		// This event can be ignored.
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeModule(net.ulrice.module.IFController)
	 */
	public void closeModule(IFController activeController) {
		// This event can be ignored.
	}

	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return getClass().getName();
	}
}

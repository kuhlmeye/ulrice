package net.ulrice.frame.impl.workarea;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

import javax.swing.JComponent;

import net.ulrice.Ulrice;
import net.ulrice.frame.IFWorkarea;
import net.ulrice.module.IFController;

/**
 * Workarea able to display a single module at a time.
 * 
 * @author ckuhlmeyer
 */
public class SingleWorkarea implements IFWorkarea, AWTEventListener {

	/** Default generated serial version uid. */
	private static final long serialVersionUID = -791167547594342060L;

	private GlassPanel workareaPanel = new GlassPanel();

	private IFController activeController;

	public SingleWorkarea() {
		workareaPanel.setLayout(new BorderLayout());
	}
	
	/**
	 * @see net.ulrice.frame.IFWorkarea#getView()
	 */
	public JComponent getView() {
		return workareaPanel;
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
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#activateModule(net.ulrice.module.IFController)
	 */
	public void activateModule(IFController activeController) {
		this.activeController = activeController;
		
		if(Ulrice.getModuleManager().isBlocked(activeController)) {
			moduleBlocked(activeController);
		} else {
			moduleUnblocked(activeController);
		}
		
		if (activeController != null && activeController.getView() != null) {
			workareaPanel.add(activeController.getView(), BorderLayout.CENTER);
		} else {
			workareaPanel.removeAll();
		}
		workareaPanel.revalidate();	
		workareaPanel.repaint();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#deactivateModule(net.ulrice.module.IFController)
	 */
	public void deactivateModule(IFController activeController) {
		this.activeController = null;
		workareaPanel.removeAll();
		workareaPanel.revalidate();	
		workareaPanel.repaint();
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#openModule(net.ulrice.module.IFController)
	 */
	public void openModule(IFController activeController) {
		// This event can be ignored.
	}

	/**
	 * @see net.ulrice.module.event.IFModuleEventListener#closeController(net.ulrice.module.IFController)
	 */
	public void closeController(IFController activeController) {
		// This event can be ignored.
		workareaPanel.removeAll();
		workareaPanel.revalidate();	
		workareaPanel.repaint();
	}

	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return getClass().getName();
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moduleBlocked(IFController controller) {
		if(activeController != null && activeController.equals(controller)) {
			workareaPanel.setBlocked(true);
			Toolkit.getDefaultToolkit().addAWTEventListener(this, 0xFFF);
		}
	}

	@Override
	public void moduleUnblocked(IFController controller) {
		if(activeController != null && activeController.equals(controller)) {
			workareaPanel.setBlocked(false);
			Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		}
	}
}

/**
 * 
 */
package net.ulrice.frame.impl.navigation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JTree;

import net.ulrice.frame.IFMainFrameComponent;

/**
 * This tree displays the instanciated modules in a 'flat' tree.
 * 
 * @author christof
 */
public class InstanceTree extends JTree implements IFMainFrameComponent, MouseListener {	
	
	/** Default generated serial version uid. */
	private static final long serialVersionUID = 1181968289912387613L;

	/**
	 * Creates a new instance tree.
	 */
	public InstanceTree() {
		super(new InstanceTreeModel());
		setCellRenderer(new InstanceTreeCellRenderer());
		setRootVisible(false);
		addMouseListener(this);
	}
	
	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getComponentId()
	 */
	@Override
	public String getComponentId() {
		return getClass().getName();
	}

	/**
	 * @see net.ulrice.frame.IFMainFrameComponent#getView()
	 */
	@Override
	public JComponent getView() {
		return this;
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

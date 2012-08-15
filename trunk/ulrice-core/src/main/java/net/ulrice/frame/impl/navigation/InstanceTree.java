/**
 * 
 */
package net.ulrice.frame.impl.navigation;

import javax.swing.JComponent;
import javax.swing.JTree;

import net.ulrice.frame.IFMainFrameComponent;

/**
 * This tree displays the instanciated modules in a 'flat' tree.
 * 
 * @author christof
 */
public class InstanceTree extends JTree implements IFMainFrameComponent {	
	
	/** Default generated serial version uid. */
	private static final long serialVersionUID = 1181968289912387613L;

	/**
	 * Creates a new instance tree.
	 */
	public InstanceTree() {
		super(new InstanceTreeModel());
		setCellRenderer(new InstanceTreeCellRenderer());
		setRootVisible(false);
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
}

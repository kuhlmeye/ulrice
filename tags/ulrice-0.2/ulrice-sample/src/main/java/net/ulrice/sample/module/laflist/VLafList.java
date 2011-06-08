/**
 * 
 */
package net.ulrice.sample.module.laflist;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.ulrice.module.IFView;

/**
 * The view of the look and feel module.
 * 
 * @author christof
 */
public class VLafList implements IFView {	
	
	/** The jtable displaying the look and feel constants. */
	private JTable lafTable;
	
	/** The view component. */
	private JPanel view;

	/**
	 * @see net.ulrice.module.IFView#getView()
	 */
	@Override
	public JComponent getView() {
		return view;
	}

	/**
	 * @see net.ulrice.module.IFView#initialize()
	 */
	@Override
	public void initialize() {
		
		lafTable = new JTable();
				
		view = new JPanel();
		view.setLayout(new BorderLayout());
		view.add(new JScrollPane(lafTable), BorderLayout.CENTER );
	}

	/**
	 * Returns the table displaying the look and feel components. 
	 * 
	 * @return The jtable.
	 */
	public JTable getTable() {
		return lafTable;
	}
}

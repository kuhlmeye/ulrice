/**
 * 
 */
package net.ulrice.sample.module.laflist;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * The view of the look and feel module.
 * 
 * @author christof
 */
public class VLafList {	
	
	/** The jtable displaying the look and feel constants. */
	private JTable lafTable;
	
	/** The view component. */
	private JPanel view;

	/**
	 * @see net.ulrice.module.IFView#getView()
	 */
	public JComponent getView() {
		return view;
	}

	/**
	 * @see net.ulrice.module.IFView#initialize()
	 */
	{
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

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
	private final JTable lafTable = new JTable ();
	
	/** The view component. */
	private JPanel view = new JPanel ();

	public VLafList() {
	    view.setLayout(new BorderLayout());
	    view.add(new JScrollPane(lafTable), BorderLayout.CENTER );
	}
	
	public JComponent getView() {
		return view;
	}

	public JTable getTable() {
		return lafTable;
	}
}

package net.ulrice.sample.module.laflist;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * The view of the look and feel module.
 * 
 * @author christof
 */
public class LafListView extends JPanel {	
	
	private static final long serialVersionUID = 1L;

	private final JTable lafTable = new JTable ();

	public LafListView(LafListModel model) {
		super(new BorderLayout());		
	    add(new JScrollPane(lafTable), BorderLayout.CENTER);
	    lafTable.setModel(model);
	}
}

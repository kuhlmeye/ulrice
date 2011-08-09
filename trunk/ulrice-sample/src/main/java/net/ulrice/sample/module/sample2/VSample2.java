package net.ulrice.sample.module.sample2;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * View of the sample module 2
 * 
 * @author christof
 */
public class VSample2 {

	private JPanel view = new JPanel();

	
	public VSample2 () {
	    view.setLayout(new BorderLayout());
	    view.add(new JLabel("Sample 2 module."), BorderLayout.CENTER);
	}

	public JComponent getView() {
		return view;
	}
}

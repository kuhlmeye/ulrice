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
	public void initialize(CSample2 controller) {
		view = new JPanel();
		view.setLayout(new BorderLayout());
		view.add(new JLabel("Sample 2 module."), BorderLayout.CENTER);
	}
}

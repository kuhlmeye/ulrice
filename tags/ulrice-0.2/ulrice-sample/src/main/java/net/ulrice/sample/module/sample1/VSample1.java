package net.ulrice.sample.module.sample1;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.ulrice.module.IFView;

/**
 * View of the sample module 1
 * 
 * @author christof
 */
public class VSample1 implements IFView {

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
	public void initialize() {
		view = new JPanel();
		view.setLayout(new BorderLayout());
		view.add(new JLabel("Sample 1 module."), BorderLayout.CENTER);
		view.add(new JButton("Hallo"), BorderLayout.SOUTH);
	}
}
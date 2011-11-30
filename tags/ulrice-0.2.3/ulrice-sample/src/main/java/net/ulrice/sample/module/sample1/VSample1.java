package net.ulrice.sample.module.sample1;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * View of the sample module 1
 * 
 * @author christof
 */
public class VSample1 {

	private final JPanel view = new JPanel();

	public VSample1() {
	    view.setLayout(new BorderLayout());
	    view.add(new JLabel("Sample 1 module."), BorderLayout.CENTER);
	    view.add(new JButton(new AbstractAction("Say hello!") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Hello!");
			}
		}), BorderLayout.SOUTH);
	}

	/**
	 * @see net.ulrice.module.IFView#getView()
	 */
	public JComponent getView() {
		return view;
	}
}

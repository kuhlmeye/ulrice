package net.ulrice.sample.module.sample2;

import javax.swing.JComponent;

import net.ulrice.module.impl.AbstractController;

/**
 * Controller of the sample module 2
 * 
 * @author christof
 */
public class CSample2 extends AbstractController {
	private final VSample2 view = new VSample2();
	
	public JComponent getView() {
	    return view.getView();
	}
}

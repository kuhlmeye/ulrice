package net.ulrice.sample.module.sample2;

import javax.swing.JComponent;

import net.ulrice.module.IFModel;
import net.ulrice.module.impl.AbstractController;

/**
 * Controller of the sample module 2
 * 
 * @author christof
 */
public class CSample2 extends AbstractController {

	/**
	 * @see net.ulrice.module.impl.AbstractController#instanciateModel()
	 */
	@Override
	protected IFModel instantiateModel() {
		return new MSample2();
	}

	/**
	 * @see net.ulrice.module.impl.AbstractController#instanciateView()
	 */
	private final VSample2 v = new VSample2();
	protected JComponent instantiateView() {
	    return v.getView();
	}
}

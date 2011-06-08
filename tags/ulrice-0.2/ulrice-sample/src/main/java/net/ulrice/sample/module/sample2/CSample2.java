package net.ulrice.sample.module.sample2;

import net.ulrice.module.IFModel;
import net.ulrice.module.IFView;
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
	protected IFModel instanciateModel() {
		return new MSample2();
	}

	/**
	 * @see net.ulrice.module.impl.AbstractController#instanciateView()
	 */
	@Override
	protected IFView instanciateView() {
		return new VSample2();
	}
}

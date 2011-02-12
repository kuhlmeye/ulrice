package net.ulrice.sample.module.sample1;

import net.ulrice.module.IFModel;
import net.ulrice.module.IFModule;
import net.ulrice.module.IFView;
import net.ulrice.module.impl.AbstractController;

/**
 * Controller of the sample module 1
 * 
 * @author christof
 */
public class CSample1 extends AbstractController {

	/**
	 * @see net.ulrice.module.IFController#postCreationEvent(net.ulrice.module.IFModule)
	 */
	@Override
	public void postCreationEvent(IFModule module) {
		postInfoMessage("Sample controller 1 successfully initialized.");
	}
	
	/**
	 * @see net.ulrice.module.impl.AbstractController#instanciateModel()
	 */
	@Override
	protected IFModel instanciateModel() {
		return new MSample1();
	}

	/**
	 * @see net.ulrice.module.impl.AbstractController#instanciateView()
	 */
	@Override
	protected IFView instanciateView() {
		return new VSample1();
	}
}

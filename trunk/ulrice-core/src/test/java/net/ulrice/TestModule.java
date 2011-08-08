package net.ulrice;

import javax.swing.JComponent;

import net.ulrice.module.IFController;
import net.ulrice.module.IFModel;
import net.ulrice.module.impl.AbstractController;


public class TestModule extends AbstractController {

	@Override
	protected IFModel instantiateModel() {
		return new IFModel() {
			
			@Override
			public void initialize(IFController controller) {
			}
		};
	}

	@Override
	protected JComponent instantiateView() {
	    return null;
	}

}

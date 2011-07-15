package net.ulrice;

import javax.swing.JComponent;

import net.ulrice.module.IFModel;
import net.ulrice.module.IFView;
import net.ulrice.module.impl.AbstractController;

public class TestModule extends AbstractController {

	@Override
	protected IFModel instanciateModel() {
		return new IFModel() {
			
			@Override
			public void initialize() {
			}
		};
	}

	@Override
	protected IFView instanciateView() {
		return new IFView() {
			
			@Override
			public void initialize() {
			}
			
			@Override
			public JComponent getView() {
				return null;
			}
		};
	}

}

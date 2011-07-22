package net.ulrice.module;

import javax.swing.JComponent;

public interface IFView<T extends IFController> {

	void initialize(T Controller);
	
	JComponent getView();
}

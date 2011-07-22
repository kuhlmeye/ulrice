package net.ulrice.module;

public interface IFModel<T extends IFController> {

	void initialize(T controller);	
}

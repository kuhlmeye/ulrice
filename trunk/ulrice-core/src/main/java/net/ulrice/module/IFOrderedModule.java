package net.ulrice.module;



/**
 * Interface of a module description. 
 * 
 * @author ckuhlmeyer
 */
public interface IFOrderedModule<T extends IFController> extends IFModule<T> { 

	int getOrderNumber();
}

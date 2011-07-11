/**
 * 
 */
package net.ulrice.databinding.modelaccess;

/**
 * @author christof
 *
 */
public interface IFDynDataAccessor<T> {
	

	T readValue(Object root);
	
	
	void writeValue(Object root, T value);
}

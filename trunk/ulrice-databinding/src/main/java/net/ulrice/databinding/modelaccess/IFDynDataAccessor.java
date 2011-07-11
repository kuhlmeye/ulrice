/**
 * 
 */
package net.ulrice.databinding.modelaccess;

/**
 * @author christof
 *
 */
public interface IFDynDataAccessor {
	

	Object getValue(Object root);
	
	
	void setValue(Object root, Object value);
}

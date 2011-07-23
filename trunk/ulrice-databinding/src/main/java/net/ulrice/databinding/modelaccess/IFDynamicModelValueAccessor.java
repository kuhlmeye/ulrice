/**
 * 
 */
package net.ulrice.databinding.modelaccess;

/**
 * @author christof
 *
 */
public interface IFDynamicModelValueAccessor {
	

	Object getValue(Object root);
	
	
	void setValue(Object root, Object value);
	

    String getAttributeId();


	Class<?> getModelType(Class<?> rootType);
}

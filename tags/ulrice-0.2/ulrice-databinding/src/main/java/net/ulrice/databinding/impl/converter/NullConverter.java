/**
 * 
 */
package net.ulrice.databinding.impl.converter;

import net.ulrice.databinding.IFConverter;

/**
 * Empty converter doing no conversion.
 * 
 * @author christof
 */
public class NullConverter<T> implements IFConverter<T, T> {

	/**
	 * @see net.ulrice.databinding.IFConverter#mapToSource(java.lang.Object)
	 */
	@Override
	public T mapToSource(T target) {
		return target;
	}

	/**
	 * @see net.ulrice.databinding.IFConverter#mapToTarget(java.lang.Object)
	 */
	@Override
	public T mapToTarget(T source) {
		return source;
	}

}

package net.ulrice.databinding.bufferedbinding;


public interface IFExtdAttributeModel<T> extends IFAttributeModel<T> {

	/**
	 * Returns the original value read from the model.
	 * 
	 * @return The original value.
	 */
	T getOriginalValue();

	/**
	 * Returns the current value of this model.
	 * 
	 * @return The current value
	 */
	T getCurrentValue();


	/**
	 * Set the current value of this model.
	 * 
	 * @param value
	 *            the current value.
	 */
	void setCurrentValue(T value);
	
	T directWrite();
	
	void directRead(T value);
}

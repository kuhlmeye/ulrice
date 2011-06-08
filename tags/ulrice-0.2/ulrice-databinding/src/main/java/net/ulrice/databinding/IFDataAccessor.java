package net.ulrice.databinding;

/**
 * Interface for all data accessors. A data accessor is used by the attribute
 * model to read and write the value. It is therefore the link between the data
 * model and the attribute models.
 * 
 * @author christof
 */
public interface IFDataAccessor<T> {

	/**
	 * Reads the value from the model.
	 * 
	 * @return The value read from the model.
	 */
	T readValue();

	/** 
	 * Writes the value into the model.
	 * 
	 * @param value The value that should be written into the model
	 */
	void writeValue(T value);

}

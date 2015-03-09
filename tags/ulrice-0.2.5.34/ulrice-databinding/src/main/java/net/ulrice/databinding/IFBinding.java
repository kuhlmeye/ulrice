package net.ulrice.databinding;

import java.util.List;

/**
 * Represents a binding (simple or buffered binding)
 * 
 * @author DL10KUH
 */
public interface IFBinding {

    /**
     * Returns the identifier of this attribute binding. The identifier must be unique within one binding group
     * 
     * @return The id as string
     */
	String getId();
	
	/**
	 * Returns the original value read from the model by the model accessor.
	 * 
	 * @return The original value as object.
	 */
	Object getOriginalValue();
	
	/**
	 * Returns the current value of the attribute of this binding. 
	 * @return
	 */
	Object getCurrentValue();
	
	/**
	 * Returns the dirty state.
	 * 
	 * @return true, if the attribute was changed (original value != current value), false otherwise.
	 */
	boolean isDirty();
    
    /**
     * Returns, if this value is a read only value.
     * 
     * @return True, if this attribute is read-only; false otherwise.
     */
    boolean isReadOnly();
	
	/**
	 * Returns the valid state
	 * 
	 * @return true, if the value of this attribute is valid, false otherwise
	 */
	boolean isValid();
	
	/**
	 * Returns a string representation of the validation failures of this attribute model or null, if the object is valid
	 * 
	 * @return A string list or null. 
	 */
	List<String> getValidationFailures();
}

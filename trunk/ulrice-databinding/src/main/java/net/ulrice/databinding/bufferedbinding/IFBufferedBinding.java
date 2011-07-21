package net.ulrice.databinding.bufferedbinding;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

public interface IFBufferedBinding<T> extends IFBinding {

	void addViewAdapter(IFViewAdapter viewAdapter);

	/**
	 * Read the value from the model. The data accessor is used to get the value
	 */
	void read();

	
	/**
	 * Called by a gui accessor if a value was changed.
	 * 
	 * @param guiAccessor Gui accessor which changes the value.
	 * @param value The new value.
	 */
	void gaChanged(IFViewAdapter viewAdapter, T value);

	/**
	 * Write the value into the model. The data accessor is used to set the
	 * value.
	 */
	void write();


	/**
	 * Returns the current state of this datamodel.
	 * 
	 * @return The state of this datamodel.
	 */
	

	/**
	 * Set the validator if this model.
	 * 
	 * @param validator
	 *            The validator.
	 */
	void setValidator(IFValidator<T> validator);

	/**
	 * Returns the validator of this model.
	 * 
	 * @return The validator.
	 */
	IFValidator<T> getValidator();

	/**
	 * Return the current validation errors or null, if the attribute model is valid.
	 * 
	 * @return The validation errors. 
	 */
	ValidationResult getValidationResult();
	
	/**
	 * Adds an attribute model event listener to the list of event listeners.
	 * 
	 * @param listener
	 *            The listener to be added to the list of listeners.
	 */
	void addAttributeModelEventListener(IFBufferedBindingEventListener<T> listener);

	/**
	 * Removes an attribute model event listener from the list of event
	 * listeners.
	 * 
	 * @param listener
	 *            The listener to be removed from the list of listeners.
	 */
	void removeAttributeModelEventListener(IFBufferedBindingEventListener<T> listener);
	
	void setValueConverter(IFValueConverter valueConverter);		
	
	boolean isInitialized();
}

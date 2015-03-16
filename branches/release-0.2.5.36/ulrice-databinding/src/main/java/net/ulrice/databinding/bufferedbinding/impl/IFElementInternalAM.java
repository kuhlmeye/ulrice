package net.ulrice.databinding.bufferedbinding.impl;

import java.util.List;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

interface IFElementInternalAM<T> extends IFBinding {

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getId()
	 */
	String getId();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getCurrentValue()
	 */
	T getCurrentValue();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	void setValue(Object value);

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setCurrentValue(java.lang.Object)
	 */
	void setCurrentValue(T value);

	void recalculateState();

	void gaChanged(IFViewAdapter viewAdapter, T value);
	
	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getOriginalValue()
	 */
	T getOriginalValue();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#read()
	 */
	@SuppressWarnings("unchecked")
	void read();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#directRead(java.lang.Object)
	 */
	void directRead(T value);

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#write()
	 */
	void write();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#directWrite()
	 */
	T directWrite();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidator()
	 */
	List<IFValidator<T>> getValidators();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValidator(net.ulrice.databinding.validation.IFValidator)
	 */
	void addValidator(IFValidator<T> validator);

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidationResult()
	 */
	ValidationResult getValidationResult();

	List<String> getValidationFailures();

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#addAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
	 */
	void addAttributeModelEventListener(
			IFAttributeModelEventListener<T> listener);

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#removeAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
	 */
	void removeAttributeModelEventListener(
			IFAttributeModelEventListener<T> listener);

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#isReadOnly()
	 */
	boolean isReadOnly();

	void setReadOnly(boolean readOnly);

	void addViewAdapter(IFViewAdapter viewAdapter);

	void removeViewAdapter(IFViewAdapter viewAdapter);

	void viewValueChanged(IFViewAdapter viewAdapter);


	boolean isValid();

	boolean isDirty();

	IFAttributeInfo getAttributeInfo();

	void addExternalValidationError(String translatedMessage);

	void addExternalValidationError(ValidationError validationError);

	void clearExternalValidationErrors();

	/**
	 * @return the isListOrderRelevant
	 */
	boolean isListOrderRelevant();

	/**
	 * @param isListOrderRelevant
	 *            the isListOrderRelevant to set
	 */
	void setListOrderRelevant(boolean isListOrderRelevant);

}
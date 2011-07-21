package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.databinding.bufferedbinding.IFBufferedBinding;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;

/**
 * @author christof
 * 
 */
public class ListAM extends AbstractTableAM implements IFBufferedBinding  {

	private IFModelValueAccessor dataAccessor;
	public ListAM(IFModelValueAccessor dataAccessor, boolean readOnly) {
		super(dataAccessor.getAttributeId(), readOnly);
		this.dataAccessor = dataAccessor;
	}

	
	public ListAM(IFModelValueAccessor dataAccessor) {
		this(dataAccessor, false);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#read()
	 */
	@Override
	public void read() {
		directRead(getDataAccessor().getValue());	
	}
	
	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#directRead(java.lang.Object)
	 */
	public void directRead(Object valueList) {
		elements.clear();
		setDirty(false);
		setInitialized(true);
		setValid(true);

		if (valueList != null) {
			for (Object value : (List<?>)valueList) {
				Element elem = createElement(value);
				elem.readObject();
				elementIdMap.put(elem.getUniqueId(), elem);				
				elements.add(elem);
			}
			fireUpdateViews();
		}	
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#write()
	 */
	@Override
	public void write() {
		getDataAccessor().setValue(directWrite());
	}


	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#directWrite()
	 */
	@SuppressWarnings("unchecked")
	public Object directWrite() {
		List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());
		if(elements != null) {
			for(Element elem : elements) {
				elem.writeObject();
				result.add(elem.getValueObject());
			}
		}		
		return result;
	}
	
	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getCurrentValue()
	 */
	@SuppressWarnings("unchecked")
	public Object getCurrentValue() {
		return directWrite();
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getOriginalValue()
	 */
	@SuppressWarnings("unchecked")
	public Object getOriginalValue() {
		List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());
		if(elements != null) {
			for(Element elem : elements) {
				result.add(elem.getValueObject());
			}
		}		
		return result;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setCurrentValue(java.lang.Object)
	 */
	public void setCurrentValue(Object valueList) {
		elements.clear();
	
		if (valueList != null) {
			for (Object value : (List<?>)valueList) {
				Element elem = createElement(value);
				elem.readObject();
				elements.add(elem);				
			}
		}	 
	}

	/**
	 * @return the dataAccessor
	 */
	public IFModelValueAccessor getDataAccessor() {
		return dataAccessor;
	} 
}


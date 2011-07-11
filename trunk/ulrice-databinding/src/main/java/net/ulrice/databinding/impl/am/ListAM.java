package net.ulrice.databinding.impl.am;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFExtdAttributeModel;
import net.ulrice.databinding.modelaccess.IFModelValueAccessor;

/**
 * @author christof
 * 
 */
public class ListAM<T extends List<S>, S> extends AbstractTableAM<T, S> implements IFExtdAttributeModel<T>  {

	private IFModelValueAccessor dataAccessor;
	public ListAM(String id, IFModelValueAccessor dataAccessor, boolean editable) {
		super(id, editable);
		this.dataAccessor = dataAccessor;
	}

	
	public ListAM(String id, IFModelValueAccessor dataAccessor) {
		this(id, dataAccessor, true);
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#read()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void read() {
		directRead((T)getDataAccessor().getValue());	
	}
	
	/**
	 * @see net.ulrice.databinding.IFAttributeModel#directRead(java.lang.Object)
	 */
	public void directRead(T valueList) {
		elements.clear();
        state = DataState.NotChanged;

		if (valueList != null) {
			for (S value : valueList) {
				Element<S> elem = createElement(value);
				elem.readObject();
				elementIdMap.put(elem.getUniqueId(), elem);				
				elements.add(elem);
			}
			// TODO Refine event.
			fireTableChanged(new TableModelEvent(this));
		}	
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#write()
	 */
	@Override
	public void write() {
		getDataAccessor().setValue(directWrite());
	}


	/**
	 * @see net.ulrice.databinding.IFAttributeModel#directWrite()
	 */
	@SuppressWarnings("unchecked")
	public T directWrite() {
		T result = (T) new ArrayList<S>(elements == null ? 0 : elements.size());
		if(elements != null) {
			for(Element<S> elem : elements) {
				elem.writeObject();
				result.add(elem.getValueObject());
			}
		}		
		return result;
	}
	
	/**
	 * @see net.ulrice.databinding.IFAttributeModel#getCurrentValue()
	 */
	@SuppressWarnings("unchecked")
	public T getCurrentValue() {
		T result = (T) new ArrayList<S>(elements == null ? 0 : elements.size());
		if(elements != null) {
			for(Element<S> elem : elements) {
				elem.writeObject();
				result.add(elem.getValueObject());
			}
		}	
		return result;
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#getOriginalValue()
	 */
	@SuppressWarnings("unchecked")
	public T getOriginalValue() {
		T result = (T) new ArrayList<S>(elements == null ? 0 : elements.size());
		if(elements != null) {
			for(Element<S> elem : elements) {
				result.add(elem.getValueObject());
			}
		}	
		return result;
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#setCurrentValue(java.lang.Object)
	 */
	public void setCurrentValue(T valueList) {
		elements.clear();
	
		if (valueList != null) {
			for (S value : valueList) {
				Element<S> elem = createElement(value);
				elem.readObject();
				
				elements.add(elem);				
			}
		}	 
	}
	

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#setValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		T tValue = (T) value;
		setCurrentValue(tValue);
	}

	/**
	 * @return the dataAccessor
	 */
	public IFModelValueAccessor getDataAccessor() {
		return dataAccessor;
	} 
}


package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFIndexedModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

/**
 * @author christof
 * 
 */
public class TableAM implements IFAttributeModel  {


	private IFIndexedModelValueAccessor tableMVA;
	
	
	protected List<Element> elements = new ArrayList<Element>();
	protected Map<String, Element> elementIdMap = new HashMap<String, Element>();
	
	private List<IFValidator> validators = new ArrayList<IFValidator>();
	private EventListenerList listenerList;
	private String id;
	private List<ColumnDefinition<? extends Object>> columns = new ArrayList<ColumnDefinition<? extends Object>>();
	private boolean readOnly;
	private long nextUniqueId;
	private Set<Element> newElements = new HashSet<Element>();
	private Set<Element> modElements = new HashSet<Element>();
	private Set<Element> delElements = new HashSet<Element>();
	private Set<Element> invElements = new HashSet<Element>();
	
	private List<IFViewAdapter> viewAdapterList = new ArrayList<IFViewAdapter>();
	
	private IFValueConverter valueConverter;
	private boolean initialized = false;
	private boolean dirty = false;
	private boolean valid = true;

	public TableAM(IFIndexedModelValueAccessor tableMVA, boolean readOnly) {
		this.tableMVA = tableMVA;
		
		nextUniqueId = System.currentTimeMillis();
		
		this.id = tableMVA.getAttributeId();
		this.listenerList = new EventListenerList();	
		this.readOnly = readOnly;	
	}
	
	public TableAM(IFIndexedModelValueAccessor tableMVA) {
		this(tableMVA, false);
	}


	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	
	public boolean isCellValid(int row, int column) {
	    return getElementAt(row).isColumnValid(column);
	}
	
	public boolean isCellDirty(int row, int column) {
	    return getElementAt(row).isColumnDirty(column);
	}

	/**
	 * @param value
	 * @return
	 */
	protected Element createElement(Object value, boolean dirty, boolean valid) {
		String uniqueId = Long.toHexString(nextUniqueId++);
		Element elem = new Element(this, uniqueId, columns, value, isReadOnly(), dirty, valid);
		return elem;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#addAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
	 */
	@Override
	public void addAttributeModelEventListener(IFAttributeModelEventListener listener) {
		listenerList.add(IFAttributeModelEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#removeAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFAttributeModelEventListener)
	 */
	@Override
	public void removeAttributeModelEventListener(IFAttributeModelEventListener listener) {
		listenerList.remove(IFAttributeModelEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValidator(net.ulrice.databinding.validation.IFValidator)
	 */
	@Override
	public void addValidator(IFValidator validator) {
		this.validators.add(validator);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidator()
	 */
	@Override
	public List<IFValidator> getValidators() {
		return validators;
	}

	public Element getElementAt(int index) {
		return elements.get(index);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columns == null ? 0 : columns.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class<?> getColumnClass(int columnIndex) {
		return columns.get(columnIndex).getColumnClass();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		return columns.get(columnIndex).getColumnName();
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return elements == null ? 0 : elements.size();
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return !isReadOnly() && !getElementAt(rowIndex).isReadOnly(columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getElementAt(rowIndex).getValueAt(columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		getElementAt(rowIndex).setValueAt(columnIndex, aValue);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFElementChangeListener#dataChanged(net.net.ulrice.databinding.bufferedbinding.impl.Element, java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void dataChanged() {
	    fireUpdateViews();
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFElementChangeListener#stateChanged(net.net.ulrice.databinding.bufferedbinding.impl.Element, net.ulrice.databinding.DataState, net.ulrice.databinding.DataState)
	 */
	protected void stateChanged(Element element) {
		if(element.isValid()) {
			invElements.remove(element);
		} else {
			invElements.add(element);
		}
		
		if(element.isDirty() && elementIdMap.containsKey(element.getUniqueId())) {
            modElements.add(element);            
		}
		if(!element.isDirty() && elementIdMap.containsKey(element.getUniqueId())) {
            modElements.remove(element);            
		}		
	    
		boolean oldValid = valid;
		boolean oldDirty = dirty;
		
	    valid = invElements.isEmpty();
	    dirty = !modElements.isEmpty() || !delElements.isEmpty() || !newElements.isEmpty();
	    
	    if(oldValid != valid || oldDirty != dirty) {
	        fireStateChanged();
	    }
	    
	}

	private void fireStateChanged() {
        IFAttributeModelEventListener[] listeners = listenerList.getListeners(IFAttributeModelEventListener.class);
        if(listeners != null) {
            for(IFAttributeModelEventListener listener : listeners) {
                listener.stateChanged(null, this);
            }
        }
    }

    /**
	 * @param columnDefinition
	 */
	public void addColumn(ColumnDefinition<?> columnDefinition) {
		columns.add(columnDefinition);
	}

	/**
	 * @return the columns
	 */
	public List<ColumnDefinition<? extends Object>> getColumns() {
	    return columns;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#gaChanged(net.ulrice.databinding.IFGuiAccessor, java.lang.Object)
	 */
	@Override
	public void gaChanged(IFViewAdapter viewAdapter, Object value) {
		fireUpdateViews();
	}
    
    public void fireUpdateViews() {
    	if(viewAdapterList != null) {
    		for(IFViewAdapter viewAdapter: viewAdapterList) {
    			viewAdapter.updateFromBinding(this);
    		}
    	}
    }

	@Override
	public void addViewAdapter(IFViewAdapter viewAdapter) {
		viewAdapterList.add(viewAdapter);
		viewAdapter.updateFromBinding(this);
	}
	
	public int getIndexOfElement(Element element) {
		return elements.indexOf(element);
	}

	
	public IFValueConverter getValueConverter() {
		return valueConverter;
	}
	
	@Override
	public void setValueConverter(IFValueConverter valueConverter) {
		this.valueConverter = valueConverter;
	}
	
	@Override
	public boolean isInitialized() {
		return initialized;
	}
	
	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	protected void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	@Override
	public boolean isValid() {
		return valid;
	}
	
	protected void setValid(boolean valid) {
		this.valid = valid;
	}
	
	
	@Override
	public ValidationResult getValidationResult() {
		ValidationResult result = new ValidationResult();
		if(invElements != null) {
			for(Element element : invElements) {
				result.addValidationErrors(element.getValidationErrors());
			}
		}
		
		return result;
	}

	@Override
	public List<String> getValidationFailures() {
		List<String> result = new ArrayList<String>();
		if(invElements != null) {
			for(Element element : invElements) {
				result.addAll(element.getValidationFailures());
			}
		}
		
		return result;
	}


	
	@Override
	public Object getCurrentValue() {
		List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());
		
		for(Element element : elements) {
			result.add(element.getCurrentValue());
		}
		
		return result;
	}
	
	@Override
	public Object getOriginalValue() {
		List<Object> result = new ArrayList<Object>(elements == null ? 0 : elements.size());
		
		for(Element element : elements) {
			result.add(element.getOriginalValue());
		}
		
		return result;
	}
	

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#read()
	 */
	@Override
	public void read() {
		initialized = true;
		dirty = false;
		valid = true;

		int numRows = tableMVA.getSize();
		
		elements.clear();
		elementIdMap.clear();
		newElements.clear();
		modElements.clear();
		invElements.clear();
		
		for(int i = 0; i < numRows; i++) {
			
			Object value = tableMVA.getValue(i);
			Element elem = createElement(value, false, true);
			elem.readObject();

			elementIdMap.put(elem.getUniqueId(), elem);				
			elements.add(elem);
		}
		fireUpdateViews();
	}


	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#write()
	 */
	@Override
	public void write() {		
		int numRows = elements.size();
		
		for(int i = 0; i < numRows; i++) {
			Element elem = elements.get(i);
			elem.writeObject();
			tableMVA.setValue(i, elem.getOriginalValue());
		}
	}

	protected Object createEmptyElementObject() {
		return tableMVA.newObjectInstance();
	}
	
	public Element addElement(Object value) {
	    return addElement(value, false, true);
	}

	public Element addElement(Object value, boolean dirty, boolean valid) {
		if(value == null) {
			value = createEmptyElementObject();
		}
		
		Element element = createElement(value, dirty, valid);
		elements.add(element);
		elementIdMap.put(element.getUniqueId(), element);
		newElements.add(element);
		stateChanged(element);
		fireUpdateViews();
		return element;
	}

    public Element addElement(int index, Object value) {
        return addElement(index, value, false, true);
    }
    
    public Element addElement(int index, Object value, boolean dirty, boolean valid) {
		if(value == null) {
			value = createEmptyElementObject();
		}
		
		Element element = createElement(value, dirty, valid);
		elements.add(index, element);
		elementIdMap.put(element.getUniqueId(), element);
		newElements.add(element);		
		stateChanged(element);
		fireUpdateViews();
		return element;
	}
	
	public void delElement(int index) {
		Element delElement = elements.remove(index);
		delElements.add(delElement);	
		stateChanged(delElement);
		fireUpdateViews();
	}
	
	public List getDeletedObjects() {
		List result = new ArrayList();
		for(Element element : delElements) {
			result.add(element.getCurrentValue());
		}
		return result;
	}
	
	public List getCreatedObjects() {
		List result = new ArrayList();
		for(Element element : newElements) {
			result.add(element.getCurrentValue());
		}
		return result;
	}
	
	public List getModifiedObjects() {
		List result = new ArrayList();
		for(Element element : modElements) {
			result.add(element.getCurrentValue());
		}
		return result;
	}
	
	public List getInvalidObjects() {
		List result = new ArrayList();
		for(Element element : invElements) {
			result.add(element.getCurrentValue());
		}
		return result;
	}

	public Element getElementById(String uniqueId) {
		return elementIdMap.get(uniqueId);
	}

	protected Object cloneObject(Object value) {
		return tableMVA.cloneObject(value);
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
    
    public List<Element> getCreatedElements() {
        return new ArrayList<Element>(newElements);
    }
    
    public List<Element> getModifiedElements() {
        return new ArrayList<Element>(modElements);
    }
    
    public List<Element> getDeletedElements() {
        return new ArrayList<Element>(delElements);
    }
	
	public void commitElement(Element element) {
	    element.writeObject();
	    element.readObject();
	    stateChanged(element);
        fireUpdateViews();
	}
	
	public void rollbackElement(Element element) {
        element.readObject();
        stateChanged(element);
        fireUpdateViews();
	}
	
	public void markAsFaulty(Element element, String message, Throwable th) {
	    element.addElementValidationError(new ValidationError(null, message, th));
	}
}


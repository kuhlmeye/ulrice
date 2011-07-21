package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.bufferedbinding.IFBufferedBinding;
import net.ulrice.databinding.bufferedbinding.IFBufferedBindingEventListener;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

public abstract class AbstractTableAM implements IFBufferedBinding {

	protected List<Element> elements = new ArrayList<Element>();
	protected Map<String, Element> elementIdMap = new HashMap<String, Element>();
	
	private IFValidator validator;
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

	public AbstractTableAM(String id, boolean readOnly) {
		super();

		nextUniqueId = System.currentTimeMillis();
		
		this.id = id;
		this.listenerList = new EventListenerList();	
		this.readOnly = readOnly;		
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public GenericAM getCellAttributeModel(int rowIndex, int columnIndex) {
		return getElementAt(rowIndex).getCellAtributeModel(columnIndex);
	}

	/**
	 * @param value
	 * @return
	 */
	protected Element createElement(Object value) {
		String uniqueId = Long.toHexString(nextUniqueId++);
		Element elem = new Element(this, uniqueId, columns, value, isReadOnly());
		return elem;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#addAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFBufferedBindingEventListener)
	 */
	@Override
	public void addAttributeModelEventListener(IFBufferedBindingEventListener listener) {
		listenerList.add(IFBufferedBindingEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#removeAttributeModelEventListener(net.ulrice.databinding.bufferedbinding.IFBufferedBindingEventListener)
	 */
	@Override
	public void removeAttributeModelEventListener(IFBufferedBindingEventListener listener) {
		listenerList.remove(IFBufferedBindingEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#setValidator(net.ulrice.databinding.validation.IFValidator)
	 */
	@Override
	public void setValidator(IFValidator validator) {
		this.validator = validator;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getValidator()
	 */
	@Override
	public IFValidator getValidator() {
		return validator;
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
		return !getElementAt(rowIndex).isReadOnly(columnIndex);
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
	protected void dataChanged(Element element, String columnId, Object newValue, Object oldValue) {
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
	    
	    valid = invElements.isEmpty();
	    dirty = !modElements.isEmpty() || !delElements.isEmpty() || !newElements.isEmpty();
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

	public Element addElement(Object value) {
		Element element = createElement(value);
		elements.add(element);
		return element;
	}
	
	public int getIndexOfElement(Element element) {
		return elements.indexOf(element);
	}
	
	@Override
	public ValidationResult getValidationResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getValidationFailures() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object getOriginalValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object getCurrentValue() {
		return elements;
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
}
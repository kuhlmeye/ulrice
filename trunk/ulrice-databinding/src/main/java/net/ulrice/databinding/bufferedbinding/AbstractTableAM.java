package net.ulrice.databinding.bufferedbinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationResult;
import net.ulrice.databinding.viewadapter.IFViewAdapter;

public abstract class AbstractTableAM implements IFBinding, IFAttributeModel, IFElementChangeListener {

	protected DataState state = DataState.NotChanged;
	protected List<Element> elements = new ArrayList<Element>();
	protected Map<String, Element> elementIdMap = new HashMap<String, Element>();
	
	private IFValidator validator;
	private EventListenerList listenerList;
	private String id;
	private List<ColumnDefinition<? extends Object>> columns = new ArrayList<ColumnDefinition<? extends Object>>();
	private boolean editable;
	private long nextUniqueId;
	private Set<Element> newElements = new HashSet<Element>();
	private Set<Element> modElements = new HashSet<Element>();
	private Set<Element> delElements = new HashSet<Element>();
	private Set<Element> invElements = new HashSet<Element>();
	
	private List<IFViewAdapter> viewAdapterList = new ArrayList<IFViewAdapter>();

	public AbstractTableAM(String id, boolean editable) {
		super();

		nextUniqueId = System.currentTimeMillis();
		
		this.id = id;
		this.listenerList = new EventListenerList();
		this.state = DataState.NotInitialized;
		this.editable = editable;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getId()
	 */
	@Override
	public String getId() {
		return id;
	}


	/**
	 * @param value
	 * @return
	 */
	protected Element createElement(Object value) {
		String uniqueId = Long.toHexString(nextUniqueId++);
		Element elem = new Element(uniqueId, columns, value, isReadOnly());
		elem.addElementChangeListener(this);
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

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFAttributeModel#getState()
	 */
	@Override
	public DataState getState() {
		return state;
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
		return getElementAt(rowIndex).isEditable(columnIndex);
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
		return editable;
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFElementChangeListener#dataChanged(net.ulrice.databinding.impl.am.Element, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void dataChanged(Element element, String columnId, Object newValue, Object oldValue) {
	    fireUpdateViews();
	}

	/**
	 * @see net.ulrice.databinding.bufferedbinding.IFElementChangeListener#stateChanged(net.ulrice.databinding.impl.am.Element, net.ulrice.databinding.DataState, net.ulrice.databinding.DataState)
	 */
	@Override
	public void stateChanged(Element element, DataState newState, DataState oldState) {
	    if(!DataState.Invalid.equals(newState)) {
	        invElements.remove(element);
	    }
	    
	    switch(newState) {
	        case Invalid:
	            invElements.add(element);
	            break;
	        case Changed:
	            if(elementIdMap.containsKey(element.getUniqueId())) {
	                modElements.add(element);
	            }
	            break;
	        case NotInitialized:
	        case NotChanged:
	            if(elementIdMap.containsKey(element.getUniqueId())) {
	                modElements.remove(element);
	            }
	            break;
	    }
	    
	    if(!invElements.isEmpty()) {
	        state = DataState.Invalid;
	    }
	    else if(!modElements.isEmpty() || !delElements.isEmpty() || !newElements.isEmpty()) {
	        state = DataState.Changed;
	    } else {
	        state = DataState.NotChanged;
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
    			viewAdapter.updateBinding(this);
    		}
    	}
    }

	@Override
	public void addViewAdapter(IFViewAdapter viewAdapter) {
		viewAdapterList.add(viewAdapter);
		viewAdapter.updateBinding(this);
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
}
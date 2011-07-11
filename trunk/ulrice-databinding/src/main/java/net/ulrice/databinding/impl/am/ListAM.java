package net.ulrice.databinding.impl.am;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.IFAttributeModel;
import net.ulrice.databinding.IFAttributeModelEventListener;
import net.ulrice.databinding.IFDataAccessor;
import net.ulrice.databinding.IFElementChangeListener;
import net.ulrice.databinding.IFGuiAccessor;
import net.ulrice.databinding.IFValidator;
import net.ulrice.databinding.impl.validation.ValidationResult;

/**
 * @author christof
 * 
 */
public class ListAM<T extends List<S>, S> implements IFAttributeModel<T>, ListModel, TableModel, IFElementChangeListener<S> {

	private IFValidator<T> validator;
	private DataState state = DataState.NotChanged;
	private EventListenerList listenerList;
	private String id;
	private List<Element<S>> elements = new ArrayList<Element<S>>();
	private List<ColumnDefinition<? extends Object>> columns = new ArrayList<ColumnDefinition<? extends Object>>();
	private boolean editable;
	private IFDataAccessor<T> dataAccessor;
	private long nextUniqueId;
	
	private Map<String, Element<S>> elementIdMap = new HashMap<String, Element<S>>();
	private Set<Element<S>> newElements = new HashSet<Element<S>>();
	private Set<Element<S>> modElements = new HashSet<Element<S>>();
    private Set<Element<S>> delElements = new HashSet<Element<S>>();
    private Set<Element<S>> invElements = new HashSet<Element<S>>();

	public ListAM(String id, IFDataAccessor<T> dataAccessor, boolean editable) {
		
		nextUniqueId = System.currentTimeMillis();
		
		this.id = id;
		this.listenerList = new EventListenerList();
		this.state = DataState.NotInitialized;
		this.editable = editable;
		this.dataAccessor = dataAccessor;
	}
	
	public ListAM(String id, IFDataAccessor<T> dataAccessor) {
		this(id, dataAccessor, true);
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#getCurrentValue()
	 */
	@SuppressWarnings("unchecked")
	@Override
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
	@Override
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
	 * @see net.ulrice.databinding.IFAttributeModel#setValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		T tValue = (T) value;
		setCurrentValue(tValue);
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#setCurrentValue(java.lang.Object)
	 */
	@Override
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
	 * @see net.ulrice.databinding.IFAttributeModel#read()
	 */
	@Override
	public void read() {
		directRead(getDataAccessor().readValue());	
	}
	
	/**
	 * @see net.ulrice.databinding.IFAttributeModel#directRead(java.lang.Object)
	 */
	@Override
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
	 * @param value
	 * @return
	 */
	private Element<S> createElement(S value) {
		String uniqueId = Long.toHexString(nextUniqueId++);
		Element<S> elem = new Element<S>(uniqueId, columns, value, isEditable());
		elem.addElementChangeListener(this);
		return elem;
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#write()
	 */
	@Override
	public void write() {
		getDataAccessor().writeValue(directWrite());
	}


	/**
	 * @see net.ulrice.databinding.IFAttributeModel#directWrite()
	 */
	@SuppressWarnings("unchecked")
	@Override
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
	 * @see net.ulrice.databinding.IFAttributeModel#addAttributeModelEventListener(net.ulrice.databinding.IFAttributeModelEventListener)
	 */
	@Override
	public void addAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
		listenerList.add(IFAttributeModelEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#removeAttributeModelEventListener(net.ulrice.databinding.IFAttributeModelEventListener)
	 */
	@Override
	public void removeAttributeModelEventListener(IFAttributeModelEventListener<T> listener) {
		listenerList.remove(IFAttributeModelEventListener.class, listener);
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#setValidator(net.ulrice.databinding.IFValidator)
	 */
	@Override
	public void setValidator(IFValidator<T> validator) {
		this.validator = validator;
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#getValidator()
	 */
	@Override
	public IFValidator<T> getValidator() {
		return validator;
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#getState()
	 */
	@Override
	public DataState getState() {
		return state;
	}

	/**
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void addListDataListener(ListDataListener listener) {
		listenerList.add(ListDataListener.class, listener);
	}

	/**
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void removeListDataListener(ListDataListener listener) {
		listenerList.remove(ListDataListener.class, listener);

	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Element<S> getElementAt(int index) {
		return elements.get(index);
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return elements == null ? 0 : elements.size();
	}

	/**
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void addTableModelListener(TableModelListener listener) {
		listenerList.add(TableModelListener.class, listener);
	}

	/**
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void removeTableModelListener(TableModelListener listener) {
		listenerList.remove(TableModelListener.class, listener);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columns == null ? 0 : columns.size();
	}	

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columns.get(columnIndex).getColumnClass();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return columns.get(columnIndex).getColumnName();
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return elements == null ? 0 : elements.size();
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return getElementAt(rowIndex).isEditable(columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getElementAt(rowIndex).getValueAt(columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		getElementAt(rowIndex).setValueAt(columnIndex, aValue);
	}

	/**
	 * @see net.ulrice.databinding.IFAttributeModel#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @return the dataAccessor
	 */
	public IFDataAccessor<T> getDataAccessor() {
		return dataAccessor;
	}

	/**
	 * @see net.ulrice.databinding.IFElementChangeListener#dataChanged(net.ulrice.databinding.impl.am.Element, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void dataChanged(Element<S> element, String columnId, Object newValue, Object oldValue) {
	    // TODO Refine event.
	    fireTableChanged(new TableModelEvent(this));
	}

    /**
     * Informs all listeners about a table model event.
     * 
     * @param e The event.
     */
    private void fireTableChanged(TableModelEvent e) {
        TableModelListener[] listeners = listenerList.getListeners(TableModelListener.class);
        if(listeners != null)  {
            for(TableModelListener listener : listeners) {
                listener.tableChanged(e);
            }
        }
    }

	/**
	 * @see net.ulrice.databinding.IFElementChangeListener#stateChanged(net.ulrice.databinding.impl.am.Element, net.ulrice.databinding.DataState, net.ulrice.databinding.DataState)
	 */
	@Override
	public void stateChanged(Element<S> element, DataState newState, DataState oldState) {
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
     * @see net.ulrice.databinding.IFAttributeModel#gaChanged(net.ulrice.databinding.IFGuiAccessor, java.lang.Object)
     */
    @Override
    public void gaChanged(IFGuiAccessor<?, ?> guiAccessor, T value) {
        //TODO Implement me.
    }

    /**
     * @see net.ulrice.databinding.IFAttributeModel#getValidationErrors()
     */
    @Override
    public ValidationResult getValidationErrors() {
        // TODO Auto-generated method stub
        return null;
    }
}

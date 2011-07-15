package net.ulrice.databinding.bufferedbinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.DataState;
import net.ulrice.databinding.modelaccess.IFDynDataAccessor;

/**
 * The element of the list attribute model. It manages the models for all
 * attributes of a data row.
 * 
 * @author christof
 */
public class Element {

    /** The unique identifier of this element. */
    private String uniqueId;

    /** The definition of the columns. */
    private List<ColumnDefinition<? extends Object>> columns;

    /** The list of models. */
    private List<GenericAM<? extends Object>> modelList;

    /** The map of the models. Key is the column identifier. */
    private Map<String, GenericAM<? extends Object>> idModelMap;

    /** The value read from the list data. */
    private Object valueObject;

    /** The current state of the data in this element. */
    private DataState state = DataState.NotInitialized;

    /** The event listeners. */
    private EventListenerList listenerList = new EventListenerList();

    /** Flag, if this element is editable. */
    private boolean editable;

    /**
     * Creates a new element.
     * 
     * @param uniqueId The unique identifier.
     * @param columns The list of column definitions
     * @param valueObject The value.
     * @param editable True, if this element should be editable.
     */
    public Element(String uniqueId, List<ColumnDefinition<? extends Object>> columns, Object valueObject, boolean editable) {
        this.uniqueId = uniqueId;
        this.modelList = new ArrayList<GenericAM<? extends Object>>();
        this.idModelMap = new HashMap<String, GenericAM<? extends Object>>();
        this.valueObject = valueObject;
        this.columns = columns;
        this.editable = editable;
        readObject();
    }

    /**
     * Adds an element change listener
     * 
     * @param listener The listener to be added to this element.
     */
    public void addElementChangeListener(IFElementChangeListener listener) {
        listenerList.add(IFElementChangeListener.class, listener);
    }

    /**
     * Removes an element change listener
     * 
     * @param listener The listener to be removed from this element.
     */
    public void removeElementChangeListener(IFElementChangeListener listener) {
        listenerList.remove(IFElementChangeListener.class, listener);
    }

    /**
     * Returns, if a cell is editable.
     * 
     * @param columnIndex The index of the column.
     * @return True, if the value is editable. False otherwise.
     */
    public boolean isEditable(int columnIndex) {

        if (!editable) {
            return false;
        }

        ColumnDefinition<?> columnDefinition = columns.get(columnIndex);
        if (columnDefinition.isEditable()) {
            return modelList.get(columnIndex).isReadOnly();
        }

        return false;
    }

    /**
     * Returns the cell value.
     * 
     * @param columnIndex The index of the column
     * @return The cell value as an object.
     * @throws IndexOutOfBoundsException If the column index is not a valid
     *             index.
     */
    public Object getValueAt(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= modelList.size()) {
            throw new IndexOutOfBoundsException("ColumnIndex: " + columnIndex + ", Size: " + modelList.size());
        }
        return modelList.get(columnIndex).getCurrentValue();
    }

    /**
     * Returns the cell value.
     * 
     * @param columnId The identifier of the column
     * @return The cell value as an object.
     * @throws IllegalStateException If the column identifer is not valid
     */
    public Object getValueAt(String columnId) {
        if (!idModelMap.containsKey(columnId)) {
            throw new IllegalStateException("Unknown column id: " + columnId);
        }
        return idModelMap.get(columnId).getCurrentValue();
    }

    /**
     * Set the value of a cell.
     * 
     * @param columnIndex Index of the column
     * @param aValue The value.
     */
    public void setValueAt(int columnIndex, Object aValue) {
    	GenericAM<?> model = modelList.get(columnIndex);
        setValue(model, columns.get(columnIndex).getId(), aValue);
    }

    /**
     * Set the value of a cell
     * 
     * @param columnId The identifier of the column
     * @param aValue The value
     */
    public void setValueAt(String columnId, Object aValue) {
    	GenericAM<?> model = idModelMap.get(columnId);
        setValue(model, columnId, aValue);
    }

    /**
     * Internal method for setting a value.
     * 
     * @param model The attribute model
     * @param columnId The identifier of the column 
     * @param aValue The value
     */
    private void setValue(GenericAM<?> model, String columnId, Object aValue) {
        if (model == null) {
            return;
        }
        Object oldValue = model.getCurrentValue();

        model.setValue(aValue);
        fireValueChanged(columnId, aValue, oldValue);
        updateState();
    }

    /**
     * Update the state of this element. This method checks all attribute models
     * and calculates the resulting state.
     */
    private void updateState() {
        DataState newState = DataState.NotInitialized;
        try {
            if (modelList != null) {
                for (IFAttributeModel<?> value : modelList) {
                    switch (value.getState()) {
                        case Invalid:
                            newState = DataState.Invalid;
                            return;
                        case Changed:
                            newState = DataState.Changed;
                            break;
                        case NotChanged:
                            if (DataState.Changed.equals(state)) {
                                newState = DataState.NotChanged;
                            }
                            break;
                        case NotInitialized:
                            break;
                    }
                }
            }
        } finally {
            DataState oldState = this.state;
            this.state = newState;
            if (!newState.equals(oldState)) {
                fireStateChanged(newState, oldState);
            }
        }
    }

    /**
     * Fires the event that the state changed.
     * 
     * @param newState The new state
     * @param oldState The old state
     */
    private void fireStateChanged(DataState newState, DataState oldState) {
        if (listenerList != null) {
            for (IFElementChangeListener listener : listenerList.getListeners(IFElementChangeListener.class)) {
                listener.stateChanged(this, newState, oldState);
            }
        }
    }


    /**
     * @param aValue
     * @param columnId 
     * @param newValue 
     * @param oldValue 
     */
    private void fireValueChanged(String columnId, Object newValue, Object oldValue) {
        if (listenerList != null) {
            for (IFElementChangeListener listener : listenerList.getListeners(IFElementChangeListener.class)) {
                listener.dataChanged(this, columnId, newValue, oldValue);
            }
        }
    }
    /**
     * Write the object managed by the element in the value object.
     * 
     * @return The value object
     */
    @SuppressWarnings("unchecked")
    public Object writeObject() {
        if (modelList != null) {
            for (int i = 0; i < modelList.size(); i++) {
                GenericAM attributeModel = modelList.get(i);
                IFDynDataAccessor dataAccessor = columns.get(i).getDataAccessor();

                Object object = attributeModel.directWrite();
                dataAccessor.setValue(getValueObject(), object);
            }
        }
        return getValueObject();
    }

    /**
     * Read the object from the value object 
     */
    @SuppressWarnings("unchecked")
    public void readObject() {
        modelList.clear();

        if (columns != null) {
            for (ColumnDefinition<? extends Object> column : columns) {
                GenericAM attributeModel = column.createAM();
                modelList.add(attributeModel);
                idModelMap.put(attributeModel.getId(), attributeModel);

                Object value = column.getDataAccessor().getValue(getValueObject());
                attributeModel.directRead(value);
            }
        }
    }

    /**
     * Return the current value object.
     * 
     * @return The value object.
     */
    public Object getValueObject() {
        return valueObject;
    }

    /**
     * Return the unique id.
     * 
     * @return the uniqueId
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Return the state of this element.
     * 
     * @return The state
     */
    public Object getState() {
        return state;
    }
}

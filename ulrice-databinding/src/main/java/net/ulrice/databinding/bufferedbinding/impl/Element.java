package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition.ColumnType;
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

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
	private Object originalValue;

	private boolean originalValueDirty = false;

	private boolean originalValueValid = true;

	/** The event listeners. */
	private EventListenerList listenerList = new EventListenerList();

	/** Flag, if this element is editable. */
	private boolean readOnly;

	private boolean dirty;
	private boolean valid;
	private boolean inserted;
	private boolean removed;

	private TableAM tableAM;

	private ValidationResult validationResult;
	
	private List<Element> childElements = new ArrayList<Element>();	

	/**
	 * Creates a new element.
	 * 
	 * @param tableAM
	 * @param uniqueId
	 *            The unique identifier.
	 * @param columns
	 *            The list of column definitions
	 * @param valueObject
	 *            The value.
	 * @param editable
	 *            True, if this element should be readonly.
	 */
	public Element(TableAM tableAM, String uniqueId,
			List<ColumnDefinition<? extends Object>> columns,
			Object valueObject, boolean readOnly, boolean dirty, boolean valid, boolean inserted) {
		this.originalValueDirty = dirty;
		this.originalValueValid = valid;
		this.tableAM = tableAM;
		this.uniqueId = uniqueId;
		this.modelList = new ArrayList<GenericAM<? extends Object>>();
		this.idModelMap = new HashMap<String, GenericAM<? extends Object>>();
		this.originalValue = tableAM.cloneObject(valueObject);
		this.columns = columns;
		this.readOnly = readOnly;
		
		this.dirty = false;
		this.valid = true;
		
		readObject();
	}
	
	public void setReadOnly(String columnId, boolean readOnly) {
        if (!idModelMap.containsKey(columnId)) {
            throw new IllegalStateException("Unknown column id: " + columnId);
        }
        idModelMap.get(columnId).setReadOnly(readOnly);	    
	}
	
	public void setReadOnly(int columnIndex, boolean readOnly) {
	    if (columnIndex < 0 || columnIndex >= modelList.size()) {
	        throw new IndexOutOfBoundsException("ColumnIndex: " + columnIndex
	                 + ", Size: " + modelList.size());
	    }
	    modelList.get(columnIndex).setReadOnly(readOnly);
	}

	/**
	 * Returns, if a cell is readonly.
	 * 
	 * @param columnIndex
	 *            The index of the column.
	 * @return True, if the value is readonly. False otherwise.
	 */
	public boolean isReadOnly(int columnIndex) {

		if (readOnly) {
			return true;
		}

		ColumnDefinition<?> columnDefinition = columns.get(columnIndex);

		ColumnType type = columnDefinition.getColumnType();
		switch (type) {
			case Editable:
				return modelList.get(columnIndex).isReadOnly();
			case ReadOnly:
				return true;
			case NewEditable:
				if(tableAM.isNew(this)) {
					return modelList.get(columnIndex).isReadOnly();					
				} else {
					return true;
				}
		}		

		return true;
	}

	/**
	 * Returns the cell value.
	 * 
	 * @param columnIndex
	 *            The index of the column
	 * @return The cell value as an object.
	 * @throws IndexOutOfBoundsException
	 *             If the column index is not a valid index.
	 */
	public Object getValueAt(int columnIndex) {
		if (columnIndex < 0 || columnIndex >= modelList.size()) {
			throw new IndexOutOfBoundsException("ColumnIndex: " + columnIndex
					+ ", Size: " + modelList.size());
		}
		return modelList.get(columnIndex).getCurrentValue();
	}

	/**
	 * Returns the cell value.
	 * 
	 * @param columnId
	 *            The identifier of the column
	 * @return The cell value as an object.
	 * @throws IllegalStateException
	 *             If the column identifer is not valid
	 */
	public Object getValueAt(String columnId) {
		if (!idModelMap.containsKey(columnId)) {
			throw new IllegalStateException("Unknown column id: " + columnId);
		}
		return idModelMap.get(columnId).getCurrentValue();
	}

	
	public Object getOriginalValueAt(int columnIndex) {
	    if(columnIndex < 0 || columnIndex >= modelList.size()) {
	        throw new IndexOutOfBoundsException("ColumnIndex: " + columnIndex + ", Size: " + modelList.size());
	    }
	    return modelList.get(columnIndex).getOriginalValue();
	}

  
    public Object getOriginalValueAt(String columnId) {
        if (!idModelMap.containsKey(columnId)) {
            throw new IllegalStateException("Unknown column id: " + columnId);
        }
        return idModelMap.get(columnId).getOriginalValue();
    }
    
	/**
	 * Set the value of a cell.
	 * 
	 * @param columnIndex
	 *            Index of the column
	 * @param aValue
	 *            The value.
	 */
	public void setValueAt(int columnIndex, Object aValue) {
		GenericAM<?> model = modelList.get(columnIndex);
		setValue(model, columns.get(columnIndex).getId(), aValue);
		
		for(Element elem: childElements){
		    elem.setValueAt(columnIndex, aValue);
		}
	}

	/**
	 * Set the value of a cell
	 * 
	 * @param columnId
	 *            The identifier of the column
	 * @param aValue
	 *            The value
	 */
	public void setValueAt(String columnId, Object aValue) {
		GenericAM<?> model = idModelMap.get(columnId);
		setValue(model, columnId, aValue);
	}

	/**
	 * Internal method for setting a value.
	 * 
	 * @param model
	 *            The attribute model
	 * @param columnId
	 *            The identifier of the column
	 * @param aValue
	 *            The value
	 */
	private void setValue(GenericAM<?> model, String columnId, Object aValue) {
		if (model == null) {
			return;
		}
		clearElementValidationErrors();
		model.setValue(aValue);
		fireValueChanged(columnId);
		updateState();
	}

	/**
	 * Update the state of this element. This method checks all attribute models
	 * and calculates the resulting state.
	 */
	private void updateState() {
		boolean oldValid = valid;
		boolean oldDirty = dirty;
		
		if(childElements != null && childElements.size()>0 ){
		    valid = true;
		    dirty = false;
		}else if (modelList != null) {
		    dirty = false;
            valid = validationResult == null || validationResult.isValid();
            for (IFAttributeModel<?> model : modelList) {
                dirty |= model.isDirty();
                valid &= model.isValid();
            }
		}

		dirty |= originalValueDirty;
		valid &= originalValueValid;

		if (oldDirty != dirty || oldValid != valid) {
	        fireStateChanged();
		}
	}

	/**
	 * Fires the event that the state changed.
	 * 
	 * @param newState
	 *            The new state
	 * @param oldState
	 *            The old state
	 */
	private void fireStateChanged() {
		tableAM.elementStateChanged(this);
	}

	/**
	 * @param aValue
	 * @param columnId
	 * @param newValue
	 * @param oldValue
	 */
	private void fireValueChanged(String columnId) {
		tableAM.elementDataChanged(this, columnId);
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
				if (!attributeModel.isReadOnly()) {

					IFDynamicModelValueAccessor dataAccessor = columns.get(i)
							.getDataAccessor();

					Object value = attributeModel.directWrite();
					Object converted = (attributeModel.getValueConverter() != null ? attributeModel
							.getValueConverter().viewToModel(value) : value);
					dataAccessor.setValue(getOriginalValue(), converted);
				}
			}
		}
		return getOriginalValue();
	}

	public void setCurrentValue(Object currentValue) {
		setCurrentValue(currentValue, false, true);
	}

	public void setCurrentValue(Object currentValue, boolean dirty, boolean valid) {
	    setCurrentValue(currentValue, dirty, valid, false);
	}
	
	public void setCurrentValue(Object currentValue, boolean dirty,
        boolean valid, boolean omitReadOnly) {
    this.originalValueDirty = dirty;
    this.originalValueValid = valid;
    this.originalValue = currentValue;
    if (modelList != null) {
        for (int i = 0; i < modelList.size(); i++) {
            if(isReadOnly(i) && omitReadOnly){
                continue;
            }
            
            GenericAM model = modelList.get(i);
            IFDynamicModelValueAccessor dataAccessor = columns.get(i).getDataAccessor();

            Object value = dataAccessor.getValue(currentValue);             
            Object converted = (model.getValueConverter() != null ? model
                    .getValueConverter().modelToView(value) : value);
            model.setValue(converted);
        }
        fireValueChanged(null);
        updateState();
    }
}
	

	public Object getCurrentValue() {
		Object result = tableAM.cloneObject(getOriginalValue());
		if (modelList != null) {
			for (int i = 0; i < modelList.size(); i++) {
				GenericAM attributeModel = modelList.get(i);
				if (!attributeModel.isReadOnly()) {
					IFDynamicModelValueAccessor dataAccessor = columns.get(i)
							.getDataAccessor();

					Object value = attributeModel.getCurrentValue();
					Object converted = (attributeModel.getValueConverter() != null ? attributeModel
							.getValueConverter().viewToModel(value) : value);
					dataAccessor.setValue(result, converted);
				}
			}
		}
		return result;
	}

	/**
	 * Read the object from the value object
	 */
	public void readObject() {
		modelList.clear();
		originalValueDirty = false;
		originalValueValid = true;
        readAdditionalColumns(columns, true);
    }

    @SuppressWarnings("unchecked")
    public void readAdditionalColumns(List<ColumnDefinition< ? extends Object>> columns, boolean updateState) {

        if (columns != null) {
            for (ColumnDefinition< ? extends Object> column : columns) {
                GenericAM attributeModel = column.createAM();
                attributeModel.addValidator(new IFValidator() {

                    @Override
                    public ValidationResult isValid(IFBinding bindingId, Object attribute) {
                        return validationResult;
                    }

                    @Override
                    public ValidationResult getLastValidationErrors() {
                        return validationResult;
                    }

                    @Override
                    public void clearValidationErrors() {
                        validationResult = new ValidationResult();
                    }
                });
                attributeModel.setReadOnly(column.getColumnType().equals(ColumnType.ReadOnly));
                modelList.add(attributeModel);
                idModelMap.put(attributeModel.getId(), attributeModel);

                if (getOriginalValue() != null) {
                    Object value = column.getDataAccessor().getValue(getOriginalValue());
                    Object converted =
                            (column.getValueConverter() != null ? column.getValueConverter().modelToView(value)
                                    : value);
                    attributeModel.directRead(converted);

                }
            }
            if (updateState)
                updateState();
        }
    }

	/**
	 * Return the current value object.
	 * 
	 * @return The value object.
	 */
	public Object getOriginalValue() {
		return originalValue;
	}

	/**
	 * Return the unique id.
	 * 
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		return uniqueId;
	}

	protected GenericAM getCellAtributeModel(int columnIndex) {
		return modelList.get(columnIndex);
	}

	public boolean isDirty() {
		return dirty || isInsertedOrRemoved();
	}

	public boolean isValid() {
		return valid;
	}

	public List<ValidationError> getValidationErrors() {
		List<ValidationError> errors = new ArrayList<ValidationError>(
				validationResult.getValidationErrors());

		if (modelList != null) {
			for (GenericAM<?> model : modelList) {
				if (model.getValidationResult() != null) {
					errors.addAll(model.getValidationResult()
							.getValidationErrors());
				}
			}
		}
		return errors;
	}



    public List<String> getValidationFailures() {
        
        List<String> result = new ArrayList<String>();
        if(modelList != null) {
            for(GenericAM<?> attributeModel : modelList) {
                result.addAll(attributeModel.getValidationFailures());
            }
        }

        for (ValidationError elementError : validationResult.getValidationErrors()) {
            result.add(elementError.getMessage());
        }
        
        return result;
    }
	
    public List<String> getValidationFailures(String columnId) {
        List<String> errors = new ArrayList<String>();

        if (idModelMap.containsKey(columnId)) {
            GenericAM<?> model = idModelMap.get(columnId);
            if (model.getValidationResult() != null) {
                errors.addAll(model.getValidationFailures());
            }
        }
        
        for (ValidationError elementError : validationResult.getValidationErrors()) {
            errors.add(elementError.getMessage());
        }
        return errors;
    }   

	public void addColumnValidationError(String columnId, String message) {
	    GenericAM<? extends Object> genericAM = idModelMap.get(columnId);
	    if(genericAM != null) {
	        genericAM.addExternalValidationError(new ValidationError(genericAM, message, null));
	    }
        updateState();
	}
	
	public void addElementValidationError(ValidationError validationError) {
		validationResult.addValidationError(validationError);
		if (modelList != null) {
			for (GenericAM<?> model : modelList) {
				model.recalculateState();
			}
		}
		updateState();
	}

	public void removeElementValidationError(ValidationError validationError) {
		validationResult.removeValidationError(validationError);
		if (modelList != null) {
			for (GenericAM<?> model : modelList) {
				model.recalculateState();
			}
		}
		updateState();
	}

	public ValidationResult getElementValidationErrors() {
		return validationResult;
	}

	public void clearElementValidationErrors() {
		validationResult = new ValidationResult();
		if (modelList != null) {
			for (GenericAM<?> model : modelList) {
				model.recalculateState();
			}
		}
		updateState();
	}

	public boolean isOriginalValueDirty() {
	    if(getChildCount()>0){
            return false;
        }
		return originalValueDirty;
	}

	public boolean isOriginalValueValid() {
	    if(getChildCount()>0){
            return true;
        }
		return originalValueValid;
	}

	public boolean isColumnValid(int column) {
	    if(getChildCount()>0){
	        return true;
	    }
		return modelList.get(column).isValid();
	}

	public boolean isColumnDirty(int column) {
	    if(getChildCount()>0){
            return false;
        }
		return modelList.get(column).isDirty();
	}

    public boolean isColumnDirty(String columnId) {
        if(getChildCount()>0){
            return false;
        }
        return idModelMap.containsKey(columnId) ? idModelMap.get(columnId).isDirty() : false;
    }

    public boolean isColumnValid(String columnId) {
        if(getChildCount()>0){
            return true;
        }
        return idModelMap.containsKey(columnId) ? idModelMap.get(columnId).isValid() : true;
    }
    
    public boolean isInsertedOrRemoved() {
        return inserted || removed;
    }

    
    
    public Element getChild(int index){
        return childElements.get(index);
    }
    
    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public int getChildCount(){
        if(childElements == null){
            return 0;
        }
        return childElements.size();
    }
    
    public void addChildElement(Element element){
        childElements.add(element);
    }
    
    public String toString(){
        //hack because TreeTableCellRenderer uses the toString of the row for display
        Object value = getCurrentValue();
        if(value == null){
            return null;
        }
        Object obj =  getValueAt(0);
        if(obj == null){
            return "";
        }
        return obj.toString();
    }
}

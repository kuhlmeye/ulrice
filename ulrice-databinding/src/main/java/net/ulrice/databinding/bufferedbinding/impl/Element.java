package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.ulrice.databinding.IFBinding;
import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.bufferedbinding.impl.ColumnDefinition.ColumnType;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;
import net.ulrice.databinding.validation.UniqueKeyConstraintError;
import net.ulrice.databinding.validation.ValidationError;
import net.ulrice.databinding.validation.ValidationResult;

/**
 * The element of the list attribute model. It manages the models for all
 * attributes of a data row.
 *
 * @author christof
 */
public class Element {

	private long uniqueId;
	private IFElementInternalAM<? extends Object>[] modelList;

	private Object originalValue;
	private ValidationResult validationResult = null;

	private boolean originalValueDirty = false;
	private boolean originalValueValid = true;
	private boolean readOnly;
	private boolean dirty;
	private boolean valid;
	private boolean inserted;
	private boolean removed;

	private TableAM tableAM;
	private List<Element> childElements;
	private Element parent;

	private IFValidator modelValidator = new IFValidator() {

        @Override
        public ValidationResult isValid(IFBinding bindingId, Object attribute, Object rawAttribute) {
            return validationResult;
        }

        @Override
        public ValidationResult getLastValidationErrors() {
            return validationResult;
        }

        @Override
        public void clearValidationErrors() {
            validationResult = null;
        }
    };

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
	public Element(TableAM tableAM, long uniqueId, Object valueObject, boolean readOnly, boolean dirty,
			boolean valid, boolean inserted) {
		this.originalValueDirty = dirty;
		this.originalValueValid = valid;
		this.tableAM = tableAM;
		this.uniqueId = uniqueId;
		this.modelList = new IFElementInternalAM[tableAM.getColumns() != null ? tableAM.getColumns().size() : 0];
		if(readOnly) {
			this.originalValue = valueObject;
		} else {
			this.originalValue = tableAM.cloneObject(valueObject);
		}
		this.readOnly = readOnly;

		this.dirty = false;
		this.valid = true;

		readObject();
	}

	/**
	 * Changes the readonly state of a column by column id
	 */
	public void setReadOnly(String columnId, boolean readOnly) {
		if (!tableAM.getIdModelIndexMap().containsKey(columnId)) {
			throw new IllegalStateException("Unknown column id: " + columnId);
		}
		modelList[tableAM.getIdModelIndexMap().get(columnId)].setReadOnly(readOnly);
	}

	/**
	 * Changes the readonly state of a column by column index
	 */
	public void setReadOnly(int columnIndex, boolean readOnly) {
		if (columnIndex < 0 || columnIndex >= modelList.length) {
			throw new IndexOutOfBoundsException("ColumnIndex: " + columnIndex + ", Size: " + modelList.length);
		}
		modelList[columnIndex].setReadOnly(readOnly);
	}

	/**
	 * Returns, if a cell is readonly.
	 *
	 * @param columnIndex
	 *            The index of the column.
	 * @return True, if the value is readonly. False otherwise.
	 */
	public boolean isReadOnly(int columnIndex) {

		if (readOnly || columnIndex >= tableAM.getColumns().size()) {
			return true;
		}

		ColumnDefinition<?> columnDefinition = tableAM.getColumns().get(columnIndex);

		ColumnType type = columnDefinition.getColumnType();
		switch (type) {
		case Editable:
			return modelList[columnIndex].isReadOnly();
		case ReadOnly:
			return true;
		case NewEditable:
			if (tableAM.isNew(this)) {
				return modelList[columnIndex].isReadOnly();
			} else {
				return true;
			}
		}

		return true;
	}

	/**
	 * Returns the cell value.
	 *
	 * @param modelColumnIndex
	 *            The index of the column
	 * @return The cell value as an object.
	 * @throws IndexOutOfBoundsException
	 *             If the column index is not a valid index.
	 */
	public Object getValueAt(int modelColumnIndex) {
		if (modelColumnIndex < 0 || modelColumnIndex >= modelList.length) {
			throw new IndexOutOfBoundsException("ColumnIndex: " + modelColumnIndex + ", Size: " + modelList.length);
		}
		return modelList[modelColumnIndex].getCurrentValue();
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
		if (!tableAM.getIdModelIndexMap().containsKey(columnId)) {
			throw new IllegalStateException("Unknown column id: " + columnId);
		}
		return modelList[tableAM.getIdModelIndexMap().get(columnId)].getCurrentValue();
	}

	/**
	 * Returns the original value of a cell by column index.
	 */
	public Object getOriginalValueAt(int columnIndex) {
		if (columnIndex < 0 || columnIndex >= modelList.length) {
			throw new IndexOutOfBoundsException("ColumnIndex: " + columnIndex + ", Size: " + modelList.length);
		}
		return modelList[columnIndex].getOriginalValue();
	}

	/**
	 * Returns the original value of a cell by column identifier.
	 */
	public Object getOriginalValueAt(String columnId) {
		if (!tableAM.getIdModelIndexMap().containsKey(columnId)) {
			throw new IllegalStateException("Unknown column id: " + columnId);
		}
		return modelList[tableAM.getIdModelIndexMap().get(columnId)].getOriginalValue();
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
		IFElementInternalAM<?> model = modelList[columnIndex];
		setValue(model, tableAM.getColumns().get(columnIndex).getId(), aValue);

		if (tableAM.isVirtualTreeNodes() && childElements != null) {
			for (Element elem : childElements) {
			    if(!elem.isReadOnly(columnIndex)){
			        elem.setValueAt(columnIndex, aValue);
			    }
			}
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
		IFElementInternalAM<?> model = modelList[tableAM.getIdModelIndexMap().get(columnId)];
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
	private void setValue(IFElementInternalAM<?> model, String columnId, Object aValue) {
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

        if (childElements != null && childElements.size() > 0 && tableAM.isVirtualTreeNodes()) {
            valid = true;
            dirty = false;
			for (Element child : childElements) {
				if (!child.isValid()) {
					valid = false;
				}
				if (child.isDirty()) {
					dirty = true;
				}
			}

		} else if (modelList != null) {
			dirty = false;
			valid = validationResult == null || validationResult.isValid();
			for (IFElementInternalAM<?> model : modelList) {
				dirty |= model.isDirty();
				valid &= model.isValid();
			}
		}

		dirty |= originalValueDirty;
		valid &= originalValueValid;

		if (oldDirty != dirty || oldValid != valid) {

			if (parent != null) {
				parent.updateState();
			}

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
		tableAM.handleElementStateChange(this);
	}

	/**
	 * @param aValue
	 * @param columnId
	 * @param newValue
	 * @param oldValue
	 */
	private void fireValueChanged(String columnId) {
		tableAM.handleElementDataChanged(this, columnId);
	}

	/**
	 * Write the object managed by the element in the value object.
	 *
	 * @return The value object
	 */
	@SuppressWarnings("unchecked")
	public Object writeObject() {
		if (modelList != null) {
			for (int i = 0; i < tableAM.getColumns().size(); i++) {
				IFElementInternalAM attributeModel = modelList[i];
				if (!attributeModel.isReadOnly()) {

					IFDynamicModelValueAccessor dataAccessor = tableAM.getColumns().get(i).getDataAccessor();

					IFValueConverter valueConverter = tableAM.getColumns().get(i).getValueConverter();
					Object value = attributeModel.directWrite();
					Object converted = (valueConverter != null ? valueConverter.viewToModel(value,
							attributeModel.getAttributeInfo()) : value);
					dataAccessor.setValue(getOriginalValue(), converted);
				}
			}
		}
		return getOriginalValue();
	}

	/**
	 * Sets the current value of this element.
	 */
	public void setCurrentValue(Object currentValue) {
		setCurrentValue(currentValue, false, true);
	}

	/**
	 * Sets the current value of this element with possibility to set dirty and
	 * valid state from outside. This could be used, if the internal validators
	 * are not enough to determine the correct state.
	 */
	public void setCurrentValue(Object currentValue, boolean dirty, boolean valid) {
		setCurrentValue(currentValue, dirty, valid, false);
	}

	public void setCurrentValue(Object currentValue, boolean dirty, boolean valid, boolean omitReadOnly) {
		this.originalValueDirty = dirty;
		this.originalValueValid = valid;
		if(readOnly) {
			this.originalValue = currentValue;
		} else {
			this.originalValue = tableAM.cloneObject(currentValue);
		}

		clearElementValidationErrors();

		if (modelList != null) {
			for (int i = 0; i < tableAM.getColumns().size(); i++) {
				if (isReadOnly(i) && omitReadOnly) {
					continue;
				}

				IFElementInternalAM model = modelList[i];
				IFDynamicModelValueAccessor dataAccessor = tableAM.getColumns().get(i).getDataAccessor();

				IFValueConverter valueConverter = tableAM.getColumns().get(i).getValueConverter();
				Object value = dataAccessor.getValue(currentValue);
				Object converted = (valueConverter != null ?valueConverter.modelToView(value, model.getAttributeInfo()) : value);
				model.setValue(converted);
			}
			fireValueChanged(null);
			updateState();
		}
	}

	/**
	 * Mark the original value as dirty.
	 */
	public void setOriginalValueDirty(boolean dirty) {
		originalValueDirty = dirty;
		updateState();
	}

	/**
	 * Return the current value of this element.
	 */
	public Object getCurrentValue() {
		Object result = tableAM.cloneObject(getOriginalValue());
		if (modelList != null) {
			for (int i = 0; i < tableAM.getColumns().size(); i++) {
				IFElementInternalAM attributeModel = modelList[i];
				if (!attributeModel.isReadOnly()) {
					IFDynamicModelValueAccessor dataAccessor = tableAM.getColumns().get(i).getDataAccessor();
					IFAttributeInfo attributeInfo = tableAM.getColumns().get(i).getAttributeInfo();
					IFValueConverter valueConverter = tableAM.getColumns().get(i).getValueConverter();
					Object value = attributeModel.getCurrentValue();
					Object converted = (valueConverter != null ? valueConverter.viewToModel(value, attributeInfo) : value);
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
	    synchronized (modelList) {
	        modelList = new IFElementInternalAM[tableAM.getColumns() != null ? tableAM.getColumns().size() : 0];
        }
		originalValueDirty = false;
		originalValueValid = true;
		readAdditionalColumns(tableAM.getColumns(), true);
	}

	/**
	 * Read the list of additional columns given to this method.
	 */
	@SuppressWarnings("unchecked")
	public void readAdditionalColumns(List<ColumnDefinition<? extends Object>> columns, boolean updateState) {
		if (columns == null) {
			return;
		}

        synchronized (modelList) {
            for (int i = 0; i < columns.size(); i++) {
            	ColumnDefinition< ? extends Object> column = columns.get(i);

            	IFElementInternalAM attributeModel = null;
            	if(column.isUseListAM()) {
                    attributeModel = column.createListAM();
            	} else {
                    attributeModel = column.createLightAM();
            	}

				attributeModel.addValidator(modelValidator);
                attributeModel.setReadOnly(column.getColumnType().equals(ColumnType.ReadOnly));
                modelList[i] = attributeModel;
                if (tableAM.getIdModelIndexMap() == null) {
                    tableAM.setIdModelIndexMap(new HashMap<String, Integer>(columns.size()));
                }
                if (tableAM.getIdModelIndexMap().size() < columns.size()) {
                    tableAM.getIdModelIndexMap().put(column.getId(), i);
                }

                if (getOriginalValue() != null) {
                    final Object value = column.getDataAccessor().getValue(getOriginalValue());
                    final Object converted = (column.getValueConverter() != null ? column.getValueConverter().modelToView(value, attributeModel.getAttributeInfo()) : value);
                    attributeModel.directRead(converted);
                }
            }

            if (updateState) {
                updateState();
            }
        }
	}

	public int getModelListSize(){
        return modelList.length;
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
	public long getUniqueId() {
		return uniqueId;
	}

	/**
	 * Returns the attribute model of a cell.
	 */
	protected IFElementInternalAM getCellAtributeModel(int columnIndex) {
		return modelList[columnIndex];
	}

	/**
	 * True, if this element is dirty.
	 */
	public boolean isDirty() {
		return dirty || isInsertedOrRemoved();
	}

	/**
	 * True, if this element is valid.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Returns the list of validation errors for this element
	 */
	public List<ValidationError> getValidationErrors() {
		List<ValidationError> errors = null;
		if(validationResult == null) {
			errors = new ArrayList<ValidationError>();
		} else {
			errors = new ArrayList<ValidationError>(validationResult.getValidationErrors());
		}

		if (modelList != null) {
			for (IFElementInternalAM<?> model : modelList) {
				if (model.getValidationResult() != null) {
					errors.addAll(model.getValidationResult().getValidationErrors());
				}
			}
		}
		return errors;
	}

	/**
	 * Returns a string presentation of the validation errors of this element
	 */
	public List<String> getValidationFailures() {

		List<String> result = new ArrayList<String>();
		if (modelList != null) {
			for (IFElementInternalAM<?> attributeModel : modelList) {
				result.addAll(attributeModel.getValidationFailures());
			}
		}
        if (validationResult != null) {
            for (ValidationError elementError : validationResult.getValidationErrors()) {
                result.add(elementError.getMessage());
            }
		}
		return result;
	}

	/**
	 * Returns a string presentation of the validation errors of a certain column
	 */
	public List<String> getValidationFailures(String columnId) {
		List<String> errors = new ArrayList<String>();

		if (tableAM.getIdModelIndexMap().containsKey(columnId)) {
			IFElementInternalAM<?> model = modelList[tableAM.getIdModelIndexMap().get(columnId)];
			if (model.getValidationResult() != null) {
				errors.addAll(model.getValidationFailures());
			}
		}
        if (validationResult != null) {
            for (ValidationError elementError : validationResult.getValidationErrors()) {
                errors.add(elementError.getMessage());
            }
		}
		return errors;
	}

	/**
	 * Add a column validation error for a cell from outside.
	 */
	public void addColumnValidationError(String columnId, String message) {
		IFElementInternalAM<? extends Object> genericAM = modelList[tableAM.getIdModelIndexMap().get(columnId)];
		if (genericAM != null) {
			genericAM.addExternalValidationError(new ValidationError(genericAM, message, null));
		}
		updateState();
	}

	/**
	 * Remove all external validation errors set in this object.
	 */
	public void removeExternalValidationErrors() {
		for (IFElementInternalAM<?> model : modelList) {
			if (model != null) {
				model.clearExternalValidationErrors();
			}
		}
		updateState();
	}

	/**
	 * Add an element validation error to this element. An element validation error is a global
	 * validation error for the whole element that could not be assigned to a single cell.
	 */
	public void addElementValidationError(ValidationError validationError) {
		if(validationResult == null) {
			validationResult = new ValidationResult();
		}
		validationResult.addValidationError(validationError);
		if (modelList != null) {
			for (IFElementInternalAM<?> model : modelList) {
				model.recalculateState();
			}
		}
		updateState();
	}

	/**
	 * Put a unique key contstraint error to the list of element validation errors.
	 */
	public void putUniqueKeyConstraintError(UniqueKeyConstraintError uniqueKeyConstraintError) {
		removeUniqueKeyConstraintErrors();
		addElementValidationError(uniqueKeyConstraintError);
	}

	/**
	 * Remove all unique key contraint errors.
	 */
	public void removeUniqueKeyConstraintErrors() {
		if(validationResult != null) {
			validationResult.removeUniqueKeyConstraintErrors();
		}
	}

	/**
	 * Remove an element validation error from this element.
	 */
	public void removeElementValidationError(ValidationError validationError) {
		if(validationResult != null) {
			validationResult.removeValidationError(validationError);
			if (modelList != null) {
				for (IFElementInternalAM<?> model : modelList) {
					model.recalculateState();
				}
			}
			updateState();
		}
	}

	/**
	 * Return the current validation result of this element.
	 */
	public ValidationResult getElementValidationErrors() {
		return validationResult;
	}

	/**
	 * Clear all element validation errors.
	 */
	public void clearElementValidationErrors() {
		validationResult = null;
		if (modelList != null) {
		    //synchronized (modelList) {
		        for (IFElementInternalAM<?> model : modelList) {
	                model.recalculateState();
	            }
            //}

		}
		updateState();
		if (parent != null) {
			parent.clearElementValidationErrors();
		}
	}

	/**
	 * Return, if the original value of this element is dirty.
	 */
	public boolean isOriginalValueDirty() {
		if (getChildCount() > 0) {
			for (Element child : childElements) {
				if (child.isOriginalValueDirty()) {
					return true;
				}
			}
			return false;
		}
		return originalValueDirty;
	}

	/**
	 * Return, if the oriinal value of this element is valid.
	 */
	public boolean isOriginalValueValid() {
		if (getChildCount() > 0) {
			for (Element child : childElements) {
				if (!child.isOriginalValueValid()) {
					return false;
				}
			}
			return true;
		}
		return originalValueValid;
	}

	/**
	 * Returns, if a columnn is valid.
	 */
	public boolean isColumnValid(int column) {
		if (getChildCount() > 0) {
			for (Element child : childElements) {
				if (!child.isColumnValid(column)) {
					return false;
				}
			}
			return true;
		}
		return modelList[column].isValid();
	}

	/**
	 * Returns, if a column is dirty.
	 */
	public boolean isColumnDirty(int column) {
		if (getChildCount() > 0) {
			for (Element child : childElements) {
				if (child.isColumnDirty(column)) {
					return true;
				}
			}
			return false;
		}
		return modelList[column].isDirty();
	}

	/**
	 * Returns, if a column identified by the column id is dirty
	 */
	public boolean isColumnDirty(String columnId) {
        if (getChildCount() > 0 && tableAM.isVirtualTreeNodes()) {
			for (Element child : childElements) {
				if (child.isColumnDirty(columnId)) {
					return true;
				}
			}
			return false;
		}
		return tableAM.getIdModelIndexMap().containsKey(columnId) ? modelList[tableAM.getIdModelIndexMap().get(columnId)].isDirty() : false;
	}

	/**
	 * Returns, if a column identified by the column id is valid
	 */
	public boolean isColumnValid(String columnId) {
		if (getChildCount() > 0) {
			for (Element child : childElements) {
				if (!child.isColumnValid(columnId)) {
					return false;
				}
			}
			return true;
		}
		return tableAM.getIdModelIndexMap().containsKey(columnId) ? modelList[tableAM.getIdModelIndexMap().get(columnId)].isValid() : true;
	}

	/**
	 * Returns true, if this element was newly inserted or removed.
	 */
	public boolean isInsertedOrRemoved() {
		if (getChildCount() > 0) {
			for (Element child : childElements) {
				if (child.inserted || child.removed) {
					return true;
				}
			}
			return false;
		}
		return inserted || removed;
	}

	/**
	 * Removes a child element from this element.
	 */
	public boolean removeChild(Element element) {
		if (getChildCount() > 0) {
			if (childElements.remove(element)) {
				return true;
			}
			for (Element child : childElements) {
				if (child.removeChild(element)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return the nth child of this element.
	 */
	public Element getChild(int index) {
		return childElements.get(index);
	}

	public void removeChild(String path) {
		if (getParent() != null && getParent().tableAM.getIdModelIndexMap().containsKey(path)) {
			IFElementInternalAM genericAM = getParent().modelList[tableAM.getIdModelIndexMap().get(path)];
			if (genericAM instanceof ListAM) {
				((ListAM) genericAM).remove(getCurrentValue());
			}
		}
	}

	public void addLeafNodes(List<Element> leafNodes) {
		if (getChildCount() == 0) {
			leafNodes.add(this);
		} else {
			for (Element element : childElements) {
				element.addLeafNodes(leafNodes);
			}
		}
	}

	/**
	 * Returns true, if this element is newly inserted.
	 */
	public boolean isInserted() {
		return inserted;
	}

	/**
	 * Changes the inserted flag. => Simulation of newly inserted object.
	 */
	public void setInserted(boolean inserted) {
		this.inserted = inserted;
	}

	/**
	 * Returns true, if this element is marked as to be removed.
	 */
	public boolean isRemoved() {
		return removed;
	}

	/**
	 * Changes the removed marker
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	/**
	 * Returns the number of childs of this element or 0 if there are no childs.
	 */
	public int getChildCount() {
		if (childElements == null) {
			return 0;
		}
		return childElements.size();
	}

	/**
	 * Adds a child to this element.
	 */
	public void addChildElement(Element element) {
	    if (childElements == null) {
	        childElements = new ArrayList<Element>();
	    }
		childElements.add(element);
		element.setParent(this);
	}

    public List<Element> getChilds() {
        if (childElements == null) {
            return Collections.<Element> emptyList();
        }
        return childElements;
    }

	/**
	 * Returns the parent element or null, if this is a root element.
	 */
	public Element getParent() {
		return parent;
	}

	public void setParent(Element parent) {
		this.parent = parent;
	}

	public Element getChildByCurrentValue(Object object) {
		if (getChildCount() > 0 && childElements != null) {
			for (Element child : childElements) {
				if (child.getCurrentValue().equals(object)) {
					return child;
				}
				Element found = child.getChildByCurrentValue(object);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		// hack because TreeTableCellRenderer uses the toString of the row for
		// display
		Object value = getCurrentValue();
		if (value == null) {
			return null;
		}
		Object obj = getValueAt(0);
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}
}

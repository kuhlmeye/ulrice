package net.ulrice.databinding.bufferedbinding.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.ulrice.databinding.ErrorHandler;
import net.ulrice.databinding.bufferedbinding.IFAttributeModel;
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.validation.ValidationError;

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

	/** The event listeners. */
	private EventListenerList listenerList = new EventListenerList();

	/** Flag, if this element is editable. */
	private boolean readOnly;

	private boolean dirty;
	private boolean valid;

	private AbstractTableAM tableAM;

	/**
	 * Creates a new element.
	 * 
	 * @param tableAM
	 * 
	 * @param uniqueId
	 *            The unique identifier.
	 * @param columns
	 *            The list of column definitions
	 * @param valueObject
	 *            The value.
	 * @param editable
	 *            True, if this element should be readonly.
	 */
	public Element(AbstractTableAM tableAM, String uniqueId, List<ColumnDefinition<? extends Object>> columns, Object valueObject,
			boolean readOnly) {
		this.tableAM = tableAM;
		this.uniqueId = uniqueId;
		this.modelList = new ArrayList<GenericAM<? extends Object>>();
		this.idModelMap = new HashMap<String, GenericAM<? extends Object>>();
		this.originalValue = cloneObject(valueObject);
		this.columns = columns;
		this.readOnly = readOnly;

		this.dirty = false;
		this.valid = true;

		readObject();
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
		if (!columnDefinition.isReadOnly()) {
			return modelList.get(columnIndex).isReadOnly();
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
			throw new IndexOutOfBoundsException("ColumnIndex: " + columnIndex + ", Size: " + modelList.size());
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
		boolean oldValid = valid;
		boolean oldDirty = dirty;

		if (modelList != null) {
			dirty = false;
			valid = true;
			for (IFAttributeModel<?> model : modelList) {
				dirty |= model.isDirty();
				valid &= model.isValid();
			}
		}
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
		tableAM.stateChanged(this);
	}

	/**
	 * @param aValue
	 * @param columnId
	 * @param newValue
	 * @param oldValue
	 */
	private void fireValueChanged(String columnId, Object newValue, Object oldValue) {
		tableAM.dataChanged(this, columnId, newValue, oldValue);
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

					IFDynamicModelValueAccessor dataAccessor = columns.get(i).getDataAccessor();

					Object value = attributeModel.directWrite();
					Object converted = (attributeModel.getValueConverter() != null ? attributeModel.getValueConverter().viewToModel(value)
							: value);
					dataAccessor.setValue(getOriginalValue(), converted);
				}
			}
		}
		return getOriginalValue();
	}

	public void setCurrentValue(Object currentValue) {
		if (modelList != null) {
			for (int i = 0; i < modelList.size(); i++) {
				GenericAM attributeModel = modelList.get(i);
				IFDynamicModelValueAccessor dataAccessor = columns.get(i).getDataAccessor();

				Object value = dataAccessor.getValue(currentValue);
				Object converted = (attributeModel.getValueConverter() != null ? attributeModel.getValueConverter().modelToView(value)
						: value);
				setValueAt(columns.get(i).getId(), converted);
			}
		}
	}

	public Object getCurrentValue() {
		if (modelList != null) {
			for (int i = 0; i < modelList.size(); i++) {
				GenericAM attributeModel = modelList.get(i);
				if (!attributeModel.isReadOnly()) {
					IFDynamicModelValueAccessor dataAccessor = columns.get(i).getDataAccessor();

					Object value = attributeModel.getCurrentValue();
					Object converted = (attributeModel.getValueConverter() != null ? attributeModel.getValueConverter().viewToModel(value)
							: value);
					dataAccessor.setValue(getOriginalValue(), converted);
				}
			}
		}
		return cloneObject(getOriginalValue());
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
				attributeModel.setReadOnly(column.isReadOnly());
				modelList.add(attributeModel);
				idModelMap.put(attributeModel.getId(), attributeModel);

				Object value = column.getDataAccessor().getValue(getOriginalValue());
				Object converted = (column.getValueConverter() != null ? column.getValueConverter().modelToView(value) : value);
				attributeModel.directRead(converted);
			}
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
		return dirty;
	}

	public boolean isValid() {
		return valid;
	}

	public List<ValidationError> getValidationErrors() {
		List<ValidationError> errors = new ArrayList<ValidationError>();
		if (modelList != null) {
			for (GenericAM<?> model : modelList) {
				if (model.getValidationResult() != null) {
					errors.addAll(model.getValidationResult().getValidationErrors());
				}
			}
		}
		return errors;
	}

	public List<String> getValidationFailures() {
		List<String> errors = new ArrayList<String>();
		if (modelList != null) {
			for (GenericAM<?> model : modelList) {
				if (model.getValidationResult() != null) {
					errors.addAll(model.getValidationFailures());
				}
			}
		}
		return errors;
	}

	private Object cloneObject(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof Cloneable) {
			Class<?> clazz = obj.getClass();
			Method m;
			try {
				m = clazz.getMethod("clone", (Class[]) null);
			} catch (NoSuchMethodException ex) {
				throw new NoSuchMethodError(ex.getMessage());
			}
			try {
				return m.invoke(obj, (Object[]) null);
			} catch (InvocationTargetException ex) {
				ErrorHandler.handle(ex);
			} catch (IllegalAccessException ex) {
				ErrorHandler.handle(ex);
			}
		} else if (obj instanceof Serializable) {

			ByteArrayOutputStream bytes = new ByteArrayOutputStream() {

				public synchronized byte[] toByteArray() {
					return buf;
				}
			};

			try {
				ObjectOutputStream out = new ObjectOutputStream(bytes);
				out.writeObject(obj);
				out.close();

				ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
				Object objCopy = in.readObject();
				return objCopy;
			} catch (IOException e) {
				ErrorHandler.handle(e);
			} catch (ClassNotFoundException e) {
				ErrorHandler.handle(e);
			}

		} else {
			Object clone = null;
			try {
				clone = obj.getClass().newInstance();
			} catch (InstantiationException e) {
				ErrorHandler.handle(e);
			} catch (IllegalAccessException e) {
				ErrorHandler.handle(e);
			}

			for (Class objClass = obj.getClass(); !objClass.equals(Object.class); objClass = objClass.getSuperclass()) {
				Field[] fields = objClass.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					fields[i].setAccessible(true);
					try {
						fields[i].set(clone, fields[i].get(obj));
					} catch (IllegalArgumentException e) {
						ErrorHandler.handle(e);
					} catch (IllegalAccessException e) {
						ErrorHandler.handle(e);
					}
				}
			}
			return clone;
		}
		return null;
	}
}

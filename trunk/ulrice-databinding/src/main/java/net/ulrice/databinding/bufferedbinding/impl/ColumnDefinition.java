package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.ulrice.databinding.bufferedbinding.IFAttributeInfo;
import net.ulrice.databinding.converter.IFValueConverter;
import net.ulrice.databinding.modelaccess.IFDynamicModelValueAccessor;
import net.ulrice.databinding.validation.IFValidator;

/**
 * @author christof
 * 
 */
public class ColumnDefinition<T extends Object> {

    private String id;
    private IFDynamicModelValueAccessor dataAccessor;
    private Class<T> columnClass;
    private FilterMode filterMode;
    private IFValueConverter valueConverter;
    private List<IFValidator> validators = new ArrayList<IFValidator>();
    private IFAttributeInfo attributeInfo;
    private String columnName;
    private String columnTooltip;
	private boolean useAutoValueConverter = true;
	private boolean useValueRange = false;
	private List<T> valueRange;
	private Comparator<T> comparator;
	private boolean fixedColumn;
	
	private TableCellEditor cellEditor;
	private TableCellRenderer cellRenderer;
	
	private EventListenerList eventListeners = new EventListenerList();
	
	private ColumnColorOverride columnColorOverride;
	
	private ColumnType columnType = ColumnType.Editable;
	
	public enum ColumnType {
		Editable,
		ReadOnly,
		NewEditable
	}

	
	   
    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        
        setFilterMode(columnClass);
    }

    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, ColumnType columnType) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = id;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.columnType = columnType;
        
        setFilterMode(columnClass);
    }
	
    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass) {
        this.id = dataAccessor != null ? dataAccessor.getAttributeId() : columnName;
        this.columnName = columnName;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        
        setFilterMode(columnClass);
    }
    
    public ColumnDefinition(IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this(dataAccessor.getAttributeId(), dataAccessor, columnClass, filterMode);
    }

    
    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode) {
        this(columnName, dataAccessor, columnClass, filterMode, ColumnType.Editable);
    }
    
    public ColumnDefinition(String columnName, IFDynamicModelValueAccessor dataAccessor, Class<T> columnClass, FilterMode filterMode, ColumnType columnType) {
        this.id = dataAccessor.getAttributeId();
        this.columnName = columnName;
        this.dataAccessor = dataAccessor;
        this.columnClass = columnClass;
        this.filterMode = filterMode;
        this.columnType = columnType;        
    }

    public GenericAM<T> createAM() {
        GenericAM<T> genericAM = new GenericAM<T>(id, attributeInfo);
        genericAM.setReadOnly(getColumnType().equals(ColumnType.ReadOnly));        
        genericAM.setValueConverter(getValueConverter());
        if(getValidators() != null && !getValidators().isEmpty()) {
            for (IFValidator validator : getValidators()) {
                genericAM.addValidator(validator);
            }
        }
		return genericAM;
    }

    private void setFilterMode(Class<T> columnClass) {
        if (Number.class.isAssignableFrom(columnClass)) {
            setFilterMode(FilterMode.Numeric);
        }
        else if (Boolean.class.isAssignableFrom(columnClass)) {
            setFilterMode(FilterMode.Boolean);
        }
        else if (Enum.class.isAssignableFrom(columnClass)) {
            setFilterMode(FilterMode.ComboBox);
        }
        else {
            setFilterMode(FilterMode.RegEx);
        }
    }
    
    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
        fireFilterModeChanged();
    }

    /**
     * @param columnIndex
     * @return
     */
    public Class<?> getColumnClass() {
        return columnClass;
    }



    /**
     * @return the dataAccessor
     */
    public IFDynamicModelValueAccessor getDataAccessor() {
        return dataAccessor;
    }

    /**
     * @param dataAccessor the dataAccessor to set
     */
    public void setDataAccessor(IFDynamicModelValueAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return getId().equals(((ColumnDefinition<?>) obj).getId());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getColumnName();
    }

    /**
     * @return the filterMode
     */
    public FilterMode getFilterMode() {
        return filterMode;
    }

    
	public IFValueConverter getValueConverter() {
		return valueConverter;
	}

	public void setValueConverter(IFValueConverter valueConverter) {
		this.valueConverter = valueConverter;
	}


	public List<IFValidator> getValidators() {
		return validators;
	}
	
	public void addValidator(IFValidator validator) {
		validators.add(validator);
	}
	
    public boolean isUseAutoValueConverter() {
    	return useAutoValueConverter;
    }
        
    public void setUseAutoValueConverter(boolean useAutoValueConverter) {
		this.useAutoValueConverter = useAutoValueConverter;
	}
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}
    
    public ColumnType getColumnType() {
		return columnType;
	}
    
    /**
     * @param columnIndex
     * @return
     */
    public String getColumnName() {
        return columnName;
    }
    
    public boolean isUseValueRange() {
        return useValueRange;
    }
    
    public void setUseValueRange(boolean useValueRange) {
        this.useValueRange = useValueRange;
    }
    
    public List<T> getValueRange() {
        return valueRange;
    }
    
    public void setValueRange(List<T> valueRange) {
        this.valueRange = valueRange;
        fireValueRangeChanged();
    }
    
    private void fireValueRangeChanged() {
        ColumnDefinitionChangedListener[] listeners = eventListeners.getListeners(ColumnDefinitionChangedListener.class);
        if(listeners != null) {
            for(ColumnDefinitionChangedListener listener : listeners) {
                listener.valueRangeChanged(this);
            }
        }
    }
    
    private void fireFilterModeChanged() {
        ColumnDefinitionChangedListener[] listeners = eventListeners.getListeners(ColumnDefinitionChangedListener.class);
        if(listeners != null) {
            for(ColumnDefinitionChangedListener listener : listeners) {
                listener.filterModeChanged(this);
            }
        }
    }

    public void addChangeListener(ColumnDefinitionChangedListener listener) {
        eventListeners.add(ColumnDefinitionChangedListener.class, listener);
    }

    public void removeChangeListener(ColumnDefinitionChangedListener listener) {
        eventListeners.remove(ColumnDefinitionChangedListener.class, listener);
    }

    public TableCellEditor getCellEditor() {
        return cellEditor;
    }
    
    public void setCellEditor(TableCellEditor cellEditor) {
        this.cellEditor = cellEditor;
    }

    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }
    

    public void setCellRenderer(TableCellRenderer cellRenderer) {
        this.cellRenderer = cellRenderer;
    }

    public void setAttributeInfo(IFAttributeInfo attributeInfo) {
        this.attributeInfo = attributeInfo;
    }
    
    public IFAttributeInfo getAttributeInfo() {
        return attributeInfo;
    }
    
    public void setColumnTooltip(String columnTooltip) {
        this.columnTooltip = columnTooltip;
    }
    
    public String getColumnTooltip() {
        return columnTooltip;
    }
    
    public Comparator<T> getComparator() {
        return comparator;
    }
    
    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean isFixedColumn() {
        return fixedColumn;
    }

    public void setFixedColumn(boolean fixedColumn) {
        this.fixedColumn = fixedColumn;
    }

    public ColumnColorOverride getColumnColorOverride() {
        return columnColorOverride;
    }

    public void setColumnColorOverride(ColumnColorOverride columnColorOverride) {
        this.columnColorOverride = columnColorOverride;
    }

 
    
    
}

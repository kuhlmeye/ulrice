package net.ulrice.databinding.bufferedbinding.impl;

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
    private IFValidator validator;
    private String columnName;
	private boolean useAutoValueConverter = true;
	
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
        this.id = dataAccessor.getAttributeId();
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
        GenericAM<T> genericAM = new GenericAM<T>(id);
        genericAM.setReadOnly(getColumnType().equals(ColumnType.ReadOnly));        
        genericAM.setValueConverter(getValueConverter());
        if(getValidator() != null) {
            genericAM.addValidator(getValidator());
        }
		return genericAM;
    }

    private void setFilterMode(Class<T> columnClass) {
        if (Number.class.isAssignableFrom(columnClass)) {
            this.filterMode = FilterMode.Numeric;
        }
        else if (Boolean.class.isAssignableFrom(columnClass)) {
            this.filterMode = FilterMode.Boolean;
        }
        else if (Enum.class.isAssignableFrom(columnClass)) {
            this.filterMode = FilterMode.Enum;
        }
        else {
            this.filterMode = FilterMode.RegEx;
        }
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


	public IFValidator getValidator() {
		return validator;
	}
	
	public void setValidator(IFValidator validator) {
		this.validator = validator;
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
}
